package pe.edu.upeu.geovision_backend.service.ventas.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoPendienteResponseDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoRegistroRequestDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoResponseDTO;
import pe.edu.upeu.geovision_backend.entity.ventas.Pago;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.entity.ventas.Venta;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.MetodoPagoEnum;
import pe.edu.upeu.geovision_backend.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.mapper.ventas.PagoMapper;
import pe.edu.upeu.geovision_backend.repository.ventas.PagoRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.VentaRepository;
import pe.edu.upeu.geovision_backend.service.ventas.PagoPendienteService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoPendienteServiceImpl implements PagoPendienteService {

    // >>>> CONSTANTS
    private static final Logger log = LoggerFactory.getLogger(PagoPendienteServiceImpl.class);

    // >>>> DEPENDENCIES
    private final VentaRepository ventaRepository;
    private final PagoRepository pagoRepository;

    // >>>> QUERIES
    @Override
    @Transactional(readOnly = true)
    public List<PagoPendienteResponseDTO> listarVentasConPagosPendientes(String termino) {
        log.info("Listando ventas con saldo pendiente. Filtro de búsqueda: '{}'", termino);
        List<Venta> ventasPendientes = ventaRepository.findVentasConPagosPendientes();

        String termClean = (termino != null && !termino.trim().isEmpty()) ? termino.trim().toLowerCase() : null;

        return ventasPendientes.stream()
                .filter(v -> {
                    if (termClean == null) {
                        return true;
                    }
                    boolean matchComprobante = v.getNumeroComprobante() != null &&
                            v.getNumeroComprobante().toLowerCase().contains(termClean);

                    boolean matchCliente = false;
                    boolean matchDocumento = false;
                    if (v.getPaciente() != null && v.getPaciente().getPersona() != null) {
                        Persona p = v.getPaciente().getPersona();
                        String fullName = (p.getNombres() + " " + p.getApellidos()).toLowerCase();
                        matchCliente = fullName.contains(termClean);
                        matchDocumento = p.getNumeroDocumento() != null && p.getNumeroDocumento().contains(termClean);
                    }
                    return matchComprobante || matchCliente || matchDocumento;
                }
                )
                .map(v -> {
                    BigDecimal totalPagado = pagoRepository.sumMontoPagadoByVentaId(v.getId());
                    if (totalPagado == null) {
                        totalPagado = BigDecimal.ZERO;
                    }
                    BigDecimal saldo = v.getTotal().subtract(totalPagado);
                    List<Pago> historial = pagoRepository.findByVentaId(v.getId());
                    return PagoMapper.toPagoPendienteResponseDTO(v, totalPagado, saldo, historial);
                }
                )
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagoPendienteResponseDTO obtenerSaldoPendientePorVenta(Long idVenta) {
        log.info("Consultando saldo pendiente para la venta ID: {}", idVenta);
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RecursosNoEncontradosException("Venta no encontrada con ID: " + idVenta));

        BigDecimal totalPagado = pagoRepository.sumMontoPagadoByVentaId(idVenta);
        if (totalPagado == null) {
            totalPagado = BigDecimal.ZERO;
        }
        BigDecimal saldoPendiente = venta.getTotal().subtract(totalPagado);
        List<Pago> historial = pagoRepository.findByVentaId(idVenta);

        return PagoMapper.toPagoPendienteResponseDTO(venta, totalPagado, saldoPendiente, historial);
    }

    // >>>> CREATION
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PagoResponseDTO registrarPagoPendiente(PagoRegistroRequestDTO request) {
        log.info("Iniciando liquidación o abono para la venta ID: {}, monto: S/ {}",
                request.idVenta(), request.monto()
        );

        if (request.idVenta() == null) {
            throw new ReglaNegocioException("El ID de la venta es obligatorio para registrar un pago pendiente");
        }

        // Sale lock
        Venta venta = ventaRepository.findByIdForUpdate(request.idVenta())
                .orElseThrow(() -> new RecursosNoEncontradosException("Venta no encontrada con ID: " + request.idVenta()));

        // Sale state
        if (venta.getEstado() == EstadoVentaEnum.ANULADA) {
            throw new ReglaNegocioException("No se pueden registrar pagos en una venta que ha sido anulada");
        }
        if (venta.getEstado() == EstadoVentaEnum.PAGADA) {
            throw new ReglaNegocioException("La venta ya se encuentra totalmente pagada y no admite nuevos registros de cobro (RN-VE14)");
        }
        if (venta.getEstado() != EstadoVentaEnum.PENDIENTE) {
            throw new ReglaNegocioException("El caso de uso aplica exclusivamente para ventas con saldo pendiente (RN-VE10)");
        }

        // Voucher
        boolean requiereVoucher = request.metodoPago() == MetodoPagoEnum.TARJETA_DEBITO ||
                request.metodoPago() == MetodoPagoEnum.TARJETA_CREDITO ||
                request.metodoPago() == MetodoPagoEnum.TRANSFERENCIA;

        if (requiereVoucher && (request.numeroOperacion() == null || request.numeroOperacion().trim().isEmpty())) {
            throw new ReglaNegocioException("Es obligatorio ingresar el número de voucher/operación para cobros con tarjeta o transferencia (RN-VE12)");
        }

        // Current balance
        BigDecimal totalPagadoActual = pagoRepository.sumMontoPagadoByVentaId(venta.getId());
        if (totalPagadoActual == null) {
            totalPagadoActual = BigDecimal.ZERO;
        }
        BigDecimal saldoPendiente = venta.getTotal().subtract(totalPagadoActual);

        // Payment amount
        if (request.monto() == null || request.monto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("El monto del abono debe ser mayor a cero (RN-VE13)");
        }
        if (request.monto().compareTo(saldoPendiente) > 0) {
            throw new ReglaNegocioException("El monto a pagar (S/ " + request.monto() +
                    ") excede el saldo pendiente restante (S/ " + saldoPendiente + ") (RN-VE13)"
            );
        }

        // Payment record
        Pago pago = PagoMapper.toEntity(request, venta);
        Pago pagoGuardado = pagoRepository.save(pago);

        // Updated balance
        BigDecimal nuevoSaldo = saldoPendiente.subtract(request.monto());
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) == 0) {
            log.info("Venta ID {} liquidada al 100%. Actualizando estado a PAGADA.", venta.getId());
            venta.setEstado(EstadoVentaEnum.PAGADA);
            ventaRepository.save(venta);
        }

        log.info("Pago registrado exitosamente con ID: {}. Saldo restante de la venta ID {}: S/ {}",
                pagoGuardado.getId(), venta.getId(), nuevoSaldo
        );

        return PagoMapper.toDTO(pagoGuardado);
    }

}

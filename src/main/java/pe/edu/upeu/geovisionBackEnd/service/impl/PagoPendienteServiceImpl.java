package pe.edu.upeu.geovisionBackEnd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovisionBackEnd.dto.PagoPendienteResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.PagoRegistroRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.PagoResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.Pago;
import pe.edu.upeu.geovisionBackEnd.entity.Persona;
import pe.edu.upeu.geovisionBackEnd.entity.Venta;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoVentaEnum;
import pe.edu.upeu.geovisionBackEnd.enums.MetodoPagoEnum;
import pe.edu.upeu.geovisionBackEnd.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;
import pe.edu.upeu.geovisionBackEnd.mapper.PagoMapper;
import pe.edu.upeu.geovisionBackEnd.repository.PagoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.VentaRepository;
import pe.edu.upeu.geovisionBackEnd.service.service.PagoPendienteService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PagoPendienteServiceImpl implements PagoPendienteService {

    private final VentaRepository ventaRepository;
    private final PagoRepository pagoRepository;
    private final PagoMapper pagoMapper;

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
                })
                .map(v -> {
                    BigDecimal totalPagado = pagoRepository.sumMontoPagadoByVentaId(v.getIdVenta());
                    if (totalPagado == null) {
                        totalPagado = BigDecimal.ZERO;
                    }
                    BigDecimal saldo = v.getTotal().subtract(totalPagado);
                    List<Pago> historial = pagoRepository.findByVentaIdVenta(v.getIdVenta());
                    return pagoMapper.toPagoPendienteResponseDTO(v, totalPagado, saldo, historial);
                })
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
        List<Pago> historial = pagoRepository.findByVentaIdVenta(idVenta);

        return pagoMapper.toPagoPendienteResponseDTO(venta, totalPagado, saldoPendiente, historial);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PagoResponseDTO registrarPagoPendiente(PagoRegistroRequestDTO request) {
        log.info("Iniciando liquidación o abono para la venta ID: {}, monto: S/ {}",
                request.getIdVenta(), request.getMonto());

        if (request.getIdVenta() == null) {
            throw new ReglaNegocioException("El ID de la venta es obligatorio para registrar un pago pendiente");
        }

        // a) Bloqueo pesimista para evitar condiciones de carrera y sobrepagos concurrentes
        Venta venta = ventaRepository.findByIdForUpdate(request.getIdVenta())
                .orElseThrow(() -> new RecursosNoEncontradosException("Venta no encontrada con ID: " + request.getIdVenta()));

        // b) RN-VE10: Validar que la venta admita pagos (no anulada y con estado PENDIENTE)
        if (venta.getEstado() == EstadoVentaEnum.ANULADA) {
            throw new ReglaNegocioException("No se pueden registrar pagos en una venta que ha sido anulada");
        }
        if (venta.getEstado() == EstadoVentaEnum.PAGADA) {
            throw new ReglaNegocioException("La venta ya se encuentra totalmente pagada y no admite nuevos registros de cobro (RN-VE14)");
        }
        if (venta.getEstado() != EstadoVentaEnum.PENDIENTE) {
            throw new ReglaNegocioException("El caso de uso aplica exclusivamente para ventas con saldo pendiente (RN-VE10)");
        }

        // c) RN-VE12: Validar voucher obligatorio en cobros con tarjeta o transferencia
        boolean requiereVoucher = request.getMetodoPago() == MetodoPagoEnum.TARJETA_DEBITO ||
                request.getMetodoPago() == MetodoPagoEnum.TARJETA_CREDITO ||
                request.getMetodoPago() == MetodoPagoEnum.TRANSFERENCIA;

        if (requiereVoucher && (request.getNumeroOperacion() == null || request.getNumeroOperacion().trim().isEmpty())) {
            throw new ReglaNegocioException("Es obligatorio ingresar el número de voucher/operación para cobros con tarjeta o transferencia (RN-VE12)");
        }

        // d) Calcular saldo pendiente actual bajo el bloqueo pesimista
        BigDecimal totalPagadoActual = pagoRepository.sumMontoPagadoByVentaId(venta.getIdVenta());
        if (totalPagadoActual == null) {
            totalPagadoActual = BigDecimal.ZERO;
        }
        BigDecimal saldoPendiente = venta.getTotal().subtract(totalPagadoActual);

        // e) RN-VE13: El monto no puede ser <= 0 ni exceder el saldo adeudado
        if (request.getMonto() == null || request.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("El monto del abono debe ser mayor a cero (RN-VE13)");
        }
        if (request.getMonto().compareTo(saldoPendiente) > 0) {
            throw new ReglaNegocioException("El monto a pagar (S/ " + request.getMonto() +
                    ") excede el saldo pendiente restante (S/ " + saldoPendiente + ") (RN-VE13)");
        }

        // f) Crear y guardar registro de Pago
        Pago pago = pagoMapper.toEntity(request, venta);
        Pago pagoGuardado = pagoRepository.save(pago);

        // g) RN-VE14: Si el saldo llega a cero, actualizar automáticamente el estado a PAGADA
        BigDecimal nuevoSaldo = saldoPendiente.subtract(request.getMonto());
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) == 0) {
            log.info("Venta ID {} liquidada al 100%. Actualizando estado a PAGADA.", venta.getIdVenta());
            venta.setEstado(EstadoVentaEnum.PAGADA);
            ventaRepository.save(venta);
        }

        log.info("Pago registrado exitosamente con ID: {}. Saldo restante de la venta ID {}: S/ {}",
                pagoGuardado.getIdPago(), venta.getIdVenta(), nuevoSaldo);

        return pagoMapper.toDTO(pagoGuardado);
    }
}

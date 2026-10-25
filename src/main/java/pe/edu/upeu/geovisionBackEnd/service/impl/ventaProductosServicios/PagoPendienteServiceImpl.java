package pe.edu.upeu.geovisionBackEnd.service.impl.ventaProductosServicios;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.PagoPendienteRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.PagoResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.SaldoPendienteDTO;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Empleado;
import pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento.MetodoPago;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.Comprobante;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.Pago;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.Venta;
import pe.edu.upeu.geovisionBackEnd.enums.gestionMantenimiento.EstadoComprobanteEnum;
import pe.edu.upeu.geovisionBackEnd.enums.gestionMantenimiento.MetodoPagoEnum;
import pe.edu.upeu.geovisionBackEnd.enums.ventaProductosServicios.EstadoCobroEnum;
import pe.edu.upeu.geovisionBackEnd.enums.ventaProductosServicios.EstadoDocumentoEnum;
import pe.edu.upeu.geovisionBackEnd.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;
import pe.edu.upeu.geovisionBackEnd.repository.controlAccesos.EmpleadoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.gestionMantenimiento.EstadoComprobanteRepository;
import pe.edu.upeu.geovisionBackEnd.repository.gestionMantenimiento.MetodoPagoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.ventaProductosServicios.ComprobanteRepository;
import pe.edu.upeu.geovisionBackEnd.repository.ventaProductosServicios.PagoRepository;
import pe.edu.upeu.geovisionBackEnd.service.service.ventaProductosServicios.PagoPendienteService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PagoPendienteServiceImpl implements PagoPendienteService {

    private final ComprobanteRepository comprobanteRepository;
    private final PagoRepository pagoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final EstadoComprobanteRepository estadoComprobanteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SaldoPendienteDTO> listarVentasConSaldoPendiente(String termino) {
        log.info("Listando comprobantes con saldo pendiente. Término de búsqueda: '{}'", termino);
        List<Comprobante> comprobantes = comprobanteRepository.findComprobantesConSaldoPendiente();

        return comprobantes.stream()
                .map(this::mapearASaldoPendienteDTO)
                .filter(dto -> {
                    if (termino == null || termino.trim().isEmpty()) {
                        return true;
                    }
                    String term = termino.trim().toLowerCase();
                    boolean matchSerieNumero = dto.getSerieNumero() != null && dto.getSerieNumero().toLowerCase().contains(term);
                    boolean matchCliente = dto.getClienteNombre() != null && dto.getClienteNombre().toLowerCase().contains(term);
                    return matchSerieNumero || matchCliente;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SaldoPendienteDTO obtenerSaldoPendientePorComprobante(Long idComprobante) {
        log.info("Consultando saldo pendiente para comprobante ID: {}", idComprobante);
        Comprobante comprobante = comprobanteRepository.findById(idComprobante)
                .orElseThrow(() -> new RecursosNoEncontradosException("Comprobante no encontrado con ID: " + idComprobante));
        return mapearASaldoPendienteDTO(comprobante);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PagoResponseDTO registrarPagoPendiente(PagoPendienteRequestDTO request) {
        log.info("Iniciando registro de pago pendiente para comprobante ID: {}, monto: {}",
                request.getComprobanteId(), request.getMonto());

        // a) Buscar comprobante por request.getComprobanteId()
        Comprobante comprobante = comprobanteRepository.findById(request.getComprobanteId())
                .orElseThrow(() -> new RecursosNoEncontradosException("Comprobante no encontrado con ID: " + request.getComprobanteId()));

        // b) Si el comprobante está anulado, no admite pagos
        if (comprobante.getEstadoDocumento() != null &&
                comprobante.getEstadoDocumento().equalsIgnoreCase(EstadoDocumentoEnum.ANULADO.name())) {
            throw new ReglaNegocioException("No se pueden registrar pagos en un comprobante anulado");
        }

        // c) Si la orden ya está cancelada, no admite nuevos pagos
        if (comprobante.getEstadoCobro() != null &&
                comprobante.getEstadoCobro().equalsIgnoreCase(EstadoCobroEnum.CANCELADO.name())) {
            throw new ReglaNegocioException("La orden ya se encuentra 100% cancelada y no admite nuevos pagos");
        }

        // d) Buscar empleado responsable activo
        Empleado empleado = empleadoRepository.findByIdPersonaAndEstado(request.getEmpleadoId(), 1)
                .orElseThrow(() -> new RecursosNoEncontradosException("Empleado responsable no encontrado o inactivo"));

        // e) Buscar método de pago
        MetodoPago metodoPago = metodoPagoRepository.findById(request.getMetodoPagoId())
                .orElseThrow(() -> new RecursosNoEncontradosException("Método de pago no encontrado"));

        // f) Validar RN-VE12: voucher obligatorio si es tarjeta/POS
        boolean requiereVoucher = metodoPago.getNombre() != null &&
                (metodoPago.getNombre().toUpperCase().contains("TARJETA") ||
                 metodoPago.getNombre().equalsIgnoreCase(MetodoPagoEnum.TARJETA.name()));
        if (requiereVoucher && (request.getNumeroOperacion() == null || request.getNumeroOperacion().trim().isEmpty())) {
            throw new ReglaNegocioException("Es obligatorio ingresar el número de voucher/operación para pagos con tarjeta/POS");
        }

        // g) Calcular saldo pendiente
        BigDecimal totalPagado = pagoRepository.sumMontoPagadoByComprobanteId(comprobante.getIdComprobante());
        if (totalPagado == null) {
            totalPagado = BigDecimal.ZERO;
        }
        BigDecimal totalEmitido = comprobante.getTotalEmitido() != null ? comprobante.getTotalEmitido() : BigDecimal.ZERO;
        BigDecimal saldoPendiente = totalEmitido.subtract(totalPagado);

        // h) Validar RN-VE13: monto mayor a 0 y no excede saldo pendiente
        if (request.getMonto() == null || request.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("El monto a pagar debe ser mayor a cero");
        }
        if (request.getMonto().compareTo(saldoPendiente) > 0) {
            throw new ReglaNegocioException("El monto ingresado (S/ " + request.getMonto() + ") excede el saldo pendiente (S/ " + saldoPendiente + ")");
        }

        // i) Crear y persistir Pago
        Pago pago = new Pago();
        pago.setComprobante(comprobante);
        pago.setEmpleado(empleado);
        pago.setMetodoPago(metodoPago);
        pago.setMonto(request.getMonto());
        pago.setNumeroOperacion(request.getNumeroOperacion() != null ? request.getNumeroOperacion().trim() : null);
        pago.setFechaRegistro(LocalDateTime.now());
        pago.setAnulado(0);
        Pago pagoGuardado = pagoRepository.save(pago);

        // j) Actualizar comprobante según saldo restante
        BigDecimal saldoRestante = saldoPendiente.subtract(request.getMonto());
        if (saldoRestante.compareTo(BigDecimal.ZERO) == 0) {
            comprobante.setEstadoCobro(EstadoCobroEnum.CANCELADO.name());
            comprobante.setFechaCancelacion(LocalDate.now());
            estadoComprobanteRepository.findByNombreIgnoreCase(EstadoComprobanteEnum.CANCELADO.name())
                    .ifPresent(comprobante::setEstadoComprobante);
        } else {
            comprobante.setEstadoCobro(EstadoCobroEnum.CON_ADELANTO.name());
            comprobante.setFechaCancelacion(null);
            estadoComprobanteRepository.findByNombreIgnoreCase(EstadoComprobanteEnum.CON_ADELANTO.name())
                    .ifPresent(comprobante::setEstadoComprobante);
        }
        comprobanteRepository.save(comprobante);

        log.info("Pago registrado exitosamente con ID: {}. Saldo restante comprobante ID {}: S/ {}",
                pagoGuardado.getIdPago(), comprobante.getIdComprobante(), saldoRestante);

        // k) Retornar PagoResponseDTO
        return mapearAPagoResponseDTO(pagoGuardado);
    }

    private SaldoPendienteDTO mapearASaldoPendienteDTO(Comprobante comprobante) {
        BigDecimal totalPagado = pagoRepository.sumMontoPagadoByComprobanteId(comprobante.getIdComprobante());
        if (totalPagado == null) {
            totalPagado = BigDecimal.ZERO;
        }
        BigDecimal totalEmitido = comprobante.getTotalEmitido() != null ? comprobante.getTotalEmitido() : BigDecimal.ZERO;
        BigDecimal saldoPendiente = totalEmitido.subtract(totalPagado);

        String serie = comprobante.getSerie() != null ? comprobante.getSerie() : "";
        String numero = comprobante.getNumero() != null ? comprobante.getNumero() : "";
        String serieNumero = serie.isEmpty() && numero.isEmpty() ? "" : (serie + "-" + numero);

        Long idVenta = comprobante.getVenta() != null ? comprobante.getVenta().getIdVenta() : null;
        String clienteNombre = obtenerNombreCliente(comprobante);

        SaldoPendienteDTO dto = new SaldoPendienteDTO();
        dto.setIdVenta(idVenta);
        dto.setIdComprobante(comprobante.getIdComprobante());
        dto.setSerieNumero(serieNumero);
        dto.setClienteNombre(clienteNombre);
        dto.setFechaEmision(comprobante.getFechaEmision());
        dto.setTotalVenta(totalEmitido);
        dto.setTotalPagado(totalPagado);
        dto.setSaldoPendiente(saldoPendiente);
        dto.setEstadoCobro(comprobante.getEstadoCobro());

        return dto;
    }

    private String obtenerNombreCliente(Comprobante comprobante) {
        if (comprobante == null || comprobante.getVenta() == null) {
            return "";
        }
        Venta venta = comprobante.getVenta();
        if (venta.getComprador() != null) {
            String nombres = venta.getComprador().getNombres() != null ? venta.getComprador().getNombres().trim() : "";
            String apellidos = venta.getComprador().getApellidos() != null ? venta.getComprador().getApellidos().trim() : "";
            return (nombres + " " + apellidos).trim();
        }
        if (venta.getPaciente() != null && venta.getPaciente().getPersona() != null) {
            String nombres = venta.getPaciente().getPersona().getNombres() != null ? venta.getPaciente().getPersona().getNombres().trim() : "";
            String apellidos = venta.getPaciente().getPersona().getApellidos() != null ? venta.getPaciente().getPersona().getApellidos().trim() : "";
            return (nombres + " " + apellidos).trim();
        }
        return "";
    }

    private String obtenerNombreEmpleado(Empleado empleado) {
        if (empleado == null || empleado.getPersona() == null) {
            return null;
        }
        String nombres = empleado.getPersona().getNombres() != null ? empleado.getPersona().getNombres().trim() : "";
        String apellidos = empleado.getPersona().getApellidos() != null ? empleado.getPersona().getApellidos().trim() : "";
        String completo = (nombres + " " + apellidos).trim();
        return completo.isEmpty() ? null : completo;
    }

    private PagoResponseDTO mapearAPagoResponseDTO(Pago pago) {
        PagoResponseDTO dto = new PagoResponseDTO();
        dto.setIdPago(pago.getIdPago());
        dto.setFechaRegistro(pago.getFechaRegistro());
        dto.setNumeroOperacion(pago.getNumeroOperacion());
        dto.setMonto(pago.getMonto());
        dto.setAnulado(pago.getAnulado());
        dto.setMotivoAnulacion(pago.getMotivoAnulacion());
        if (pago.getMetodoPago() != null) {
            dto.setMetodoPagoId(pago.getMetodoPago().getIdMetodoPago());
            dto.setMetodoPagoNombre(pago.getMetodoPago().getNombre());
        }
        if (pago.getEmpleado() != null) {
            dto.setEmpleadoId(pago.getEmpleado().getIdPersona());
            dto.setEmpleadoNombre(obtenerNombreEmpleado(pago.getEmpleado()));
        }
        return dto;
    }
}

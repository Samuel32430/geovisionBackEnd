package pe.edu.upeu.geovisionBackEnd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovisionBackEnd.dto.ProductoVendidoDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ServicioVendidoDTO;
import pe.edu.upeu.geovisionBackEnd.dto.PagoRegistroRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.VentaRegistroRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.VentaResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.Empleado;
import pe.edu.upeu.geovisionBackEnd.entity.MovimientoStockLog;
import pe.edu.upeu.geovisionBackEnd.enums.MotivoMovimientoStockEnum;
import pe.edu.upeu.geovisionBackEnd.repository.MovimientoStockLogRepository;
import pe.edu.upeu.geovisionBackEnd.entity.Paciente;
import pe.edu.upeu.geovisionBackEnd.entity.Pago;
import pe.edu.upeu.geovisionBackEnd.entity.Producto;
import pe.edu.upeu.geovisionBackEnd.entity.ProductoVendido;
import pe.edu.upeu.geovisionBackEnd.entity.Receta;
import pe.edu.upeu.geovisionBackEnd.entity.Servicio;
import pe.edu.upeu.geovisionBackEnd.entity.ServicioVendido;
import pe.edu.upeu.geovisionBackEnd.entity.Usuario;
import pe.edu.upeu.geovisionBackEnd.entity.Venta;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoVentaEnum;
import pe.edu.upeu.geovisionBackEnd.enums.MetodoPagoEnum;
import pe.edu.upeu.geovisionBackEnd.enums.TipoComprobanteEnum;
import pe.edu.upeu.geovisionBackEnd.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;
import pe.edu.upeu.geovisionBackEnd.mapper.PagoMapper;
import pe.edu.upeu.geovisionBackEnd.mapper.VentaMapper;
import pe.edu.upeu.geovisionBackEnd.repository.EmpleadoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.PacienteRepository;
import pe.edu.upeu.geovisionBackEnd.repository.PagoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.ProductoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.ProductoVendidoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.RecetaRepository;
import pe.edu.upeu.geovisionBackEnd.repository.ServicioRepository;
import pe.edu.upeu.geovisionBackEnd.repository.ServicioVendidoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.UsuarioRepository;
import pe.edu.upeu.geovisionBackEnd.repository.VentaRepository;
import pe.edu.upeu.geovisionBackEnd.service.service.VentaService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final PagoRepository pagoRepository;
    private final ProductoVendidoRepository productoVendidoRepository;
    private final ServicioVendidoRepository servicioVendidoRepository;
    private final ProductoRepository productoRepository;
    private final ServicioRepository servicioRepository;
    private final RecetaRepository recetaRepository;
    private final PacienteRepository pacienteRepository;
    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoStockLogRepository movimientoStockLogRepository;
    private final VentaMapper ventaMapper;
    private final PagoMapper pagoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VentaResponseDTO registrarVenta(VentaRegistroRequestDTO request) {
        log.info("Iniciando registro de venta unificada para total: S/ {}", request.getTotal());

        // 1. Determinar el empleado responsable (desde la sesión autenticada o fallback autorizado)
        Empleado empleado = obtenerEmpleadoResponsable(request.getIdEmpleado());

        // 2. Validar paciente si se indicó
        Paciente paciente = null;
        if (request.getIdPaciente() != null) {
            paciente = pacienteRepository.findById(request.getIdPaciente())
                    .orElseThrow(() -> new RecursosNoEncontradosException("Paciente no encontrado con ID: " + request.getIdPaciente()));
            if (paciente.getEstado() != EstadoGeneralEnum.ACTIVO) {
                throw new ReglaNegocioException("El paciente indicado no se encuentra en estado activo");
            }
        }

        // 3. Validar receta (RN-VE02: obligatoria si incluye lentes con receta)
        Receta receta = null;
        if (request.getIdReceta() != null) {
            if (paciente == null) {
                throw new ReglaNegocioException("Toda venta con receta debe estar asociada obligatoriamente a un paciente (RN-VE02)");
            }
            receta = recetaRepository.findByIdForUpdate(request.getIdReceta())
                    .orElseThrow(() -> new RecursosNoEncontradosException("Receta no encontrada con ID: " + request.getIdReceta()));

            if (receta.getPaciente() == null ||
                    !receta.getPaciente().getIdPersona().equals(paciente.getIdPersona())) {
                throw new ReglaNegocioException("La receta seleccionada no corresponde al paciente indicado (RN-VE03)");
            }
        }

        // 4. Validar pagos, adelantos y métodos de pago de forma anticipada (Fail-Fast)
        if (request.getPagos() == null || request.getPagos().isEmpty()) {
            throw new ReglaNegocioException("Debe registrar al menos un pago inicial o completo para la venta");
        }

        BigDecimal totalPagado = BigDecimal.ZERO;
        for (PagoRegistroRequestDTO pagoDTO : request.getPagos()) {
            if (pagoDTO.getMonto() == null || pagoDTO.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ReglaNegocioException("El monto de cada pago debe ser mayor a cero (RN-VE13)");
            }

            // RN-VE12: voucher obligatorio si es tarjeta o transferencia
            boolean requiereVoucher = pagoDTO.getMetodoPago() == MetodoPagoEnum.TARJETA_DEBITO ||
                    pagoDTO.getMetodoPago() == MetodoPagoEnum.TARJETA_CREDITO ||
                    pagoDTO.getMetodoPago() == MetodoPagoEnum.TRANSFERENCIA;

            if (requiereVoucher && (pagoDTO.getNumeroOperacion() == null || pagoDTO.getNumeroOperacion().trim().isEmpty())) {
                throw new ReglaNegocioException("Es obligatorio ingresar el número de voucher/operación para pagos con tarjeta o transferencia (RN-VE12)");
            }

            totalPagado = totalPagado.add(pagoDTO.getMonto());
        }

        if (totalPagado.compareTo(request.getTotal()) > 0) {
            throw new ReglaNegocioException("El total pagado (S/ " + totalPagado + ") no puede ser superior al total de la venta (S/ " + request.getTotal() + ") (RN-VE13)");
        }

        // RN-VE06: si es venta sin receta, se debe cobrar el 100%
        if (receta == null) {
            if (totalPagado.compareTo(request.getTotal()) < 0) {
                throw new ReglaNegocioException("En ventas de productos o servicios directos sin receta, el pago debe registrarse por el 100% del monto total (RN-VE06)");
            }
        } else {
            // RN-VE07: venta con receta permite adelanto pero no menor al 25%
            BigDecimal adelantoMinimo = request.getTotal().multiply(new BigDecimal("0.25"));
            if (totalPagado.compareTo(adelantoMinimo) < 0) {
                throw new ReglaNegocioException("En ventas con receta médica, el adelanto inicial no puede ser menor al 25% del total de la orden (Mínimo: S/ " + adelantoMinimo + ") (RN-VE07)");
            }

            // RN-VE08: si se registra con adelanto, es obligatoria la fecha de entrega estimada
            if (totalPagado.compareTo(request.getTotal()) < 0 && request.getFechaEntregaEstimada() == null) {
                throw new ReglaNegocioException("Si la venta se registra con adelanto, debe consignarse de forma obligatoria la fecha estimada de entrega (RN-VE08)");
            }
        }

        // 5. Determinar estado inicial de la venta (RN-VE09)
        EstadoVentaEnum estadoInicial = (totalPagado.compareTo(request.getTotal()) >= 0)
                ? EstadoVentaEnum.PAGADA
                : EstadoVentaEnum.PENDIENTE;

        // 6. Generar número de comprobante si no se proporcionó
        String numeroComprobante = request.getNumeroComprobante();
        if (numeroComprobante == null || numeroComprobante.trim().isEmpty()) {
            numeroComprobante = generarNumeroComprobante(request.getTipoComprobante());
        } else {
            numeroComprobante = numeroComprobante.trim();
            if (ventaRepository.existsByNumeroComprobante(numeroComprobante)) {
                throw new ReglaNegocioException("Ya existe una venta registrada con el número de comprobante: " + numeroComprobante);
            }
        }

        // 7. Determinar usuario responsable para auditoría de inventario (Kardex)
        Usuario usuarioResponsable = empleado.getUsuario();
        if (usuarioResponsable == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                usuarioResponsable = usuarioRepository.findByNombreOrCorreo(auth.getName(), auth.getName()).orElse(null);
            }
        }
        if (usuarioResponsable == null) {
            usuarioResponsable = usuarioRepository.findById(1L).orElse(null);
        }

        // 8. Validar y descontar stock de productos físicos con bloqueo pesimista (RN-VE05)
        List<ProductoVendido> productosParaGuardar = new ArrayList<>();
        List<MovimientoStockLog> movimientosParaGuardar = new ArrayList<>();

        if (request.getProductos() != null && !request.getProductos().isEmpty()) {
            for (ProductoVendidoDTO prodDTO : request.getProductos()) {
                Producto producto = productoRepository.findByIdForUpdate(prodDTO.getIdProducto())
                        .orElseThrow(() -> new RecursosNoEncontradosException("Producto no encontrado con ID: " + prodDTO.getIdProducto()));

                int stockPrevio = producto.getStock() != null ? producto.getStock() : 0;
                if (stockPrevio < prodDTO.getCantidad()) {
                    throw new ReglaNegocioException("Stock insuficiente para el producto '" + producto.getNombre() +
                            "'. Stock disponible: " + stockPrevio + ", Solicitado: " + prodDTO.getCantidad() + " (RN-VE05)");
                }

                // Descuento atómico de stock
                int nuevoStock = stockPrevio - prodDTO.getCantidad();
                producto.setStock(nuevoStock);
                productoRepository.save(producto);

                BigDecimal subTotal = prodDTO.getPrecioUnit().multiply(BigDecimal.valueOf(prodDTO.getCantidad()));
                ProductoVendido pv = ProductoVendido.builder()
                        .producto(producto)
                        .cantidad(prodDTO.getCantidad())
                        .precioUnit(prodDTO.getPrecioUnit())
                        .subTotal(subTotal)
                        .build();
                productosParaGuardar.add(pv);

                // Preparar log de Kardex (Motivo 5: SALIDA_POR_VENTA)
                MovimientoStockLog movimiento = MovimientoStockLog.builder()
                        .producto(producto)
                        .usuario(usuarioResponsable)
                        .fechaRegistro(LocalDateTime.now())
                        .cantidad(prodDTO.getCantidad())
                        .stockPrevio(stockPrevio)
                        .stockPosterior(nuevoStock)
                        .motivo(MotivoMovimientoStockEnum.SALIDA_POR_VENTA.getCodigo())
                        .observaciones("Salida por venta. Comprobante: " + numeroComprobante)
                        .build();
                movimientosParaGuardar.add(movimiento);
            }
        }

        // 9. Validar servicios solicitados
        List<ServicioVendido> serviciosParaGuardar = new ArrayList<>();
        if (request.getServicios() != null && !request.getServicios().isEmpty()) {
            for (ServicioVendidoDTO servDTO : request.getServicios()) {
                Servicio servicio = servicioRepository.findById(servDTO.getIdServicio())
                        .orElseThrow(() -> new RecursosNoEncontradosException("Servicio no encontrado con ID: " + servDTO.getIdServicio()));

                ServicioVendido sv = ServicioVendido.builder()
                        .servicio(servicio)
                        .precio(servDTO.getPrecio())
                        .build();
                serviciosParaGuardar.add(sv);
            }
        }

        // 10. Persistir cabecera de la venta
        Venta venta = Venta.builder()
                .empleado(empleado)
                .paciente(paciente)
                .receta(receta)
                .tipoComprobante(request.getTipoComprobante())
                .numeroComprobante(numeroComprobante)
                .total(request.getTotal())
                .estado(estadoInicial)
                .fechaRegistro(LocalDateTime.now())
                .build();

        Venta ventaGuardada = ventaRepository.save(venta);

        // 11. Persistir detalles de productos y movimientos de kardex
        for (ProductoVendido pv : productosParaGuardar) {
            pv.setVenta(ventaGuardada);
            productoVendidoRepository.save(pv);
        }

        for (MovimientoStockLog mov : movimientosParaGuardar) {
            mov.setObservaciones("Salida por venta. Comprobante ID: " + ventaGuardada.getIdVenta() + " (" + numeroComprobante + ")");
            movimientoStockLogRepository.save(mov);
        }

        // 12. Persistir detalles de servicios
        for (ServicioVendido sv : serviciosParaGuardar) {
            sv.setVenta(ventaGuardada);
            servicioVendidoRepository.save(sv);
        }

        // 13. Persistir pagos realizados
        List<Pago> pagosGuardados = new ArrayList<>();
        for (PagoRegistroRequestDTO pagoDTO : request.getPagos()) {
            Pago pago = pagoMapper.toEntity(pagoDTO, ventaGuardada);
            pagosGuardados.add(pagoRepository.save(pago));
        }

        BigDecimal saldoPendiente = request.getTotal().subtract(totalPagado);

        log.info("Venta registrada exitosamente con ID: {}, comprobante: {}, estado: {}, saldo pendiente: S/ {}",
                ventaGuardada.getIdVenta(), numeroComprobante, estadoInicial, saldoPendiente);

        return ventaMapper.toResponseDTO(
                ventaGuardada,
                totalPagado,
                saldoPendiente,
                productosParaGuardar,
                serviciosParaGuardar,
                pagosGuardados
        );
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponseDTO obtenerVentaPorId(Long idVenta) {
        log.info("Consultando detalle de venta ID: {}", idVenta);
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RecursosNoEncontradosException("Venta no encontrada con ID: " + idVenta));

        BigDecimal totalPagado = pagoRepository.sumMontoPagadoByVentaId(idVenta);
        if (totalPagado == null) {
            totalPagado = BigDecimal.ZERO;
        }
        BigDecimal saldoPendiente = venta.getTotal().subtract(totalPagado);

        List<ProductoVendido> productos = productoVendidoRepository.findByVentaIdVenta(idVenta);
        List<ServicioVendido> servicios = servicioVendidoRepository.findByVentaIdVenta(idVenta);
        List<Pago> pagos = pagoRepository.findByVentaIdVenta(idVenta);

        return ventaMapper.toResponseDTO(venta, totalPagado, saldoPendiente, productos, servicios, pagos);
    }

    private Empleado obtenerEmpleadoResponsable(Long idEmpleadoFallback) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            String username = auth.getName();
            Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreOrCorreo(username, username);
            if (usuarioOpt.isPresent()) {
                Optional<Empleado> empleadoOpt = empleadoRepository.findByUsuarioIdUsuario(usuarioOpt.get().getIdUsuario());
                if (empleadoOpt.isPresent()) {
                    Empleado emp = empleadoOpt.get();
                    if (emp.getEstado() == EstadoGeneralEnum.ACTIVO) {
                        return emp;
                    }
                }
            }
        }

        // Fallback para pruebas o llamadas con ID explícito
        if (idEmpleadoFallback != null) {
            return empleadoRepository.findByIdPersonaAndEstado(idEmpleadoFallback, EstadoGeneralEnum.ACTIVO)
                    .orElseThrow(() -> new RecursosNoEncontradosException("Empleado responsable no encontrado o inactivo con ID: " + idEmpleadoFallback));
        }

        throw new ReglaNegocioException("No se encontró un empleado activo responsable para registrar la venta");
    }

    private synchronized String generarNumeroComprobante(TipoComprobanteEnum tipoComprobante) {
        String prefijo = switch (tipoComprobante) {
            case FACTURA -> "F001-";
            case BOLETA -> "B001-";
            default -> "NV01-";
        };
        return ventaRepository.findUltimoNumeroComprobantePorPrefijo(prefijo)
                .map(ultimo -> {
                    try {
                        String correlativoStr = ultimo.substring(prefijo.length());
                        long siguiente = Long.parseLong(correlativoStr) + 1;
                        return String.format("%s%08d", prefijo, siguiente);
                    } catch (Exception e) {
                        return String.format("%s00000001", prefijo);
                    }
                })
                .orElseGet(() -> String.format("%s00000001", prefijo));
    }
}

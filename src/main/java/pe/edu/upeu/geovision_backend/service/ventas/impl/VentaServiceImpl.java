package pe.edu.upeu.geovision_backend.service.ventas.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.dto.ventas.ProductoVendidoDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.ServicioVendidoDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoRegistroRequestDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.VentaRegistroRequestDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.VentaResponseDTO;
import pe.edu.upeu.geovision_backend.entity.security.Empleado;
import pe.edu.upeu.geovision_backend.entity.atencion.Paciente;
import pe.edu.upeu.geovision_backend.entity.ventas.Pago;
import pe.edu.upeu.geovision_backend.entity.inventario.Producto;
import pe.edu.upeu.geovision_backend.entity.ventas.ProductoVendido;
import pe.edu.upeu.geovision_backend.entity.atencion.Receta;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.Servicio;
import pe.edu.upeu.geovision_backend.entity.ventas.ServicioVendido;
import pe.edu.upeu.geovision_backend.entity.security.Usuario;
import pe.edu.upeu.geovision_backend.entity.ventas.Venta;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.MetodoPagoEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.TipoComprobanteEnum;
import pe.edu.upeu.geovision_backend.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.mapper.ventas.PagoMapper;
import pe.edu.upeu.geovision_backend.mapper.ventas.VentaMapper;
import pe.edu.upeu.geovision_backend.repository.security.EmpleadoRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.PacienteRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.PagoRepository;
import pe.edu.upeu.geovision_backend.repository.inventario.ProductoRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.ProductoVendidoRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.RecetaRepository;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.ServicioRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.ServicioVendidoRepository;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.VentaRepository;
import pe.edu.upeu.geovision_backend.service.ventas.VentaService;

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
    private final VentaMapper ventaMapper;
    private final PagoMapper pagoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VentaResponseDTO registrarVenta(VentaRegistroRequestDTO request) {
        log.info("Iniciando registro de venta unificada para total: S/ {}", request.total());

        // 1. Determinar el empleado responsable (desde la sesión autenticada o fallback autorizado)
        Empleado empleado = obtenerEmpleadoResponsable(request.idEmpleado());

        // 2. Validar paciente si se indicó
        Paciente paciente = null;
        if (request.idPaciente() != null) {
            paciente = pacienteRepository.findById(request.idPaciente())
                    .orElseThrow(() -> new RecursosNoEncontradosException("Paciente no encontrado con ID: " + request.idPaciente()));
            if (!Boolean.TRUE.equals(paciente.getActivo())) {
                throw new ReglaNegocioException("El paciente indicado no se encuentra en estado activo");
            }
        }

        // 3. Validar receta (RN-VE02: obligatoria si incluye lentes con receta)
        Receta receta = null;
        if (request.idReceta() != null) {
            if (paciente == null) {
                throw new ReglaNegocioException("Toda venta con receta debe estar asociada obligatoriamente a un paciente (RN-VE02)");
            }
            receta = recetaRepository.findByIdForUpdate(request.idReceta())
                    .orElseThrow(() -> new RecursosNoEncontradosException("Receta no encontrada con ID: " + request.idReceta()));

            if (receta.getPaciente() == null ||
                    !receta.getPaciente().getId().equals(paciente.getId())) {
                throw new ReglaNegocioException("La receta seleccionada no corresponde al paciente indicado (RN-VE03)");
            }
        }

        // 4. Validar y descontar stock de productos físicos (RN-VE05)
        List<ProductoVendido> productosParaGuardar = new ArrayList<>();
        if (request.productos() != null && !request.productos().isEmpty()) {
            for (ProductoVendidoDTO prodDTO : request.productos()) {
                Producto producto = productoRepository.findById(prodDTO.idProducto())
                        .orElseThrow(() -> new RecursosNoEncontradosException("Producto no encontrado con ID: " + prodDTO.idProducto()));

                if (producto.getStock() < prodDTO.cantidad()) {
                    throw new ReglaNegocioException("Stock insuficiente para el producto '" + producto.getNombre() +
                            "'. Stock disponible: " + producto.getStock() + ", Solicitado: " + prodDTO.cantidad() + " (RN-VE05)");
                }

                // Descuento atómico de stock
                producto.setStock(producto.getStock() - prodDTO.cantidad());
                productoRepository.save(producto);

                BigDecimal subTotal = prodDTO.precioUnit().multiply(BigDecimal.valueOf(prodDTO.cantidad()));
                ProductoVendido pv = new ProductoVendido(null, prodDTO.cantidad(), prodDTO.precioUnit(), subTotal, null, producto);
                productosParaGuardar.add(pv);
            }
        }

        // 5. Validar servicios solicitados
        List<ServicioVendido> serviciosParaGuardar = new ArrayList<>();
        if (request.servicios() != null && !request.servicios().isEmpty()) {
            for (ServicioVendidoDTO servDTO : request.servicios()) {
                Servicio servicio = servicioRepository.findById(servDTO.idServicio())
                        .orElseThrow(() -> new RecursosNoEncontradosException("Servicio no encontrado con ID: " + servDTO.idServicio()));

                ServicioVendido sv = new ServicioVendido(null, servDTO.precio(), null, servicio);
                serviciosParaGuardar.add(sv);
            }
        }

        // 6. Validar pagos, adelantos y métodos de pago (RN-VE06, RN-VE07, RN-VE08, RN-VE12, RN-VE13)
        if (request.pagos() == null || request.pagos().isEmpty()) {
            throw new ReglaNegocioException("Debe registrar al menos un pago inicial o completo para la venta");
        }

        BigDecimal totalPagado = BigDecimal.ZERO;
        for (PagoRegistroRequestDTO pagoDTO : request.pagos()) {
            if (pagoDTO.monto() == null || pagoDTO.monto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ReglaNegocioException("El monto de cada pago debe ser mayor a cero (RN-VE13)");
            }

            // RN-VE12: voucher obligatorio si es tarjeta o transferencia
            boolean requiereVoucher = pagoDTO.metodoPago() == MetodoPagoEnum.TARJETA_DEBITO ||
                    pagoDTO.metodoPago() == MetodoPagoEnum.TARJETA_CREDITO ||
                    pagoDTO.metodoPago() == MetodoPagoEnum.TRANSFERENCIA;

            if (requiereVoucher && (pagoDTO.numeroOperacion() == null || pagoDTO.numeroOperacion().trim().isEmpty())) {
                throw new ReglaNegocioException("Es obligatorio ingresar el número de voucher/operación para pagos con tarjeta o transferencia (RN-VE12)");
            }

            totalPagado = totalPagado.add(pagoDTO.monto());
        }

        if (totalPagado.compareTo(request.total()) > 0) {
            throw new ReglaNegocioException("El total pagado (S/ " + totalPagado + ") no puede ser superior al total de la venta (S/ " + request.total() + ") (RN-VE13)");
        }

        // RN-VE06: si es venta sin receta, se debe cobrar el 100%
        if (receta == null) {
            if (totalPagado.compareTo(request.total()) < 0) {
                throw new ReglaNegocioException("En ventas de productos o servicios directos sin receta, el pago debe registrarse por el 100% del monto total (RN-VE06)");
            }
        } else {
            // RN-VE07: venta con receta permite adelanto pero no menor al 25%
            BigDecimal adelantoMinimo = request.total().multiply(new BigDecimal("0.25"));
            if (totalPagado.compareTo(adelantoMinimo) < 0) {
                throw new ReglaNegocioException("En ventas con receta médica, el adelanto inicial no puede ser menor al 25% del total de la orden (Mínimo: S/ " + adelantoMinimo + ") (RN-VE07)");
            }

            // RN-VE08: si se registra con adelanto, es obligatoria la fecha de entrega estimada
            if (totalPagado.compareTo(request.total()) < 0 && request.fechaEntregaEstimada() == null) {
                throw new ReglaNegocioException("Si la venta se registra con adelanto, debe consignarse de forma obligatoria la fecha estimada de entrega (RN-VE08)");
            }
        }

        // 7. Determinar estado inicial de la venta (RN-VE09)
        EstadoVentaEnum estadoInicial = (totalPagado.compareTo(request.total()) >= 0)
                ? EstadoVentaEnum.PAGADA
                : EstadoVentaEnum.PENDIENTE;

        // 8. Generar número de comprobante si no se proporcionó
        String numeroComprobante = request.numeroComprobante();
        if (numeroComprobante == null || numeroComprobante.trim().isEmpty()) {
            numeroComprobante = generarNumeroComprobante(request.tipoComprobante());
        } else {
            numeroComprobante = numeroComprobante.trim();
            if (ventaRepository.existsByNumeroComprobante(numeroComprobante)) {
                throw new ReglaNegocioException("Ya existe una venta registrada con el número de comprobante: " + numeroComprobante);
            }
        }

        // 9. Persistir cabecera de la venta
        Venta venta = new Venta(null, LocalDateTime.now(), request.total(), estadoInicial, numeroComprobante, request.tipoComprobante(), empleado, paciente, receta);

        Venta ventaGuardada = ventaRepository.save(venta);

        // 10. Persistir detalles de productos
        for (ProductoVendido pv : productosParaGuardar) {
            pv.setVenta(ventaGuardada);
            productoVendidoRepository.save(pv);
        }

        // 11. Persistir detalles de servicios
        for (ServicioVendido sv : serviciosParaGuardar) {
            sv.setVenta(ventaGuardada);
            servicioVendidoRepository.save(sv);
        }

        // 12. Persistir pagos realizados
        List<Pago> pagosGuardados = new ArrayList<>();
        for (PagoRegistroRequestDTO pagoDTO : request.pagos()) {
            Pago pago = pagoMapper.toEntity(pagoDTO, ventaGuardada);
            pagosGuardados.add(pagoRepository.save(pago));
        }

        BigDecimal saldoPendiente = request.total().subtract(totalPagado);

        log.info("Venta registrada exitosamente con ID: {}, comprobante: {}, estado: {}, saldo pendiente: S/ {}",
                ventaGuardada.getId(), numeroComprobante, estadoInicial, saldoPendiente);

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

        List<ProductoVendido> productos = productoVendidoRepository.findByVentaId(idVenta);
        List<ServicioVendido> servicios = servicioVendidoRepository.findByVentaId(idVenta);
        List<Pago> pagos = pagoRepository.findByVentaId(idVenta);

        return ventaMapper.toResponseDTO(venta, totalPagado, saldoPendiente, productos, servicios, pagos);
    }

    private Empleado obtenerEmpleadoResponsable(Long idEmpleadoFallback) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            String username = auth.getName();
            Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreOrCorreo(username, username);
            if (usuarioOpt.isPresent()) {
                Optional<Empleado> empleadoOpt = empleadoRepository.findByUsuarioId(usuarioOpt.get().getId());
                if (empleadoOpt.isPresent()) {
                    Empleado emp = empleadoOpt.get();
                    if (Boolean.TRUE.equals(emp.getActivo())) {
                        return emp;
                    }
                }
            }
        }

        // Fallback para pruebas o llamadas con ID explícito
        if (idEmpleadoFallback != null) {
            return empleadoRepository.findByIdAndActivo(idEmpleadoFallback, true)
                    .orElseThrow(() -> new RecursosNoEncontradosException("Empleado responsable no encontrado o inactivo con ID: " + idEmpleadoFallback));
        }

        throw new ReglaNegocioException("No se encontró un empleado activo responsable para registrar la venta");
    }

    private String generarNumeroComprobante(TipoComprobanteEnum tipoComprobante) {
        String prefijo;
        if (tipoComprobante == TipoComprobanteEnum.FACTURA) {
            prefijo = "F001-";
        } else if (tipoComprobante == TipoComprobanteEnum.BOLETA) {
            prefijo = "B001-";
        } else {
            prefijo = "NV01-";
        }
        long correlativo = System.currentTimeMillis() % 100000000L;
        return String.format("%s%08d", prefijo, correlativo);
    }
}

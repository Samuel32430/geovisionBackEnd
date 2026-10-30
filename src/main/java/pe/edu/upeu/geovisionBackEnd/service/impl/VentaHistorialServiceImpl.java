package pe.edu.upeu.geovisionBackEnd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovisionBackEnd.dto.VentaHistorialResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.VentaResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.Pago;
import pe.edu.upeu.geovisionBackEnd.entity.ProductoVendido;
import pe.edu.upeu.geovisionBackEnd.entity.ServicioVendido;
import pe.edu.upeu.geovisionBackEnd.entity.Venta;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoVentaEnum;
import pe.edu.upeu.geovisionBackEnd.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovisionBackEnd.mapper.VentaMapper;
import pe.edu.upeu.geovisionBackEnd.repository.PagoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.ProductoVendidoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.ServicioVendidoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.VentaRepository;
import pe.edu.upeu.geovisionBackEnd.service.service.VentaHistorialService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VentaHistorialServiceImpl implements VentaHistorialService {

    private final VentaRepository ventaRepository;
    private final PagoRepository pagoRepository;
    private final ProductoVendidoRepository productoVendidoRepository;
    private final ServicioVendidoRepository servicioVendidoRepository;
    private final VentaMapper ventaMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<VentaHistorialResponseDTO> buscarHistorialVentas(
            String termino,
            EstadoVentaEnum estado,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable) {

        log.info("Consultando historial de ventas: termino='{}', estado={}, fechaInicio={}, fechaFin={}",
                termino, estado, fechaInicio, fechaFin);

        String terminoLimpio = (termino != null && !termino.trim().isEmpty()) ? termino.trim() : null;

        Page<Venta> paginaVentas = ventaRepository.buscarHistorialVentas(
                terminoLimpio,
                estado,
                fechaInicio,
                fechaFin,
                pageable
        );

        return paginaVentas.map(venta -> {
            BigDecimal totalPagado = pagoRepository.sumMontoPagadoByVentaId(venta.getIdVenta());
            if (totalPagado == null) {
                totalPagado = BigDecimal.ZERO;
            }
            BigDecimal saldoPendiente = venta.getTotal().subtract(totalPagado);

            int cantidadProductos = productoVendidoRepository.findByVentaIdVenta(venta.getIdVenta()).size();
            int cantidadServicios = servicioVendidoRepository.findByVentaIdVenta(venta.getIdVenta()).size();
            int cantidadItems = cantidadProductos + cantidadServicios + (venta.getReceta() != null ? 1 : 0);

            return ventaMapper.toHistorialResponseDTO(venta, totalPagado, saldoPendiente, cantidadItems);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponseDTO obtenerDetalleModalVenta(Long idVenta) {
        log.info("Consultando detalle completo para modal de la venta ID: {}", idVenta);
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
}

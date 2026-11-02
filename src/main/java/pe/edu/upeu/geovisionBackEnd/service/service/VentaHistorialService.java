package pe.edu.upeu.geovisionBackEnd.service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovisionBackEnd.dto.VentaHistorialResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.VentaResponseDTO;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoVentaEnum;

import java.time.LocalDateTime;

public interface VentaHistorialService {

    /**
     * CUS08: Consultar Historial de Ventas
     * Búsqueda paginada con filtros dinámicos por término (comprobante, DNI o cliente),
     * estado de venta y rango de fechas.
     */
    Page<VentaHistorialResponseDTO> buscarHistorialVentas(
            String termino,
            EstadoVentaEnum estado,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable
    );

    /**
     * CUS08: Detalle exhaustivo de la venta para modal informativo.
     */
    VentaResponseDTO obtenerDetalleModalVenta(Long idVenta);
}

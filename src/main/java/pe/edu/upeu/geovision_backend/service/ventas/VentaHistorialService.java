package pe.edu.upeu.geovision_backend.service.ventas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovision_backend.dto.ventas.VentaHistorialResponseDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.VentaResponseDTO;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;

import java.time.LocalDateTime;

public interface VentaHistorialService {

    // >>>> QUERIES
    Page<VentaHistorialResponseDTO> buscarHistorialVentas(
        String termino,
        EstadoVentaEnum estado,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        Pageable pageable
    );

    VentaResponseDTO obtenerDetalleModalVenta(Long idVenta);

}

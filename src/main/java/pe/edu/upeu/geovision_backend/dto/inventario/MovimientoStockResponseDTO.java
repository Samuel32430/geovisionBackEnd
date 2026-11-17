package pe.edu.upeu.geovision_backend.dto.inventario;

import java.time.LocalDateTime;

public record MovimientoStockResponseDTO(
    Long id,
    Long idProducto,
    String codigoProducto,
    String nombreProducto,
    Integer cantidad,
    Integer stockPrevio,
    Integer stockPosterior,
    Integer motivo,
    String motivoDescripcion,
    String observaciones,
    String nombreUsuario,
    LocalDateTime fechaRegistro
) {
}

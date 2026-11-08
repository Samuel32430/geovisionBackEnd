package pe.edu.upeu.geovision_backend.dto.inventario;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record MovimientoStockResponseDTO(
    Long idMovimiento,
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

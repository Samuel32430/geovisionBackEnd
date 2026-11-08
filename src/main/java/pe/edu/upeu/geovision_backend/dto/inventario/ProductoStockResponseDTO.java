package pe.edu.upeu.geovision_backend.dto.inventario;

import lombok.Builder;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record ProductoStockResponseDTO(
    Long idProducto,
    String codigo,
    String nombre,
    Integer stockActual,
    Integer stockMinimoReferencial,
    BigDecimal precio,
    Boolean disponible,
    Integer estado,
    String estadoDescripcion
) {
}

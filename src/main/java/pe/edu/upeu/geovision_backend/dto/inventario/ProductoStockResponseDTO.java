package pe.edu.upeu.geovision_backend.dto.inventario;

import java.math.BigDecimal;

public record ProductoStockResponseDTO(
    Long id,
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

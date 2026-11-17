package pe.edu.upeu.geovision_backend.dto.inventario;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductoResponseDTO(
    Long id,
    String codigo,
    String nombre,
    String marca,
    String descripcion,
    BigDecimal precio,
    Integer stock,
    Integer estado,
    String estadoDescripcion,
    LocalDateTime fechaRegistro,
    Long idCategoria,
    String nombreCategoria,
    MonturaDTO montura,
    Boolean esMontura
) {
    public Boolean esMontura() {
        if (esMontura != null) {
            return esMontura;
        }
        return montura != null;
    }
}

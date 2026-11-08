package pe.edu.upeu.geovision_backend.dto.inventario;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record ProductoResponseDTO(
    Long idProducto,
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

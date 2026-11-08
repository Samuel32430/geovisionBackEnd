package pe.edu.upeu.geovision_backend.dto.inventario;

import lombok.Builder;
import jakarta.validation.constraints.Size;
import pe.edu.upeu.geovision_backend.enums.inventario.MaterialMonturaEnum;
import pe.edu.upeu.geovision_backend.enums.inventario.ModeloMonturaEnum;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record MonturaDTO(
    Long idProducto,
    String codigo,
    String nombre,
    BigDecimal precio,
    Integer stock,
    @Size(max = 100, message = "El color no debe superar los 100 caracteres")
    String color,
    ModeloMonturaEnum modelo,
    String modeloDescripcion,
    MaterialMonturaEnum material,
    String materialDescripcion
) {
}

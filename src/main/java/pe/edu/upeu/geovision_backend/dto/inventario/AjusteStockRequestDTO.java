package pe.edu.upeu.geovision_backend.dto.inventario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AjusteStockRequestDTO {

    @NotNull(message = "El ID del producto es obligatorio")
    private Long idProducto;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad para ajuste debe ser mayor a cero")
    private Integer cantidad;

    @NotBlank(message = "El tipo de ajuste es obligatorio (INCREMENTO o DECREMENTO)")
    @Pattern(regexp = "^(?i)(INCREMENTO|DECREMENTO)$", message = "El tipo de ajuste debe ser INCREMENTO o DECREMENTO")
    private String tipoAjuste;

    @NotNull(message = "El motivo del ajuste es obligatorio")
    private Integer motivo;

    @NotBlank(message = "La justificación u observaciones son obligatorias para registrar un ajuste de stock")
    @Size(max = 500, message = "Las observaciones no deben superar los 500 caracteres")
    private String observaciones;
}

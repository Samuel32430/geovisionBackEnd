package pe.edu.upeu.geovision_backend.dto.inventario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AjusteStockRequestDTO(
    @NotNull(message = "El ID del producto es obligatorio")
    Long idProducto,
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad para ajuste debe ser mayor a cero")
    Integer cantidad,
    @NotBlank(message = "El tipo de ajuste es obligatorio (INCREMENTO o DECREMENTO)")
    @Pattern(regexp = "^(?i)(INCREMENTO|DECREMENTO)$", message = "El tipo de ajuste debe ser INCREMENTO o DECREMENTO")
    String tipoAjuste,
    @NotNull(message = "El motivo del ajuste es obligatorio")
    Integer motivo,
    @NotBlank(message = "La justificación u observaciones son obligatorias para registrar un ajuste de stock")
    @Size(max = 500, message = "Las observaciones no deben superar los 500 caracteres")
    String observaciones
) {
}

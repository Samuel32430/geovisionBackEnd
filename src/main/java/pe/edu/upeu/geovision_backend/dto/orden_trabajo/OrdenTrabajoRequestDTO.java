package pe.edu.upeu.geovision_backend.dto.orden_trabajo;

import jakarta.validation.constraints.Positive;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OrdenTrabajoRequestDTO(
    @NotNull(message = "El ID del lente es obligatorio")
    @Positive
    Long idLente,
    @Size(max = 500, message = "La descripción no debe superar los 500 caracteres")
    String descripcion
) {
}

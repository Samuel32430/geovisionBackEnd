package pe.edu.upeu.geovision_backend.dto.atencion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public record PacienteRequestDTO(
    @Positive(message = "El ID de la persona debe ser positivo")
    Long id,
    @Valid
    PersonaRequestDTO persona,
    @Min(value = 0, message = "El estado debe ser 0 o 1")
    @Max(value = 1, message = "El estado debe ser 0 o 1")
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = pe.edu.upeu.geovision_backend.config.DtoDefaultsJackson2.One.class)
    @tools.jackson.databind.annotation.JsonDeserialize(using = pe.edu.upeu.geovision_backend.config.DtoDefaultsJackson3.One.class)
    Integer estado,
    @Positive(message = "El ID del usuario debe ser positivo")
    Long idUsuario
) {
}

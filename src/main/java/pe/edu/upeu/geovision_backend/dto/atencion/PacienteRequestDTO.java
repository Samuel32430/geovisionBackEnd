package pe.edu.upeu.geovision_backend.dto.atencion;

import lombok.Builder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Builder(toBuilder = true)
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(builder = PacienteRequestDTO.PacienteRequestDTOBuilder.class)
@tools.jackson.databind.annotation.JsonDeserialize(builder = PacienteRequestDTO.PacienteRequestDTOBuilder.class)
public record PacienteRequestDTO(
    @Positive(message = "El ID de la persona debe ser positivo")
    Long idPersona,
    @Valid
    PersonaRequestDTO persona,
    @Min(value = 0, message = "El estado debe ser 0 o 1")
    @Max(value = 1, message = "El estado debe ser 0 o 1")
    Integer estado,
    @Positive(message = "El ID del usuario debe ser positivo")
    Long idUsuario
) {
    @com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "")
    @tools.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "")
    public static class PacienteRequestDTOBuilder {
        private Integer estado = 1;
    }
}

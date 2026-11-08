package pe.edu.upeu.geovision_backend.dto.atencion;

import lombok.Builder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(builder = RecetaRequestDTO.RecetaRequestDTOBuilder.class)
@tools.jackson.databind.annotation.JsonDeserialize(builder = RecetaRequestDTO.RecetaRequestDTOBuilder.class)
public record RecetaRequestDTO(
    @NotNull(message = "El ID del paciente es obligatorio")
    @Positive(message = "El ID del paciente debe ser positivo")
    Long idPaciente,
    @NotNull(message = "El ID del empleado/optómetra es obligatorio")
    @Positive(message = "El ID del empleado debe ser positivo")
    Long idEmpleado,
    @NotNull(message = "El indicador externo es obligatorio")
    @Min(value = 0, message = "El indicador externo debe ser 0 o 1")
    @Max(value = 1, message = "El indicador externo debe ser 0 o 1")
    Integer esExterno,
    LocalDateTime fechaRegistro,
    @NotEmpty(message = "Debe registrar al menos una medida clínica")
    List<@NotNull @Valid MedidaRequestDTO> medidas
) {
    @com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "")
    @tools.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "")
    public static class RecetaRequestDTOBuilder {
        private Integer esExterno = 0;
    }
}

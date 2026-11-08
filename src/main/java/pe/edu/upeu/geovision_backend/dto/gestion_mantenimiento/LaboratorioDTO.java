package pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento;

import lombok.Builder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Builder(toBuilder = true)
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(builder = LaboratorioDTO.LaboratorioDTOBuilder.class)
@tools.jackson.databind.annotation.JsonDeserialize(builder = LaboratorioDTO.LaboratorioDTOBuilder.class)
public record LaboratorioDTO(
    Long idLaboratorio,
    @NotBlank(message = "El nombre del laboratorio es obligatorio")
    @Size(max = 200, message = "El nombre del laboratorio no debe superar los 200 caracteres")
    String nombre,
    @Size(max = 20, message = "El RUC no debe superar los 20 caracteres")
    String ruc,
    @Size(max = 50, message = "El teléfono no debe superar los 50 caracteres")
    String telefono,
    @Size(max = 200, message = "La dirección no debe superar los 200 caracteres")
    String direccion,
    @Email(message = "El formato de correo no es válido")
    @Size(max = 100, message = "El correo no debe superar los 100 caracteres")
    String correo,
    @NotNull(message = "El estado es obligatorio")
    @Min(value = 0, message = "El estado debe ser 0 o 1")
    @Max(value = 1, message = "El estado debe ser 0 o 1")
    Integer estado
) {
    @com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "")
    @tools.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "")
    public static class LaboratorioDTOBuilder {
        private Integer estado = 1;
    }
}

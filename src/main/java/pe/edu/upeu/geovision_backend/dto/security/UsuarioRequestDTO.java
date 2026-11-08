package pe.edu.upeu.geovision_backend.dto.security;

import lombok.Builder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.Valid;

import java.util.Set;

@Builder(toBuilder = true)
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(builder = UsuarioRequestDTO.UsuarioRequestDTOBuilder.class)
@tools.jackson.databind.annotation.JsonDeserialize(builder = UsuarioRequestDTO.UsuarioRequestDTOBuilder.class)
public record UsuarioRequestDTO(
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre de usuario debe tener entre 3 y 100 caracteres")
    String nombre,
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    String password,
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    @Size(max = 100, message = "El correo electrónico no debe superar los 100 caracteres")
    String correo,
    @Min(0)
    @Max(1)
    Integer estado,
    @NotNull(message = "El ID de la persona asociada es obligatorio")
    @Positive
    Long idPersona,
    Set<@NotNull @Positive Long> rolesIds,
    @Valid
    EmpleadoRegistroDTO empleado
) {
    @Override
    public String toString() {
        return "UsuarioRequestDTO[" + "nombre=" + nombre + ", " + "password=<redacted>" + ", " + "correo=" + correo + ", " + "estado=" + estado + ", " + "idPersona=" + idPersona + ", " + "rolesIds=" + rolesIds + ", " + "empleado=" + empleado + "]";
    }

    @com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "")
    @tools.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "")
    public static class UsuarioRequestDTOBuilder {
        private Integer estado = 1;
    }
}

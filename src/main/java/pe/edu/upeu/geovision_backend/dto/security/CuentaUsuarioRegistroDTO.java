package pe.edu.upeu.geovision_backend.dto.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Set;

// Sin toString: este objeto contiene una contraseña en texto plano.

public record CuentaUsuarioRegistroDTO(
    @NotBlank
    @Size(min = 3, max = 100)
    String nombre,
    @NotBlank
    @Email
    @Size(max = 100)
    String correo,
    @NotBlank
    @Size(min = 6, max = 100)
    String password,
    @Min(0)
    @Max(1)
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = pe.edu.upeu.geovision_backend.config.DtoDefaultsJackson2.One.class)
    @tools.jackson.databind.annotation.JsonDeserialize(using = pe.edu.upeu.geovision_backend.config.DtoDefaultsJackson3.One.class)
    Integer estado,
    Set<@NotNull @Positive Long> rolesIds
) {
}

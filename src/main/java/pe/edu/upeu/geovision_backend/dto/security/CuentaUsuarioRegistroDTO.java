package pe.edu.upeu.geovision_backend.dto.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

// Sin toString: este objeto contiene una contraseña en texto plano.
@Getter
@Setter
public class CuentaUsuarioRegistroDTO {
    @NotBlank
    @Size(min = 3, max = 100)
    private String nombre;

    @NotBlank
    @Email
    @Size(max = 100)
    private String correo;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    @Min(0)
    @Max(1)
    private Integer estado = 1;

    private Set<@NotNull @Positive Long> rolesIds;
}

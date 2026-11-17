package pe.edu.upeu.geovision_backend.dto.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
    @NotBlank(message = "El nombre de usuario o correo electrónico es obligatorio")
    @Size(max = 100, message = "El usuario o correo no debe superar los 100 caracteres")
    String username,
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    String password
) {
}

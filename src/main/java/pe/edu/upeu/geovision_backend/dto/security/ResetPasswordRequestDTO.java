package pe.edu.upeu.geovision_backend.dto.security;

import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Builder(toBuilder = true)
public record ResetPasswordRequestDTO(
    @NotBlank(message = "El token de recuperación es obligatorio")
    String token,
    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 6, max = 100, message = "La nueva contraseña debe tener entre 6 y 100 caracteres")
    String nuevoPassword,
    @NotBlank(message = "La confirmación de la contraseña es obligatoria")
    String confirmarPassword
) {
}

package pe.edu.upeu.geovision_backend.dto.security;

import lombok.Builder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Builder(toBuilder = true)
public record RecuperarPasswordRequestDTO(
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato de correo electrónico no es válido")
    @Size(max = 100, message = "El correo electrónico no debe superar los 100 caracteres")
    String correo
) {
}

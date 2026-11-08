package pe.edu.upeu.geovision_backend.dto.orden_trabajo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record NotificacionOrdenRequestDTO(
        @NotBlank @Pattern(regexp = "\\+[1-9][0-9]{7,14}", message = "Use formato internacional, por ejemplo +51987654321")
        String telefono) {
}

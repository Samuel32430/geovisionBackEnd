package pe.edu.upeu.geovision_backend.dto.orden_trabajo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record NotificacionPacienteDTO(
    @NotNull(message = "El ID de la orden de trabajo es obligatorio")
    Long id,
    @NotBlank(message = "El teléfono de destino es obligatorio")
    @Size(max = 50, message = "El teléfono no debe superar los 50 caracteres")
    String telefono,
    @NotBlank(message = "El mensaje a enviar es obligatorio")
    @Size(max = 500, message = "El mensaje no debe superar los 500 caracteres")
    String mensaje,
    String canal,
    Boolean exitoso,
    LocalDateTime fechaEnvio
) {
}

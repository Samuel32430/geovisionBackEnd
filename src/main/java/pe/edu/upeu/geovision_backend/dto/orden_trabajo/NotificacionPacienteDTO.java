package pe.edu.upeu.geovision_backend.dto.orden_trabajo;

import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(builder = NotificacionPacienteDTO.NotificacionPacienteDTOBuilder.class)
@tools.jackson.databind.annotation.JsonDeserialize(builder = NotificacionPacienteDTO.NotificacionPacienteDTOBuilder.class)
public record NotificacionPacienteDTO(
    @NotNull(message = "El ID de la orden de trabajo es obligatorio")
    Long idOrdenTrabajo,
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
    @com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "")
    @tools.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "")
    public static class NotificacionPacienteDTOBuilder {
        private String canal = "WHATSAPP";
    }
}

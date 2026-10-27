package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionPacienteDTO {

    @NotNull(message = "El ID de la orden de trabajo es obligatorio")
    private Long idOrdenTrabajo;

    @NotBlank(message = "El teléfono de destino es obligatorio")
    @Size(max = 50, message = "El teléfono no debe superar los 50 caracteres")
    private String telefono;

    @NotBlank(message = "El mensaje a enviar es obligatorio")
    @Size(max = 500, message = "El mensaje no debe superar los 500 caracteres")
    private String mensaje;

    @Builder.Default
    private String canal = "WHATSAPP";

    private Boolean exitoso;

    private LocalDateTime fechaEnvio;
}

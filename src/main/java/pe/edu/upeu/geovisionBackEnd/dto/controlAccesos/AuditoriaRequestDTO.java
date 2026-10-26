package pe.edu.upeu.geovisionBackEnd.dto.controlAccesos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaRequestDTO {

    @NotBlank(message = "La entidad es obligatoria")
    @Size(max = 100, message = "La entidad no debe exceder 100 caracteres")
    private String entidad;

    @NotNull(message = "El identificador de entidad es obligatorio")
    @Positive(message = "El identificador de entidad debe ser positivo")
    private Long idEntidad;

    @NotBlank(message = "La acción es obligatoria")
    @Size(max = 100, message = "La acción no debe exceder 100 caracteres")
    private String accion;

    private String datosAnteriores;
    private String datosNuevos;

    @NotNull(message = "El identificador de usuario es obligatorio")
    @Positive(message = "El identificador de usuario debe ser positivo")
    private Long idUsuario;
}

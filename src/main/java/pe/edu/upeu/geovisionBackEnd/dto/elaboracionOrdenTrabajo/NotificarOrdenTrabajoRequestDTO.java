package pe.edu.upeu.geovisionBackEnd.dto.elaboracionOrdenTrabajo;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

/** El backend establece la fecha real y verifica el estado antes de notificar. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificarOrdenTrabajoRequestDTO {

    @NotNull(message = "idOrdenTrabajo es obligatorio")
    @Positive(message = "idOrdenTrabajo debe ser positivo")
    private Long idOrdenTrabajo;
}

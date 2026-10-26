package pe.edu.upeu.geovisionBackEnd.dto.elaboracionOrdenTrabajo;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/** Edicion de datos de la orden. La fecha se compara con su registro, no con el dia actual. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarOrdenTrabajoRequestDTO {

    private LocalDateTime fechaEntregaEstimada;

    @NotNull(message = "esUrgente es obligatorio")
    private Boolean esUrgente;

    @Size(max = 2000)
    private String descripcion;
}

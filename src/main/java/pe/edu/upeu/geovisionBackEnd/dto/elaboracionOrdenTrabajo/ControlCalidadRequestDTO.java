package pe.edu.upeu.geovisionBackEnd.dto.elaboracionOrdenTrabajo;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

/** Creacion en EN_REVISION. El responsable y el resultado se establecen en el backend. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ControlCalidadRequestDTO {

    @NotNull(message = "idEnvioLaboratorio es obligatorio")
    @Positive(message = "idEnvioLaboratorio debe ser positivo")
    private Long idEnvioLaboratorio;

    @Size(max = 2000)
    private String observaciones;
}

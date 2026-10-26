package pe.edu.upeu.geovisionBackEnd.dto.elaboracionOrdenTrabajo;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

/** El backend establece la fecha real de recepcion; no se recibe desde el cliente. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecepcionarLaboratorioRequestDTO {

    @NotNull(message = "idEnvioLaboratorio es obligatorio")
    @Positive(message = "idEnvioLaboratorio debe ser positivo")
    private Long idEnvioLaboratorio;
}

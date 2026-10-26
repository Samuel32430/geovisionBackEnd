package pe.edu.upeu.geovisionBackEnd.dto.elaboracionOrdenTrabajo;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import pe.edu.upeu.geovisionBackEnd.enums.elaboracionOrdenTrabajo.MotivoEnvioLaboratorio;

/** Registro del envio: fechaEnvio se establece en el backend y la recepcion es una operacion posterior. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnvioLaboratorioRequestDTO {

    @NotNull(message = "idOrdenTrabajo es obligatorio")
    @Positive(message = "idOrdenTrabajo debe ser positivo")
    private Long idOrdenTrabajo;

    @NotNull(message = "idLaboratorio es obligatorio")
    @Positive(message = "idLaboratorio debe ser positivo")
    private Long idLaboratorio;

    @NotNull(message = "motivo es obligatorio")
    private MotivoEnvioLaboratorio motivo = MotivoEnvioLaboratorio.FABRICACION;

    @Size(max = 1000)
    private String descripcion;

    @NotNull(message = "costoOptica es obligatorio")
    @Digits(integer = 12, fraction = 2)
    @DecimalMin("0")
    private BigDecimal costoOptica = BigDecimal.ZERO;
}

package pe.edu.upeu.geovisionBackEnd.dto.elaboracionOrdenTrabajo;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/** Solo se recibe el valor observado. El backend captura criterio, tolerancia y periodo y calcula la aprobacion. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParametroCalidadRequestDTO {

    @NotNull(message = "idControlCalidad es obligatorio")
    @Positive(message = "idControlCalidad debe ser positivo")
    private Long idControlCalidad;

    @NotNull(message = "idTipoParametroCalidad es obligatorio")
    @Positive(message = "idTipoParametroCalidad debe ser positivo")
    private Long idTipoParametroCalidad;

    @NotNull(message = "idMedidaOjo es obligatorio")
    @Positive(message = "idMedidaOjo debe ser positivo")
    private Long idMedidaOjo;

    @NotNull(message = "valorObtenido es obligatorio")
    @Digits(integer = 6, fraction = 4)
    private BigDecimal valorObtenido;
}

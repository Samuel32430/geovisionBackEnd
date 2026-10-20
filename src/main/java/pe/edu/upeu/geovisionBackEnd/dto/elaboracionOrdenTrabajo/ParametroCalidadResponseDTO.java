package pe.edu.upeu.geovisionBackEnd.dto.elaboracionOrdenTrabajo;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParametroCalidadResponseDTO {

    private Long idParametroCalidad;

    private BigDecimal tolerancia;

    private BigDecimal valorEsperado;

    private BigDecimal valorObtenido;

    private BigDecimal periodo;

    private Boolean aprobado;

    private Long idControlCalidad;

    private Long idTipoParametroCalidad;

    private Long idMedidaOjo;
}

package pe.edu.upeu.geovisionBackEnd.dto.gestionMantenimiento;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoParametroCalidadResponseDTO {

    private Long idTipoParametroCalidad;

    private String nombre;

    private String unidad;

    private BigDecimal tolerancia;

    private BigDecimal periodo;

    private BigDecimal valorEsperado;
}

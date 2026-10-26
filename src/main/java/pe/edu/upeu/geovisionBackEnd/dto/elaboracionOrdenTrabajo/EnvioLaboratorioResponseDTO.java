package pe.edu.upeu.geovisionBackEnd.dto.elaboracionOrdenTrabajo;

import pe.edu.upeu.geovisionBackEnd.enums.elaboracionOrdenTrabajo.MotivoEnvioLaboratorio;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnvioLaboratorioResponseDTO {

    private Long idEnvioLaboratorio;

    private LocalDateTime fechaEnvio;

    private LocalDateTime fechaRecepcion;

    private MotivoEnvioLaboratorio motivo;

    private String descripcion;

    private BigDecimal costoOptica;

    private Long idOrdenTrabajo;

    private Long idLaboratorio;
}

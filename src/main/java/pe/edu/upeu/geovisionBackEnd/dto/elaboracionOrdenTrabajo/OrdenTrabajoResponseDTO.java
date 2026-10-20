package pe.edu.upeu.geovisionBackEnd.dto.elaboracionOrdenTrabajo;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenTrabajoResponseDTO {

    private Long idOrdenTrabajo;

    private String numeroOrden;

    private LocalDateTime fechaRegistro;

    private LocalDateTime fechaEntregaEstimada;

    private LocalDateTime fechaEntregaPaciente;

    private LocalDateTime fechaNotificacion;

    private Boolean esUrgente;

    private String descripcion;

    private LocalDateTime fechaEntregaReal;

    private Long idLente;

    private Long idEmpleado;

    private Long idEstadoOrdenTrabajo;
}

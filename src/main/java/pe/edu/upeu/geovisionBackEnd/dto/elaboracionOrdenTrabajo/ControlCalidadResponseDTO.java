package pe.edu.upeu.geovisionBackEnd.dto.elaboracionOrdenTrabajo;

import pe.edu.upeu.geovisionBackEnd.enums.elaboracionOrdenTrabajo.EstadoControlCalidad;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ControlCalidadResponseDTO {

    private Long idControlCalidad;

    private LocalDateTime fechaRegistro;

    private EstadoControlCalidad estado;

    private String observaciones;

    private Boolean aprobado;

    private Long idEnvioLaboratorio;

    private Long idEmpleado;
}

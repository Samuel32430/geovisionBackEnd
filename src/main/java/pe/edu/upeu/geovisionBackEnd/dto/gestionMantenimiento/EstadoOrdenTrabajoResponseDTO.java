package pe.edu.upeu.geovisionBackEnd.dto.gestionMantenimiento;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoOrdenTrabajoResponseDTO {

    private Long idEstadoOrdenTrabajo;

    private String nombre;
}

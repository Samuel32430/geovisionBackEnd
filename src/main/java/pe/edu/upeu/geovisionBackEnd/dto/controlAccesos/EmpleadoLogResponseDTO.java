package pe.edu.upeu.geovisionBackEnd.dto.controlAccesos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoLogResponseDTO {

    private Long idEmpleadoLog;
    private LocalDateTime fechaLog;
    private Long idUsuario;
    private String nombreUsuario;
    private Long idPersona;
    private String nombreEmpleado;
}

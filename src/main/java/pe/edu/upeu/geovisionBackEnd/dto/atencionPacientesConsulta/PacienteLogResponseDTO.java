package pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PacienteLogResponseDTO {
    private Long idPacienteLog;
    private Long idPaciente;
    private LocalDateTime fechaLog;
}


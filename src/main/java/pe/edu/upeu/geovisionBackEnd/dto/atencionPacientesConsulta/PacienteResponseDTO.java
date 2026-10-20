package pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PacienteResponseDTO {
    private Long idPersona;
    private String nombres;
    private String apellidos;
    private String telefono;
    private Integer estado;
    private LocalDateTime fechaRegistro;
}


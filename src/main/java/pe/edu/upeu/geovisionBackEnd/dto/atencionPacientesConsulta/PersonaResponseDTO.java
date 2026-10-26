package pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class PersonaResponseDTO {
    private Long idPersona;
    private String nombres;
    private String apellidos;
    private String telefono;
    private LocalDate fechaNacimiento;
    private Integer sexo;
    private String direccion;
}


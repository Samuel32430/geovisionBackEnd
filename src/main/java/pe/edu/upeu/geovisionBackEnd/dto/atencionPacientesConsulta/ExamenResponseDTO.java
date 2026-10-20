package pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ExamenResponseDTO {
    private Long idExamen;
    private Long idPaciente;
    private LocalDateTime fechaRegistro;
    private Integer esExterno;
    private String profesionalExterno;
    private String observaciones;
}


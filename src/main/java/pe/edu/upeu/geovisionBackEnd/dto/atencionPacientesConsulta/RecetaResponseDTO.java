package pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RecetaResponseDTO {
    private Long idReceta;
    private Long idExamen;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaDeEntrega;
    private Integer esExterno;
    private String observaciones;
}


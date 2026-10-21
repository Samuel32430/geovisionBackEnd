package pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DocumentoResponseDTO {
    private Long idDocumento;
    private String numero;
    private Long idPersona;
    private Long idTipoDocumento;
}


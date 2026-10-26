package pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta;

import lombok.Data;
import java.util.List;

@Data
public class PacienteDetalleResponseDTO {
    private PacienteResponseDTO paciente;
    private PersonaResponseDTO persona;
    private List<DocumentoResponseDTO> documentos;
}


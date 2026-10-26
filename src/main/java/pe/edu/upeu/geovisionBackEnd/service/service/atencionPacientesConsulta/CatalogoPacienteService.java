package pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import java.util.List;

public interface CatalogoPacienteService {
    List<CatalogoPacienteResponseDTO> tiposDocumento();
    List<CatalogoPacienteResponseDTO> tiposMedida();
    List<CatalogoPacienteResponseDTO> tiposOjo();
}


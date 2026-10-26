package pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import java.util.List;

public interface DocumentoService {
    DocumentoResponseDTO create(@Valid @NotNull DocumentoRequestDTO request);
    DocumentoResponseDTO update(@Positive @NotNull Long id, @Valid @NotNull DocumentoRequestDTO request);
    DocumentoResponseDTO read(@Positive @NotNull Long id);
    List<DocumentoResponseDTO> listarPorPersona(@Positive @NotNull Long idPersona);
}


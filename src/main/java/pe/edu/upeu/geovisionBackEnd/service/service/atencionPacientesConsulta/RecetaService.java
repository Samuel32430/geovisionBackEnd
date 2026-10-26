package pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import java.util.List;

public interface RecetaService {
    RecetaResponseDTO create(@Valid @NotNull RecetaRequestDTO request);
    RecetaResponseDTO read(@Positive @NotNull Long id);
    RecetaResponseDTO registrarEntrega(@Positive @NotNull Long id, @Valid @NotNull RecetaEntregaRequestDTO request);
    PaginaResponse<RecetaResponseDTO> listarPorPaciente(@Positive @NotNull Long paciente, int pagina, int tamanio);
}


package pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import java.util.List;

public interface ExamenService {
    ExamenResponseDTO create(@Valid @NotNull ExamenRequestDTO request);
    ExamenResponseDTO update(@Positive @NotNull Long id, @Valid @NotNull ExamenRequestDTO request);
    ExamenDetalleResponseDTO read(@Positive @NotNull Long id);
    PaginaResponse<ExamenResponseDTO> listarPorPaciente(@Positive @NotNull Long paciente, int pagina, int tamanio);
}


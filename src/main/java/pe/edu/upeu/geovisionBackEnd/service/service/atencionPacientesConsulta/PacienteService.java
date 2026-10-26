package pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import java.util.List;

public interface PacienteService {
    PacienteDetalleResponseDTO registrar(@Valid @NotNull PacienteRegistroRequestDTO request);
    PacienteResponseDTO create(@Valid @NotNull PacienteRequestDTO request);
    PacienteDetalleResponseDTO update(@Positive @NotNull Long id, @Valid @NotNull PersonaRequestDTO request);
    PacienteDetalleResponseDTO read(@Positive @NotNull Long id);
    PaginaResponse<PacienteResponseDTO> listar(String termino, Integer estado, int pagina, int tamanio,
                                             String ordenarPor, String direccion);
    PaginaResponse<PacienteLogResponseDTO> listarLogs(@Positive @NotNull Long id, int pagina, int tamanio);
}


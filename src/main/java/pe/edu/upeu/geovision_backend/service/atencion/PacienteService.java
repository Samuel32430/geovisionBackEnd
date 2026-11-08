package pe.edu.upeu.geovision_backend.service.atencion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovision_backend.dto.atencion.PacienteRequestDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.PacienteResponseDTO;

public interface PacienteService {

    Page<PacienteResponseDTO> listarPacientes(String termino, Boolean estado, Pageable pageable);

    PacienteResponseDTO obtenerPorId(Long idPaciente);

    PacienteResponseDTO obtenerPorNumeroDocumento(String numeroDocumento);

    PacienteResponseDTO registrarPaciente(@NotNull @Valid PacienteRequestDTO request, String usernameAuditoria);

    PacienteResponseDTO actualizarPaciente(Long idPaciente, @NotNull @Valid PacienteRequestDTO request,
                                           String usernameAuditoria);

    PacienteResponseDTO cambiarEstadoPaciente(Long idPaciente, Integer nuevoEstado, String usernameAuditoria);
}

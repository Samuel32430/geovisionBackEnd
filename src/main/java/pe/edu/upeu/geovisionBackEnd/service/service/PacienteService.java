package pe.edu.upeu.geovisionBackEnd.service.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovisionBackEnd.dto.PacienteRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.PacienteResponseDTO;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;

public interface PacienteService {

    Page<PacienteResponseDTO> listarPacientes(String termino, EstadoGeneralEnum estado, Pageable pageable);

    PacienteResponseDTO obtenerPorId(Long idPaciente);

    PacienteResponseDTO obtenerPorNumeroDocumento(String numeroDocumento);

    PacienteResponseDTO registrarPaciente(@NotNull @Valid PacienteRequestDTO request, String usernameAuditoria);

    PacienteResponseDTO actualizarPaciente(Long idPaciente, @NotNull @Valid PacienteRequestDTO request,
                                           String usernameAuditoria);

    PacienteResponseDTO cambiarEstadoPaciente(Long idPaciente, Integer nuevoEstado, String usernameAuditoria);
}

package pe.edu.upeu.geovisionBackEnd.service.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovisionBackEnd.dto.RecetaRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.RecetaResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.LenteRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.LenteResponseDTO;

import java.util.List;

public interface RecetaService {

    RecetaResponseDTO registrarExamenVisual(@NotNull @Valid RecetaRequestDTO request, String usernameAuditoria);

    RecetaResponseDTO registrarRecetaExterna(@NotNull @Valid RecetaRequestDTO request, String usernameAuditoria);

    LenteResponseDTO configurarLente(Long idReceta, @NotNull @Valid LenteRequestDTO request);

    RecetaResponseDTO obtenerRecetaPorId(Long idReceta);

    List<RecetaResponseDTO> listarHistorialPorPaciente(Long idPaciente);

    Page<RecetaResponseDTO> listarRecetasPaginadas(Long idPaciente, Pageable pageable);
}

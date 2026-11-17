package pe.edu.upeu.geovision_backend.service.atencion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovision_backend.dto.atencion.RecetaRequestDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.RecetaResponseDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.LenteRequestDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.LenteResponseDTO;

import java.util.List;

public interface RecetaService {

    // >>>> QUERIES
    RecetaResponseDTO obtenerRecetaPorId(Long idReceta);

    List<RecetaResponseDTO> listarHistorialPorPaciente(Long idPaciente);

    Page<RecetaResponseDTO> listarRecetasPaginadas(Long idPaciente, Pageable pageable);

    // >>>> CREATION
    RecetaResponseDTO registrarExamenVisual(@NotNull @Valid RecetaRequestDTO request, String usernameAuditoria);

    RecetaResponseDTO registrarRecetaExterna(@NotNull @Valid RecetaRequestDTO request, String usernameAuditoria);

    // >>>> OPERATIONS
    LenteResponseDTO configurarLente(Long idReceta, @NotNull @Valid LenteRequestDTO request);

}

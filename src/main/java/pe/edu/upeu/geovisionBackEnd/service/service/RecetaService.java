package pe.edu.upeu.geovisionBackEnd.service.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import pe.edu.upeu.geovisionBackEnd.dto.RecetaRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.RecetaResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.LenteRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.LenteResponseDTO;

public interface RecetaService {

    RecetaResponseDTO registrarExamenVisual(@NotNull @Valid RecetaRequestDTO request, String usernameAuditoria);

    RecetaResponseDTO registrarRecetaExterna(@NotNull @Valid RecetaRequestDTO request, String usernameAuditoria);

    LenteResponseDTO configurarLente(Long idReceta, @NotNull @Valid LenteRequestDTO request);
}

package pe.edu.upeu.geovisionBackEnd.service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovisionBackEnd.dto.*;

public interface ControlCalidadService {
    Page<OrdenTrabajoResponseDTO> pendientes(Pageable pageable);

    EnvioLaboratorioResponseDTO registrar(ControlCalidadRequestDTO request, String usuario);

    PoliticaCalidadDTO politica();
}

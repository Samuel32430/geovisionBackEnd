package pe.edu.upeu.geovision_backend.service.orden_trabajo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.ControlCalidadRequestDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.EnvioLaboratorioResponseDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.OrdenTrabajoResponseDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.PoliticaCalidadDTO;

public interface ControlCalidadService {

    // >>>> QUERIES
    Page<OrdenTrabajoResponseDTO> pendientes(Pageable pageable);

    PoliticaCalidadDTO politica();

    // >>>> QUALITY CONTROL
    EnvioLaboratorioResponseDTO registrar(ControlCalidadRequestDTO request, String usuario);

}

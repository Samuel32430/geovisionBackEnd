package pe.edu.upeu.geovision_backend.service.security;

import pe.edu.upeu.geovision_backend.dto.security.RolDTO;

import java.util.List;
import java.util.Set;

public interface RolService {

    // >>>> QUERIES
    List<RolDTO> listar();

    RolDTO obtenerPorId(Long id);

    // >>>> CREATION
    RolDTO crear(RolDTO request);

    // >>>> UPDATES
    RolDTO asignarPermisos(Long idRol, Set<Long> permisoIds);

}

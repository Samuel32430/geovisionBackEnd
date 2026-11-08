package pe.edu.upeu.geovision_backend.service.security;

import pe.edu.upeu.geovision_backend.dto.security.RolDTO;

import java.util.List;
import java.util.Set;

public interface RolService {

    List<RolDTO> listar();

    RolDTO obtenerPorId(Long id);

    RolDTO crear(RolDTO request);

    RolDTO asignarPermisos(Long idRol, Set<Long> permisoIds);
}

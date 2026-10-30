package pe.edu.upeu.geovisionBackEnd.service.service;

import pe.edu.upeu.geovisionBackEnd.dto.RolDTO;

import java.util.List;
import java.util.Set;

public interface RolService {

    List<RolDTO> listar();

    RolDTO obtenerPorId(Long id);

    RolDTO crear(RolDTO request);

    RolDTO asignarPermisos(Long idRol, Set<Long> permisoIds);
}

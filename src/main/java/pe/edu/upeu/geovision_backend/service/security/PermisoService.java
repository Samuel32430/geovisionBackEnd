package pe.edu.upeu.geovision_backend.service.security;

import pe.edu.upeu.geovision_backend.dto.security.PermisoDTO;
import pe.edu.upeu.geovision_backend.enums.security.ModuloEnum;

import java.util.List;

public interface PermisoService {

    // >>>> QUERIES
    List<PermisoDTO> listar();

    List<PermisoDTO> listarPorModulo(ModuloEnum modulo);

    PermisoDTO obtenerPorId(Long id);

}

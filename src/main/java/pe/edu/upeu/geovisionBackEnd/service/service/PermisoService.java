package pe.edu.upeu.geovisionBackEnd.service.service;

import pe.edu.upeu.geovisionBackEnd.dto.PermisoDTO;
import pe.edu.upeu.geovisionBackEnd.enums.ModuloEnum;

import java.util.List;

public interface PermisoService {

    List<PermisoDTO> listar();

    List<PermisoDTO> listarPorModulo(ModuloEnum modulo);

    PermisoDTO obtenerPorId(Long id);
}

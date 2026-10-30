package pe.edu.upeu.geovisionBackEnd.service.service;

import pe.edu.upeu.geovisionBackEnd.dto.UsuarioRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.UsuarioResponseDTO;

import java.util.List;
import java.util.Set;

public interface UsuarioService {

    List<UsuarioResponseDTO> listar();

    UsuarioResponseDTO obtenerPorId(Long id);

    UsuarioResponseDTO crear(UsuarioRequestDTO request);

    UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO request);

    UsuarioResponseDTO cambiarEstado(Long id, Integer nuevoEstado, String authUsername);

    UsuarioResponseDTO asignarRoles(Long id, Set<Long> roleIds, String authUsername);
}

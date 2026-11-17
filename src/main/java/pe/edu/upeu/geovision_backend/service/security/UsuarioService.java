package pe.edu.upeu.geovision_backend.service.security;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovision_backend.dto.security.PersonaUsuarioResumenDTO;
import pe.edu.upeu.geovision_backend.dto.security.RegistroPersonaUsuarioRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.UsuarioRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.UsuarioResponseDTO;

import java.util.List;
import java.util.Set;

public interface UsuarioService {

    // >>>> QUERIES
    Page<PersonaUsuarioResumenDTO> buscarPersonasParaUsuario(String termino, boolean sinUsuario, Pageable pageable);

    List<UsuarioResponseDTO> listar();

    UsuarioResponseDTO obtenerPorId(Long id);

    // >>>> CREATION
    UsuarioResponseDTO registrarConPersona(RegistroPersonaUsuarioRequestDTO request);

    UsuarioResponseDTO crear(UsuarioRequestDTO request);

    // >>>> UPDATES
    UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO request);

    UsuarioResponseDTO cambiarEstado(Long id, Integer nuevoEstado, String authUsername);

    UsuarioResponseDTO asignarRoles(Long id, Set<Long> roleIds, String authUsername);

}

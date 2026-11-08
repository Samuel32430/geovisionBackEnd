package pe.edu.upeu.geovision_backend.dto.security;

import lombok.Builder;

import java.util.Set;

@Builder(toBuilder = true)
public record UsuarioResponseDTO(
    Long idUsuario,
    String nombre,
    String correo,
    Integer estado,
    String estadoDescripcion,
    Long idPersona,
    String nombreCompletoPersona,
    Set<RolDTO> roles
) {
}

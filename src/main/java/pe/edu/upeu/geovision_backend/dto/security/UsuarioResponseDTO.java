package pe.edu.upeu.geovision_backend.dto.security;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record UsuarioResponseDTO(
    Long id,
    String nombre,
    String correo,
    Integer estado,
    String estadoDescripcion,
    Long idPersona,
    String nombreCompletoPersona,
    Set<RolDTO> roles
) {
    @JsonProperty("idUsuario")
    public Long idUsuario() {
        return id;
    }
}

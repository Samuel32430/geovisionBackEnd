package pe.edu.upeu.geovision_backend.dto.security;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record LoginResponseDTO(
    String token,
    String tipoToken,
    Long id,
    String username,
    String correo,
    Long idPersona,
    String nombreCompleto,
    List<String> roles,
    List<String> permisos
) {
    @JsonProperty("idUsuario")
    public Long idUsuario() {
        return id;
    }
}

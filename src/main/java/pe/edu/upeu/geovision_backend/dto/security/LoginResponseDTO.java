package pe.edu.upeu.geovision_backend.dto.security;

import java.util.List;

public record LoginResponseDTO(
    String token,
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = pe.edu.upeu.geovision_backend.config.DtoDefaultsJackson2.Bearer.class)
    @tools.jackson.databind.annotation.JsonDeserialize(using = pe.edu.upeu.geovision_backend.config.DtoDefaultsJackson3.Bearer.class)
    String tipoToken,
    Long id,
    String username,
    String correo,
    Long idPersona,
    String nombreCompleto,
    List<String> roles,
    List<String> permisos
) {
}

package pe.edu.upeu.geovision_backend.dto.security;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(builder = LoginResponseDTO.LoginResponseDTOBuilder.class)
@tools.jackson.databind.annotation.JsonDeserialize(builder = LoginResponseDTO.LoginResponseDTOBuilder.class)
public record LoginResponseDTO(
    String token,
    String tipoToken,
    Long idUsuario,
    String username,
    String correo,
    Long idPersona,
    String nombreCompleto,
    List<String> roles,
    List<String> permisos
) {
    @com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "")
    @tools.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "")
    public static class LoginResponseDTOBuilder {
        private String tipoToken = "Bearer";
    }
}

package pe.edu.upeu.geovision_backend.dto.security;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record LogoutResponseDTO(
    String mensaje,
    Boolean exitoso,
    LocalDateTime fecha
) {
}

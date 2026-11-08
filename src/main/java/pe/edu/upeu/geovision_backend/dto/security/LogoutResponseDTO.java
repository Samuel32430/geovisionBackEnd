package pe.edu.upeu.geovision_backend.dto.security;

import java.time.LocalDateTime;

public record LogoutResponseDTO(
    String mensaje,
    Boolean exitoso,
    LocalDateTime fecha
) {
}

package pe.edu.upeu.geovision_backend.exception.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder(toBuilder = true)
public record ErrorResponseDTO(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path,
    Map<String, String> validationErrors
) {
}

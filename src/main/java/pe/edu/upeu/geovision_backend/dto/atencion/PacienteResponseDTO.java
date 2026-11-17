package pe.edu.upeu.geovision_backend.dto.atencion;

import java.time.LocalDateTime;

public record PacienteResponseDTO(
    Long id,
    PersonaResponseDTO persona,
    Integer estado,
    String estadoDescripcion,
    LocalDateTime fechaRegistro,
    Long idUsuario
) {
}

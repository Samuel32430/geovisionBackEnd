package pe.edu.upeu.geovision_backend.dto.atencion;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record PacienteResponseDTO(
    Long idPersona,
    PersonaResponseDTO persona,
    Integer estado,
    String estadoDescripcion,
    LocalDateTime fechaRegistro,
    Long idUsuario
) {
}

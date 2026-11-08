package pe.edu.upeu.geovision_backend.dto.atencion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.geovision_backend.enums.EstadoGeneralEnum;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PacienteResponseDTO {

    private Long idPersona;
    private PersonaResponseDTO persona;
    private Integer estado;
    private String estadoDescripcion;
    private LocalDateTime fechaRegistro;
    private Long idUsuario;
}

package pe.edu.upeu.geovision_backend.dto.atencion;

import java.time.LocalDateTime;
import java.util.List;

public record RecetaResponseDTO(
    Long id,
    LocalDateTime fechaRegistro,
    Integer esExterno,
    Boolean esRecetaExterna,
    Long idPaciente,
    String nombreCompletoPaciente,
    String numeroDocumentoPaciente,
    Long idEmpleado,
    String nombreCompletoEmpleado,
    List<MedidaResponseDTO> medidas,
    List<LenteResponseDTO> lentes
) {
    public Boolean esRecetaExterna() {
        if (esRecetaExterna != null) {
            return esRecetaExterna;
        }
        return esExterno != null && esExterno == 1;
    }
}

package pe.edu.upeu.geovision_backend.dto.atencion;

import pe.edu.upeu.geovision_backend.enums.atencion.SexoEnum;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoDocumentoEnum;

import java.time.LocalDate;

public record PersonaResponseDTO(
    Long id,
    String nombres,
    String apellidos,
    String nombreCompleto,
    String telefono,
    LocalDate fechaNacimiento,
    SexoEnum sexo,
    String sexoDescripcion,
    String direccion,
    TipoDocumentoEnum tipoDocumento,
    String tipoDocumentoDescripcion,
    String numeroDocumento
) {
    public String nombreCompleto() {
        if (nombreCompleto != null && !nombreCompleto.isBlank()) {
            return nombreCompleto;
        }
        if (nombres == null && apellidos == null) return null;
        return ((nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "")).trim();
    }
}

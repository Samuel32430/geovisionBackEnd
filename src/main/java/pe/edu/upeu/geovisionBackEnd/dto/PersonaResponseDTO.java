package pe.edu.upeu.geovisionBackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.geovisionBackEnd.enums.SexoEnum;
import pe.edu.upeu.geovisionBackEnd.enums.TipoDocumentoEnum;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaResponseDTO {

    private Long idPersona;
    private String nombres;
    private String apellidos;
    private String nombreCompleto;
    private String telefono;
    private LocalDate fechaNacimiento;
    private SexoEnum sexo;
    private String sexoDescripcion;
    private String direccion;
    private TipoDocumentoEnum tipoDocumento;
    private String tipoDocumentoDescripcion;
    private String numeroDocumento;

    public String getNombreCompleto() {
        if (nombreCompleto != null && !nombreCompleto.isBlank()) {
            return nombreCompleto;
        }
        if (nombres == null && apellidos == null) return null;
        return ((nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "")).trim();
    }
}

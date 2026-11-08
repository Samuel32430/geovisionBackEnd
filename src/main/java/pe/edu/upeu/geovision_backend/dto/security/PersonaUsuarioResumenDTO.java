package pe.edu.upeu.geovision_backend.dto.security;

import lombok.Builder;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoDocumentoEnum;

/** Proyección de selección: no contiene contraseñas, hashes ni datos clínicos. */

@Builder(toBuilder = true)
public record PersonaUsuarioResumenDTO(
    Long idPersona,
    String nombres,
    String apellidos,
    TipoDocumentoEnum tipoDocumento,
    String numeroDocumento,
    Long idUsuario,
    String nombreUsuario,
    Integer estadoUsuario,
    boolean esEmpleado,
    boolean empleadoActivo
) {
    public PersonaUsuarioResumenDTO(Long idPersona, String nombres, String apellidos,
            TipoDocumentoEnum tipoDocumento, String numeroDocumento, Long idUsuario,
            String nombreUsuario, Boolean estadoUsuario, boolean esEmpleado, boolean empleadoActivo) {
        this(idPersona, nombres, apellidos, tipoDocumento, numeroDocumento, idUsuario,
                nombreUsuario, estadoUsuario == null ? null : (estadoUsuario ? 1 : 0),
                esEmpleado, empleadoActivo);
    }

    @com.fasterxml.jackson.annotation.JsonProperty("nombreCompleto")
    public String nombreCompleto() { return (nombres + " " + apellidos).trim(); }

    @com.fasterxml.jackson.annotation.JsonProperty("tieneUsuario")
    public boolean tieneUsuario() { return idUsuario != null; }
}

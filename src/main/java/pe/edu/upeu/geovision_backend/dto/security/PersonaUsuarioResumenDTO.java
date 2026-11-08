package pe.edu.upeu.geovision_backend.dto.security;

import pe.edu.upeu.geovision_backend.enums.atencion.TipoDocumentoEnum;

/** Proyección de selección: no contiene contraseñas, hashes ni datos clínicos. */

public record PersonaUsuarioResumenDTO(
    Long id,
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
    @com.fasterxml.jackson.annotation.JsonProperty("nombreCompleto")
    public String nombreCompleto() { return (nombres + " " + apellidos).trim(); }

    @com.fasterxml.jackson.annotation.JsonProperty("tieneUsuario")
    public boolean tieneUsuario() { return idUsuario != null; }
}

package pe.edu.upeu.geovision_backend.dto.security;

import com.fasterxml.jackson.annotation.JsonProperty;

import pe.edu.upeu.geovision_backend.enums.atencion.TipoDocumentoEnum;

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
    @JsonProperty("nombreCompleto")
    public String nombreCompleto() { return (nombres + " " + apellidos).trim(); }

    @JsonProperty("tieneUsuario")
    public boolean tieneUsuario() { return idUsuario != null; }
}

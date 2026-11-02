package pe.edu.upeu.geovisionBackEnd.dto;

import lombok.Getter;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;
import pe.edu.upeu.geovisionBackEnd.enums.TipoDocumentoEnum;

/** Proyección de selección: no contiene contraseñas, hashes ni datos clínicos. */
@Getter
public class PersonaUsuarioResumenDTO {
    private final Long idPersona;
    private final String nombres;
    private final String apellidos;
    private final TipoDocumentoEnum tipoDocumento;
    private final String numeroDocumento;
    private final Long idUsuario;
    private final String nombreUsuario;
    private final Integer estadoUsuario;
    private final boolean esEmpleado;
    private final boolean empleadoActivo;

    public PersonaUsuarioResumenDTO(Long idPersona, String nombres, String apellidos,
            TipoDocumentoEnum tipoDocumento, String numeroDocumento, Long idUsuario,
            String nombreUsuario, EstadoGeneralEnum estadoUsuario, boolean esEmpleado, boolean empleadoActivo) {
        this.idPersona = idPersona;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.estadoUsuario = estadoUsuario == null ? null : estadoUsuario.getCodigo();
        this.esEmpleado = esEmpleado;
        this.empleadoActivo = empleadoActivo;
    }

    public String getNombreCompleto() { return (nombres + " " + apellidos).trim(); }
    public boolean getTieneUsuario() { return idUsuario != null; }
}

package pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.repository.controlAccesos.*;

/** Authentication.getName() must identify USUARIO.nombre; no client-supplied author IDs. */
@Component
@RequiredArgsConstructor
public class ActorPacienteActual {
    private final UsuarioRepository usuarios;
    private final EmpleadoRepository empleados;
    private final UsuarioRolRepository usuarioRoles;
    private final RolPermisoRepository rolPermisos;

    // Existing permission names must be configured by the security-module owner.
    @Value("${app.atencion.permiso-lectura:}")
    private String permisoLectura;
    @Value("${app.atencion.permiso-escritura:}")
    private String permisoEscritura;

    public record Actor(Long idEmpleado, Long idUsuario) {}

    public Actor lectura() { return verificar(permisoLectura); }
    public Actor escritura() { return verificar(permisoEscritura); }

    private Actor verificar(String permiso) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new AuthenticationCredentialsNotFoundException("Se requiere autenticacion");
        }
        var usuario = usuarios.findByNombre(auth.getName())
                .filter(u -> Integer.valueOf(1).equals(u.getEstado()))
                .orElseThrow(() -> new AccessDeniedException("Usuario no disponible"));
        var empleado = empleados.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .filter(e -> Integer.valueOf(1).equals(e.getEstado()))
                .orElseThrow(() -> new AccessDeniedException("Empleado no disponible"));
        if (permiso == null || permiso.isBlank() || usuarioRoles.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream().noneMatch(ur -> rolPermisos.findNombresPermisosByRolId(ur.getRol().getIdRol()).contains(permiso))) {
            throw new AccessDeniedException("Sin permiso configurado para esta operacion");
        }
        return new Actor(empleado.getIdPersona(), usuario.getIdUsuario());
    }
}


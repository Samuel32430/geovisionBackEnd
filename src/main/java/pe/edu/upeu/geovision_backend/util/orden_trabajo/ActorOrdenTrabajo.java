package pe.edu.upeu.geovision_backend.util.orden_trabajo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.security.access.AccessDeniedException;
import pe.edu.upeu.geovision_backend.entity.security.Empleado;
import pe.edu.upeu.geovision_backend.enums.EstadoGeneralEnum;
import pe.edu.upeu.geovision_backend.repository.security.EmpleadoRepository;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioRepository;

@Component @RequiredArgsConstructor
public class ActorOrdenTrabajo {
    private final UsuarioRepository usuarios;
    private final EmpleadoRepository empleados;
    public Empleado obtener(String nombre) {
        var usuario=usuarios.findByNombre(nombre).filter(u->u.getEstado()==EstadoGeneralEnum.ACTIVO)
            .orElseThrow(()->new AccessDeniedException("Se requiere usuario activo"));
        return empleados.findByUsuarioIdUsuario(usuario.getIdUsuario()).filter(e->e.getEstado()==EstadoGeneralEnum.ACTIVO)
            .orElseThrow(()->new AccessDeniedException("Se requiere un empleado activo vinculado al usuario"));
    }
}

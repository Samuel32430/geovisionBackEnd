package pe.edu.upeu.geovisionBackEnd.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.security.access.AccessDeniedException;
import pe.edu.upeu.geovisionBackEnd.entity.Empleado;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;
import pe.edu.upeu.geovisionBackEnd.repository.*;

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

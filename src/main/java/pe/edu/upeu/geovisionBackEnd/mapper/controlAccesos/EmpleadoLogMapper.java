package pe.edu.upeu.geovisionBackEnd.mapper.controlAccesos;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.EmpleadoLogResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Empleado;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.EmpleadoLog;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Usuario;

import java.time.LocalDateTime;

@Component
public class EmpleadoLogMapper {

    public EmpleadoLog toEntity(Usuario usuario, Empleado empleado) {
        if (usuario == null || empleado == null) {
            return null;
        }
        return EmpleadoLog.builder()
                .usuario(usuario)
                .empleado(empleado)
                .fechaLog(LocalDateTime.now())
                .build();
    }

    public EmpleadoLogResponseDTO toResponseDTO(EmpleadoLog entity) {
        if (entity == null) {
            return null;
        }
        String nombreEmpleado = null;
        if (entity.getEmpleado() != null && entity.getEmpleado().getPersona() != null) {
            nombreEmpleado = entity.getEmpleado().getPersona().getNombres() + " " + entity.getEmpleado().getPersona().getApellidos();
        }

        return EmpleadoLogResponseDTO.builder()
                .idEmpleadoLog(entity.getIdEmpleadoLog())
                .fechaLog(entity.getFechaLog())
                .idUsuario(entity.getUsuario() != null ? entity.getUsuario().getIdUsuario() : null)
                .nombreUsuario(entity.getUsuario() != null ? entity.getUsuario().getNombre() : null)
                .idPersona(entity.getEmpleado() != null ? entity.getEmpleado().getIdPersona() : null)
                .nombreEmpleado(nombreEmpleado)
                .build();
    }
}

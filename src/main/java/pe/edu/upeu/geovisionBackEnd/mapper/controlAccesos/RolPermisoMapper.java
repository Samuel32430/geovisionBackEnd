package pe.edu.upeu.geovisionBackEnd.mapper.controlAccesos;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.RolPermisoResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Permiso;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Rol;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.RolPermiso;

@Component
public class RolPermisoMapper {

    public RolPermiso toEntity(Rol rol, Permiso permiso) {
        if (rol == null || permiso == null) {
            return null;
        }
        return RolPermiso.builder()
                .rol(rol)
                .permiso(permiso)
                .build();
    }

    public RolPermisoResponseDTO toResponseDTO(RolPermiso entity) {
        if (entity == null) {
            return null;
        }
        return RolPermisoResponseDTO.builder()
                .idRolPermiso(entity.getIdRolPermiso())
                .idRol(entity.getRol() != null ? entity.getRol().getIdRol() : null)
                .nombreRol(entity.getRol() != null ? entity.getRol().getNombre() : null)
                .idPermiso(entity.getPermiso() != null ? entity.getPermiso().getIdPermiso() : null)
                .nombrePermiso(entity.getPermiso() != null ? entity.getPermiso().getNombre() : null)
                .build();
    }
}

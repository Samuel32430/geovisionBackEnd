package pe.edu.upeu.geovision_backend.mapper.security;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovision_backend.dto.security.PermisoDTO;
import pe.edu.upeu.geovision_backend.entity.security.Permiso;

@Component
public class PermisoMapper {

    public PermisoDTO toDTO(Permiso permiso) {
        if (permiso == null) {
            return null;
        }

        return new PermisoDTO(permiso.getId(), permiso.getNombre(), permiso.getModulo(), permiso.getModulo() != null ? permiso.getModulo().getDescripcion() : null);
    }

    public Permiso toEntity(PermisoDTO dto) {
        if (dto == null) {
            return null;
        }

        return new Permiso(dto.id(), dto.nombre(), dto.modulo());
    }
}

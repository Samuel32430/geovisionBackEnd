package pe.edu.upeu.geovision_backend.mapper.security;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovision_backend.dto.security.PermisoDTO;
import pe.edu.upeu.geovision_backend.dto.security.RolDTO;
import pe.edu.upeu.geovision_backend.entity.security.Rol;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RolMapper {

    private final PermisoMapper permisoMapper;

    public RolMapper(PermisoMapper permisoMapper) {
        this.permisoMapper = permisoMapper;
    }

    public RolDTO toDTO(Rol rol) {
        return toDTO(rol, Collections.emptySet());
    }

    public RolDTO toDTO(Rol rol, Set<PermisoDTO> permisos) {
        if (rol == null) {
            return null;
        }

        return new RolDTO(rol.getId(), rol.getNombre(), permisos != null ? permisos : Collections.emptySet());
    }

    public Rol toEntity(RolDTO dto) {
        if (dto == null) {
            return null;
        }

        return new Rol(dto.id(), dto.nombre());
    }
}

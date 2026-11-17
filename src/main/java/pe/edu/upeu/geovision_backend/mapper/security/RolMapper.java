package pe.edu.upeu.geovision_backend.mapper.security;

import pe.edu.upeu.geovision_backend.dto.security.PermisoDTO;
import pe.edu.upeu.geovision_backend.dto.security.RolDTO;
import pe.edu.upeu.geovision_backend.entity.security.Rol;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public final class RolMapper {
    private RolMapper() {}

    public static RolDTO toDTO(Rol rol) {
        return toDTO(rol, Collections.emptySet());
    }

    public static RolDTO toDTO(Rol rol, Set<PermisoDTO> permisos) {
        if (rol == null) {
            return null;
        }

        return new RolDTO(rol.getId(), rol.getNombre(), permisos != null ? permisos : Collections.emptySet());
    }

    public static Rol toEntity(RolDTO dto) {
        if (dto == null) {
            return null;
        }

        return new Rol(dto.id(), dto.nombre());
    }
}

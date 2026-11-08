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

        return RolDTO.builder()
                .idRol(rol.getIdRol())
                .nombre(rol.getNombre())
                .permisos(permisos != null ? permisos : Collections.emptySet())
                .build();
    }

    public Rol toEntity(RolDTO dto) {
        if (dto == null) {
            return null;
        }

        return Rol.builder()
                .idRol(dto.idRol())
                .nombre(dto.nombre())
                .build();
    }
}

package pe.edu.upeu.geovisionBackEnd.mapper.controlAccesos;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.PermisoResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.RolRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.RolResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Rol;

import java.util.Collections;
import java.util.List;

@Component
public class RolMapper {

    public Rol toEntity(RolRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Rol.builder()
                .nombre(dto.getNombre())
                .build();
    }

    public RolResponseDTO toResponseDTO(Rol entity) {
        if (entity == null) {
            return null;
        }
        return toResponseDTO(entity, Collections.emptyList());
    }

    public RolResponseDTO toResponseDTO(Rol entity, List<PermisoResponseDTO> permisos) {
        if (entity == null) {
            return null;
        }
        return RolResponseDTO.builder()
                .idRol(entity.getIdRol())
                .nombre(entity.getNombre())
                .permisos(permisos != null ? permisos : Collections.emptyList())
                .build();
    }
}

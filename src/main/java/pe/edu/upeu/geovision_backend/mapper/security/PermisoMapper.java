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

        return PermisoDTO.builder()
                .idPermiso(permiso.getIdPermiso())
                .nombre(permiso.getNombre())
                .modulo(permiso.getModulo())
                .moduloDescripcion(permiso.getModulo() != null ? permiso.getModulo().getDescripcion() : null)
                .build();
    }

    public Permiso toEntity(PermisoDTO dto) {
        if (dto == null) {
            return null;
        }

        return Permiso.builder()
                .idPermiso(dto.idPermiso())
                .nombre(dto.nombre())
                .modulo(dto.modulo())
                .build();
    }
}

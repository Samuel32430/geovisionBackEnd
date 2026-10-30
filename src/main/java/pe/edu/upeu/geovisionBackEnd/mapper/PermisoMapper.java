package pe.edu.upeu.geovisionBackEnd.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.PermisoDTO;
import pe.edu.upeu.geovisionBackEnd.entity.Permiso;

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
                .idPermiso(dto.getIdPermiso())
                .nombre(dto.getNombre())
                .modulo(dto.getModulo())
                .build();
    }
}

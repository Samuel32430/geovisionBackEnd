package pe.edu.upeu.geovisionBackEnd.mapper.controlAccesos;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.PermisoRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.PermisoResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Modulo;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Permiso;

@Component
public class PermisoMapper {

    public Permiso toEntity(PermisoRequestDTO dto, Modulo modulo) {
        if (dto == null) {
            return null;
        }
        return Permiso.builder()
                .nombre(dto.getNombre())
                .modulo(modulo)
                .build();
    }

    public PermisoResponseDTO toResponseDTO(Permiso entity) {
        if (entity == null) {
            return null;
        }
        return PermisoResponseDTO.builder()
                .idPermiso(entity.getIdPermiso())
                .nombre(entity.getNombre())
                .idModulo(entity.getModulo() != null ? entity.getModulo().getIdModulo() : null)
                .nombreModulo(entity.getModulo() != null ? entity.getModulo().getNombre() : null)
                .build();
    }
}

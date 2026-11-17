package pe.edu.upeu.geovision_backend.mapper.security;

import pe.edu.upeu.geovision_backend.dto.security.PermisoDTO;
import pe.edu.upeu.geovision_backend.entity.security.Permiso;

public final class PermisoMapper {
    private PermisoMapper() {}

    public static PermisoDTO toDTO(Permiso permiso) {
        if (permiso == null) {
            return null;
        }

        return new PermisoDTO(permiso.getId(), permiso.getNombre(), permiso.getModulo(), permiso.getModulo() != null ? permiso.getModulo().getDescripcion() : null);
    }

    public static Permiso toEntity(PermisoDTO dto) {
        if (dto == null) {
            return null;
        }

        return new Permiso(dto.id(), dto.nombre(), dto.modulo());
    }
}

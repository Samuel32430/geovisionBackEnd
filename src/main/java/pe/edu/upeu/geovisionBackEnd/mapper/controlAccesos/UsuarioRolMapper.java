package pe.edu.upeu.geovisionBackEnd.mapper.controlAccesos;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.UsuarioRolResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Rol;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Usuario;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.UsuarioRol;

@Component
public class UsuarioRolMapper {

    public UsuarioRol toEntity(Usuario usuario, Rol rol) {
        if (usuario == null || rol == null) {
            return null;
        }
        return UsuarioRol.builder()
                .usuario(usuario)
                .rol(rol)
                .build();
    }

    public UsuarioRolResponseDTO toResponseDTO(UsuarioRol entity) {
        if (entity == null) {
            return null;
        }
        return UsuarioRolResponseDTO.builder()
                .idUsuarioRol(entity.getIdUsuarioRol())
                .idUsuario(entity.getUsuario() != null ? entity.getUsuario().getIdUsuario() : null)
                .nombreUsuario(entity.getUsuario() != null ? entity.getUsuario().getNombre() : null)
                .idRol(entity.getRol() != null ? entity.getRol().getIdRol() : null)
                .nombreRol(entity.getRol() != null ? entity.getRol().getNombre() : null)
                .build();
    }
}

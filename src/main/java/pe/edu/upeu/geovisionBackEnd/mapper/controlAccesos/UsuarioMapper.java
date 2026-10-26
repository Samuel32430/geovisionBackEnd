package pe.edu.upeu.geovisionBackEnd.mapper.controlAccesos;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.UsuarioRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.UsuarioResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.UsuarioUpdateDTO;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Usuario;

import java.util.Collections;
import java.util.List;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Usuario.builder()
                .nombre(dto.getNombre())
                .password(dto.getPassword())
                .correo(dto.getCorreo())
                .estado(1)
                .build();
    }

    public UsuarioResponseDTO toResponseDTO(Usuario entity) {
        if (entity == null) {
            return null;
        }
        return toResponseDTO(entity, Collections.emptyList(), null, null);
    }

    public UsuarioResponseDTO toResponseDTO(Usuario entity, List<String> roles, Long idPersona, String nombreCompletoEmpleado) {
        if (entity == null) {
            return null;
        }
        return UsuarioResponseDTO.builder()
                .idUsuario(entity.getIdUsuario())
                .nombre(entity.getNombre())
                .correo(entity.getCorreo())
                .estado(entity.getEstado())
                .idPersona(idPersona)
                .nombreCompletoEmpleado(nombreCompletoEmpleado)
                .roles(roles != null ? roles : Collections.emptyList())
                .build();
    }

    public void updateEntityFromDTO(Usuario entity, UsuarioUpdateDTO dto) {
        if (entity == null || dto == null) {
            return;
        }
        if (dto.getNombre() != null) {
            entity.setNombre(dto.getNombre());
        }
        if (dto.getCorreo() != null) {
            entity.setCorreo(dto.getCorreo());
        }
        if (dto.getEstado() != null) {
            entity.setEstado(dto.getEstado());
        }
    }
}

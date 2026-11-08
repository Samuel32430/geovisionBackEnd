package pe.edu.upeu.geovision_backend.mapper.security;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovision_backend.dto.security.RolDTO;
import pe.edu.upeu.geovision_backend.dto.security.UsuarioResponseDTO;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.entity.security.Usuario;

import java.util.Collections;
import java.util.Set;

@Component
public class UsuarioMapper {

    public UsuarioResponseDTO toUsuarioResponse(Usuario usuario) {
        return toUsuarioResponse(usuario, Collections.emptySet());
    }

    public UsuarioResponseDTO toUsuarioResponse(Usuario usuario, Set<RolDTO> roles) {
        if (usuario == null) {
            return null;
        }

        Persona persona = usuario.getPersona();
        Long idPersona = persona != null ? persona.getIdPersona() : null;
        String nombreCompleto = null;
        if (persona != null) {
            String nombres = persona.getNombres() != null ? persona.getNombres().trim() : "";
            String apellidos = persona.getApellidos() != null ? persona.getApellidos().trim() : "";
            nombreCompleto = (nombres + " " + apellidos).trim();
        }

        Integer estadoCodigo = (usuario.getActivo() == null ? null : Boolean.TRUE.equals(usuario.getActivo()) ? 1 : 0);
        String estadoDesc = (Boolean.TRUE.equals(usuario.getActivo())) ? "Activo" : "Inactivo";

        return UsuarioResponseDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .estado(estadoCodigo)
                .estadoDescripcion(estadoDesc)
                .idPersona(idPersona)
                .nombreCompletoPersona(nombreCompleto)
                .roles(roles != null ? roles : Collections.emptySet())
                .build();
    }
}

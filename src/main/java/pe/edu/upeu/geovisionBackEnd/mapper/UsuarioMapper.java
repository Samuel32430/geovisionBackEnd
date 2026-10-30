package pe.edu.upeu.geovisionBackEnd.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.RolDTO;
import pe.edu.upeu.geovisionBackEnd.dto.UsuarioResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.Persona;
import pe.edu.upeu.geovisionBackEnd.entity.Usuario;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;

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

        Integer estadoCodigo = usuario.getEstadoCodigo();
        String estadoDesc = (usuario.getEstado() == EstadoGeneralEnum.ACTIVO) ? "Activo" : "Inactivo";

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

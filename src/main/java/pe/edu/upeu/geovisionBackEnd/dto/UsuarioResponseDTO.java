package pe.edu.upeu.geovisionBackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {

    private Long idUsuario;
    private String nombre;
    private String correo;
    private Integer estado;
    private String estadoDescripcion;
    private Long idPersona;
    private String nombreCompletoPersona;
    private Set<RolDTO> roles;
}

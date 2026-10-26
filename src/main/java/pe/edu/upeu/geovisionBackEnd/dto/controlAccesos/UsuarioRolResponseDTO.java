package pe.edu.upeu.geovisionBackEnd.dto.controlAccesos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRolResponseDTO {

    private Long idUsuarioRol;
    private Long idUsuario;
    private String nombreUsuario;
    private Long idRol;
    private String nombreRol;
}

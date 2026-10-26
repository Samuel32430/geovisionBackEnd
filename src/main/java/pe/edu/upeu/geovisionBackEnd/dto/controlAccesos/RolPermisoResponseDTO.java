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
public class RolPermisoResponseDTO {

    private Long idRolPermiso;
    private Long idRol;
    private String nombreRol;
    private Long idPermiso;
    private String nombrePermiso;
}

package pe.edu.upeu.geovisionBackEnd.dto.controlAccesos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    private String token;
    @Builder.Default
    private String tipoToken = "Bearer";
    private Long idUsuario;
    private String nombre;
    private String correo;
    private List<String> roles;
    private List<String> permisos;
    private Long idEmpleado;
    private String nombreCompleto;
}

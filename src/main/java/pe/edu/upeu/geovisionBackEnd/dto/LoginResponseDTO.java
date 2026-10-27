package pe.edu.upeu.geovisionBackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;

    @Builder.Default
    private String tipoToken = "Bearer";

    private Long idUsuario;
    private String username;
    private String correo;
    private Long idPersona;
    private String nombreCompleto;
    private List<String> roles;
    private List<String> permisos;
}

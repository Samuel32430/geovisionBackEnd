package pe.edu.upeu.geovisionBackEnd.dto.controlAccesos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class UsuarioRequestDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 200, message = "El nombre no debe exceder 200 caracteres")
    private String nombre;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 500, message = "La contraseña debe tener entre 6 y 500 caracteres")
    private String password;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato de correo es inválido")
    @Size(max = 200, message = "El correo no debe exceder 200 caracteres")
    private String correo;

    @Positive(message = "El identificador de persona debe ser positivo")
    private Long idPersona;

    private List<Long> idsRoles;
}

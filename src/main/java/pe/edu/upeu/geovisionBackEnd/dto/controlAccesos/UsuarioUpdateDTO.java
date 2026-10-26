package pe.edu.upeu.geovisionBackEnd.dto.controlAccesos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class UsuarioUpdateDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 200, message = "El nombre no debe exceder 200 caracteres")
    private String nombre;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato de correo es inválido")
    @Size(max = 200, message = "El correo no debe exceder 200 caracteres")
    private String correo;

    @Min(value = 0, message = "El estado debe ser 0 o 1")
    @Max(value = 1, message = "El estado debe ser 0 o 1")
    private Integer estado;

    private List<Long> idsRoles;
}

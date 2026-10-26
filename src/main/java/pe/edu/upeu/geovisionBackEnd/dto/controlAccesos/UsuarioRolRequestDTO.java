package pe.edu.upeu.geovisionBackEnd.dto.controlAccesos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class UsuarioRolRequestDTO {

    @NotNull(message = "El identificador de usuario es obligatorio")
    @Positive(message = "El identificador de usuario debe ser positivo")
    private Long idUsuario;

    @NotNull(message = "El identificador de rol es obligatorio")
    @Positive(message = "El identificador de rol debe ser positivo")
    private Long idRol;
}

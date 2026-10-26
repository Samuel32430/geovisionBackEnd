package pe.edu.upeu.geovisionBackEnd.dto.elaboracionOrdenTrabajo;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

/** El backend verifica saldo, calidad y estado, y establece la fecha real antes de entregar. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntregarOrdenTrabajoRequestDTO {

    @NotNull(message = "idOrdenTrabajo es obligatorio")
    @Positive(message = "idOrdenTrabajo debe ser positivo")
    private Long idOrdenTrabajo;
}

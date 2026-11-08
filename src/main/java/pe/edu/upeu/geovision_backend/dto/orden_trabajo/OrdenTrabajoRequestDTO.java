package pe.edu.upeu.geovision_backend.dto.orden_trabajo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenTrabajoRequestDTO {

    @NotNull(message = "El ID del lente es obligatorio")
    @jakarta.validation.constraints.Positive
    private Long idLente;

    @Size(max = 500, message = "La descripción no debe superar los 500 caracteres")
    private String descripcion;

}

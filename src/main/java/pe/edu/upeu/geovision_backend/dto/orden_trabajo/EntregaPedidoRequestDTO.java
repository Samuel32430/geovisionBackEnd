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
public class EntregaPedidoRequestDTO {

    @NotNull(message = "El ID de la orden de trabajo es obligatorio")
    @jakarta.validation.constraints.Positive
    private Long idOrdenTrabajo;

    // Opcional para compatibilidad; si se proporciona debe coincidir con la sesion.
    private Long idEmpleadoEntrega;

    @Size(max = 500, message = "Las observaciones no deben superar los 500 caracteres")
    private String observaciones;
}

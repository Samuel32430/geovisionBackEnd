package pe.edu.upeu.geovision_backend.dto.orden_trabajo;

import lombok.Builder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Builder(toBuilder = true)
public record EntregaPedidoRequestDTO(
    @NotNull(message = "El ID de la orden de trabajo es obligatorio")
    @jakarta.validation.constraints.Positive
    Long idOrdenTrabajo,
    // Opcional para compatibilidad; si se proporciona debe coincidir con la sesion.
    Long idEmpleadoEntrega,
    @Size(max = 500, message = "Las observaciones no deben superar los 500 caracteres")
    String observaciones
) {
}

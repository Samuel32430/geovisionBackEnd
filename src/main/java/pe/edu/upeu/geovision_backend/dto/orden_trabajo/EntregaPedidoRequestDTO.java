package pe.edu.upeu.geovision_backend.dto.orden_trabajo;

import jakarta.validation.constraints.Positive;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EntregaPedidoRequestDTO(
    @NotNull(message = "El ID de la orden de trabajo es obligatorio")
    @Positive
    Long idOrdenTrabajo,

    Long idEmpleadoEntrega,
    @Size(max = 500, message = "Las observaciones no deben superar los 500 caracteres")
    String observaciones
) {
}

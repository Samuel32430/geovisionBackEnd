package pe.edu.upeu.geovisionBackEnd.dto;

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
    private Long idOrdenTrabajo;

    @NotNull(message = "El ID del empleado que realiza la entrega es obligatorio")
    private Long idEmpleadoEntrega;

    @Size(max = 500, message = "Las observaciones no deben superar los 500 caracteres")
    private String observaciones;
}

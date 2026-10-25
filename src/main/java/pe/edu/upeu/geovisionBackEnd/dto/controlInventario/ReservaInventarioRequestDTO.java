package pe.edu.upeu.geovisionBackEnd.dto.controlInventario;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * DTO para solicitar el bloqueo preventivo de stock durante una venta concurrente (RF-81).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaInventarioRequestDTO {

    @NotNull(message = "El identificador del ítem es obligatorio")
    @Positive(message = "El identificador del ítem debe ser positivo")
    private Long idItem;

    @NotNull(message = "El identificador del ítem vendido es obligatorio")
    @Positive(message = "El identificador de ítem vendido debe ser positivo")
    private Long idItemVendido;

    @NotNull(message = "La cantidad a reservar es obligatoria")
    @Positive(message = "La cantidad a reservar debe ser mayor a cero")
    private Integer cantidad;
}

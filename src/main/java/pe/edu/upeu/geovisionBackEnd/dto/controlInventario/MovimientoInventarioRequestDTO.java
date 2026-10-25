package pe.edu.upeu.geovisionBackEnd.dto.controlInventario;

import jakarta.validation.constraints.*;
import lombok.*;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.MotivoMovimiento;

/**
 * DTO para solicitar un movimiento físico o ajuste en el inventario (RF-74, RF-75, RF-76).
 * El cliente siempre envía una cantidad positiva (> 0) y el tipo de ajuste o motivo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoInventarioRequestDTO {

    @NotNull(message = "El identificador del ítem es obligatorio")
    @Positive(message = "El identificador del ítem debe ser positivo")
    private Long idItem;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad registrada debe ser estrictamente mayor a cero (RF-76)")
    private Integer cantidad;

    @NotNull(message = "El motivo del movimiento es obligatorio")
    private MotivoMovimiento motivo;

    /**
     * Requerido únicamente si el motivo es AJUSTE para indicar si suma o resta existencias.
     * Si es null, para AJUSTE se asume decremento o incremento según la lógica de servicio.
     */
    private Boolean esIncrementoAjuste;

    @Size(max = 1000, message = "Las observaciones no deben superar los 1000 caracteres")
    private String observaciones;

    @Positive(message = "El identificador de ítem vendido debe ser positivo")
    private Long idItemVendido;

    @NotNull(message = "El empleado responsable es obligatorio")
    @Positive(message = "El identificador de empleado debe ser positivo")
    private Long idEmpleado;
}

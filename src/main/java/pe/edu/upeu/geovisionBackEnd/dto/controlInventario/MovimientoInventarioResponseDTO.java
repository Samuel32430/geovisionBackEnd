package pe.edu.upeu.geovisionBackEnd.dto.controlInventario;

import lombok.*;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.MotivoMovimiento;

import java.time.LocalDateTime;

/**
 * DTO para la consulta y trazabilidad inmutable del historial de movimientos (RF-79, RF-83).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoInventarioResponseDTO {

    private Long idMovimientoInventario;
    private LocalDateTime fechaRegistro;
    private Integer cantidad;
    private MotivoMovimiento motivo;
    private String observaciones;
    private Long idItem;
    private Long idItemVendido;
    private Long idEmpleado;
}

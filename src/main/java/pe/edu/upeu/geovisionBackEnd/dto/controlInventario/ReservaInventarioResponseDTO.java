package pe.edu.upeu.geovisionBackEnd.dto.controlInventario;

import lombok.*;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.EstadoReserva;

import java.time.LocalDateTime;

/**
 * DTO para la consulta y control de reservas activas o consumidas.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaInventarioResponseDTO {

    private Long idReservaInventario;
    private LocalDateTime fechaRegistro;
    private Integer cantidad;
    private EstadoReserva estado;
    private Long idItemVendido;
    private Long idItem;
}

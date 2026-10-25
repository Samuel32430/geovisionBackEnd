package pe.edu.upeu.geovisionBackEnd.dto.controlInventario;

import lombok.*;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.TipoItem;

import java.math.BigDecimal;

/**
 * DTO para la consulta rápida de disponibilidad y stock de un producto (RF-72, RF-73, RF-81).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemStockResponseDTO {

    private Long idItem;
    private String codigo;
    private String nombre;
    private TipoItem tipo;
    private BigDecimal precio;
    private Boolean controlaStock;
    private Integer stockFisico;
    private Integer stockReservado;
    private Integer stockDisponible;
    private Boolean disponibleParaVenta;
    private Boolean estado;
}

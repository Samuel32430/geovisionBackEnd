package pe.edu.upeu.geovisionBackEnd.dto.controlInventario;

import lombok.*;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.TipoItem;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.UnidadVenta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para la respuesta de consulta de un Item de catálogo (RF-66, RF-73).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponseDTO {

    private Long idItem;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private TipoItem tipo;
    private UnidadVenta unidadVenta;
    private Boolean controlaStock;
    private Boolean estado;
    private LocalDateTime fechaRegistro;
    private Integer stock;
    private Long idCategoria;
    private Long idMarca;
}

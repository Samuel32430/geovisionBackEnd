package pe.edu.upeu.geovision_backend.dto.inventario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoStockResponseDTO {

    private Long idProducto;
    private String codigo;
    private String nombre;
    private Integer stockActual;
    private Integer stockMinimoReferencial;
    private BigDecimal precio;
    private Boolean disponible;
    private Integer estado;
    private String estadoDescripcion;
}

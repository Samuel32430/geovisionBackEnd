package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoVendidoDTO {

    private Long idProductoVendido;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long idProducto;

    private String codigoProducto;
    private String nombreProducto;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @PositiveOrZero(message = "El precio unitario debe ser mayor o igual a cero")
    private BigDecimal precioUnit;

    private BigDecimal subTotal;

    public BigDecimal getSubTotal() {
        if (subTotal != null) {
            return subTotal;
        }
        if (precioUnit != null && cantidad != null) {
            return precioUnit.multiply(BigDecimal.valueOf(cantidad));
        }
        return BigDecimal.ZERO;
    }
}

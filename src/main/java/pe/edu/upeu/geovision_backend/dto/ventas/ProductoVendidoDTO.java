package pe.edu.upeu.geovision_backend.dto.ventas;

import lombok.Builder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record ProductoVendidoDTO(
    Long idProductoVendido,
    @NotNull(message = "El ID del producto es obligatorio")
    Long idProducto,
    String codigoProducto,
    String nombreProducto,
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    Integer cantidad,
    @NotNull(message = "El precio unitario es obligatorio")
    @PositiveOrZero(message = "El precio unitario debe ser mayor o igual a cero")
    BigDecimal precioUnit,
    BigDecimal subTotal
) {
    public BigDecimal subTotal() {
        if (subTotal != null) {
            return subTotal;
        }
        if (precioUnit != null && cantidad != null) {
            return precioUnit.multiply(BigDecimal.valueOf(cantidad));
        }
        return BigDecimal.ZERO;
    }
}

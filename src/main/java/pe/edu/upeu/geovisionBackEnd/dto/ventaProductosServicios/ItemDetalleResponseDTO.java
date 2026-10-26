package pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDetalleResponseDTO {

    private String descripcion;     // Nombre del producto, montura, cristales o servicio
    private String subtexto;        // Detalle secundario (ej. Tratamiento Antireflex, SKU, etc.)
    private Integer cantidad;       // Cantidad de unidades
    private BigDecimal precioUnit;  // Precio unitario
    private BigDecimal total;       // Subtotal del ítem
}

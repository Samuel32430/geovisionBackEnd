package pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemVendidoResponseDTO {

    private Long idItemVendido;
    private Long itemId;
    private String codigoItem;
    private String nombreItem;
    private String descripcion;
    private Integer cantidad;
    private BigDecimal precioUnit;
    private BigDecimal descuento;
    private BigDecimal subTotal;
    private Long idLente;
}

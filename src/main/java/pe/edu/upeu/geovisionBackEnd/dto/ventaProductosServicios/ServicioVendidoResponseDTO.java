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
public class ServicioVendidoResponseDTO {

    private Long idServicioVendido;
    private Long servicioId;
    private String nombreServicio;
    private String descripcion;
    private Integer cantidad;
    private BigDecimal precio;
    private BigDecimal subTotal;
}

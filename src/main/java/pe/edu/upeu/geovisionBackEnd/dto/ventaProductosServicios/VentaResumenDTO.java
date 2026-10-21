package pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VentaResumenDTO {

    private Long idVenta;
    private LocalDateTime fechaRegistro;
    private String comprobanteSerieNumero;
    private String clienteNombre;
    private BigDecimal total;
    private String estadoVenta;
    private String estadoCobro;
    private BigDecimal totalPagado;
    private BigDecimal saldoPendiente;
}

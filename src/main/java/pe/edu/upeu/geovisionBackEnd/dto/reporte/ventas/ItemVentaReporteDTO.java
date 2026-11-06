package pe.edu.upeu.geovisionBackEnd.dto.reporte.ventas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemVentaReporteDTO {
    private Long idVenta;
    private LocalDateTime fechaRegistro;
    private String numeroComprobante;
    private String tipoComprobante;
    private String estado;
    private BigDecimal total;
    private BigDecimal totalPagado;
    private BigDecimal saldoPendiente;
    private String condicionPago;
    private String docCliente;
    private String nombreCliente;
    private String nombreVendedor;
    private Integer cantidadItems;
}

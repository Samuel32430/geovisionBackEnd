package pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaldoPendienteDTO {

    private Long idVenta;
    private Long idComprobante;
    private String serieNumero;
    private String clienteNombre;
    private LocalDate fechaEmision;
    private BigDecimal totalVenta;
    private BigDecimal totalPagado;
    private BigDecimal saldoPendiente;
    private String estadoCobro;
}

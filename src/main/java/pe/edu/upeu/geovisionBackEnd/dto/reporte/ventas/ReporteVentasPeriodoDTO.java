package pe.edu.upeu.geovisionBackEnd.dto.reporte.ventas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.geovisionBackEnd.dto.reporte.KpiCardDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVentasPeriodoDTO {
    private List<KpiCardDTO> kpis;
    private BigDecimal totalVendido;
    private BigDecimal totalCobrado;
    private BigDecimal totalSaldoPendiente;
    private Integer totalVentas;
    private BigDecimal ticketPromedio;
    private Map<String, BigDecimal> totalPorMetodoPago;
    private List<ItemVentaReporteDTO> ventas;
}

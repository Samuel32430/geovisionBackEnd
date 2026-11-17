package pe.edu.upeu.geovision_backend.dto.reports;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ReporteVentasPeriodoDTO(
    List<KpiCardDTO> kpis,
    BigDecimal totalVendido,
    BigDecimal totalCobrado,
    BigDecimal totalSaldoPendiente,
    long totalVentas,
    BigDecimal ticketPromedio,
    Map<String, BigDecimal> totalPorMetodoPago,
    List<ItemVentaReporteDTO> ventas
) {
}

package pe.edu.upeu.geovision_backend.dto.reports;

import java.math.BigDecimal;
import java.util.List;

public record ReporteRankingComercialDTO(
    List<KpiCardDTO> kpis,
    long totalUnidadesVendidas,
    BigDecimal totalRecaudado,
    List<ItemRankingComercialDTO> items
) {
}

package pe.edu.upeu.geovisionBackEnd.dto.reporte.ventas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.geovisionBackEnd.dto.reporte.KpiCardDTO;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteRankingComercialDTO {
    private List<KpiCardDTO> kpis;
    private Long totalUnidadesVendidas;
    private BigDecimal totalRecaudado;
    private List<ItemRankingComercialDTO> items;
}

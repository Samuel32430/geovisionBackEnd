package pe.edu.upeu.geovision_backend.dto.reports;

import java.math.BigDecimal;

public record ItemRankingComercialDTO(
    int ranking,
    String tipoItem,
    Long idItem,
    String codigo,
    String nombre,
    String categoria,
    BigDecimal precioUnitario,
    long unidadesVendidas,
    BigDecimal totalRecaudado
) {
}

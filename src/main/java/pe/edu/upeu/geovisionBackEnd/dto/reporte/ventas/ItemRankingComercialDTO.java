package pe.edu.upeu.geovisionBackEnd.dto.reporte.ventas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRankingComercialDTO {
    private Integer ranking;
    private String tipoItem; // "PRODUCTO" o "SERVICIO"
    private Long idItem;
    private String codigo;
    private String nombre;
    private String categoria;
    private BigDecimal precioUnitario;
    private Long unidadesVendidas;
    private BigDecimal totalRecaudado;
}

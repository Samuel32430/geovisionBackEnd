package pe.edu.upeu.geovisionBackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedidaResponseDTO {

    private Long idMedida;
    private BigDecimal distanciaPupilar;
    private BigDecimal prisma;
    private String observaciones;
    private Long idReceta;
    private MedidaOjoDTO ojoDerecho;
    private MedidaOjoDTO ojoIzquierdo;
    private Long idEnvioLaboratorio;
}

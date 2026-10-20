package pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class MedidaOjoResponseDTO {
    private Long idMedidaOjo;
    private Long idMedida;
    private Long idTipoOjo;
    private BigDecimal agudezaVisual;
    private BigDecimal esfera;
    private BigDecimal cilindro;
    private BigDecimal eje;
    private BigDecimal adicion;
    private BigDecimal distanciaPupilarMm;
    private BigDecimal prisma;
    private String basePrisma;
}


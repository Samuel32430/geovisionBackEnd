package pe.edu.upeu.geovision_backend.dto.atencion;

import lombok.Builder;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoMedidaEnum;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record MedidaResponseDTO(
    Long idMedida,
    TipoMedidaEnum tipoMedida,
    BigDecimal distanciaPupilar,
    BigDecimal prisma,
    String observaciones,
    Long idReceta,
    MedidaOjoDTO ojoDerecho,
    MedidaOjoDTO ojoIzquierdo,
    Long idEnvioLaboratorio
) {
}

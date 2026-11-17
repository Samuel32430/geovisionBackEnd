package pe.edu.upeu.geovision_backend.dto.atencion;

import pe.edu.upeu.geovision_backend.enums.atencion.TipoMedidaEnum;

import java.math.BigDecimal;

public record MedidaResponseDTO(
    Long id,
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

package pe.edu.upeu.geovision_backend.dto.orden_trabajo;
import java.math.BigDecimal;
public record PoliticaCalidadDTO(boolean configurada, BigDecimal dioptrias, BigDecimal ejeGrados,
        BigDecimal distanciaPupilarMm, BigDecimal prisma
) {}

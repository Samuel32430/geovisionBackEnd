package pe.edu.upeu.geovisionBackEnd.dto;
import java.math.BigDecimal;
public record PoliticaCalidadDTO(boolean configurada, BigDecimal dioptrias, BigDecimal ejeGrados,
        BigDecimal distanciaPupilarMm, BigDecimal prisma) {}

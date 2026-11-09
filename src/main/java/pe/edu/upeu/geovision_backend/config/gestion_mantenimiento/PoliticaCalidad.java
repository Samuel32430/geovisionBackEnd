package pe.edu.upeu.geovision_backend.config.gestion_mantenimiento;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.PoliticaCalidadDTO;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;

@Component
@RequiredArgsConstructor
public class PoliticaCalidad {

    // >>>> CONFIGURATION
    @Value("${app.calidad.dioptrias:}")
    private final String dioptrias;
    @Value("${app.calidad.eje-grados:}")
    private final String eje;
    @Value("${app.calidad.distancia-pupilar-mm:}")
    private final String dp;
    @Value("${app.calidad.prisma:}")
    private final String prisma;

    // >>>> HELPERS
    private BigDecimal valor(String value) {
        try {
            if (value == null || value.isBlank()) return null;
            var n = new BigDecimal(value);
            return n.signum() >= 0 && n.compareTo(new BigDecimal("999.99")) <= 0 && n.stripTrailingZeros().scale() <= 2 ? n : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // >>>> OPERATIONS
    public PoliticaCalidadDTO consultar() {
        BigDecimal d = valor(dioptrias), e = valor(eje), p = valor(dp), pr = valor(prisma);
        boolean configurada = d != null && e != null && p != null && pr != null && e.compareTo(new BigDecimal("90")) <= 0;
        return new PoliticaCalidadDTO(configurada, d, e, p, pr);
    }

    public PoliticaCalidadDTO exigir() {
        var politica = consultar();
        if (!politica.configurada())
            throw new ReglaNegocioException("Configure todas las tolerancias de calidad antes de evaluar lensometria.");
        return politica;
    }
}

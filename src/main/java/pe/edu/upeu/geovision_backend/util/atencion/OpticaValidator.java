package pe.edu.upeu.geovision_backend.util.atencion;

import pe.edu.upeu.geovision_backend.dto.atencion.MedidaOjoDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.MedidaRequestDTO;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.util.List;

public final class OpticaValidator {
    private OpticaValidator() {}


    // >>>> CONSTANTS
    private static final BigDecimal PASO_DIOPTRICO = new BigDecimal("0.25");

    // >>>> VALIDATION
    public static void validarMedidas(List<MedidaRequestDTO> medidas) {
        for (MedidaRequestDTO medida : medidas) {
            validarOjo(medida.ojoDerecho());
            validarOjo(medida.ojoIzquierdo());
        }
    }

    // >>>> HELPERS
    private static void validarOjo(MedidaOjoDTO ojo) {
        validarPaso(ojo.esfera(), "esfera");
        validarPaso(ojo.cilindro(), "cilindro");
        validarPaso(ojo.adicion(), "adición");
        if (ojo.eje() != null && (ojo.eje() < 0 || ojo.eje() > 180)) {
            throw new ReglaNegocioException("El eje debe estar entre 0 y 180 grados");
        }
    }

    private static void validarPaso(BigDecimal valor, String campo) {
        if (valor != null && valor.remainder(PASO_DIOPTRICO).compareTo(BigDecimal.ZERO) != 0) {
            throw new ReglaNegocioException("El valor de " + campo + " debe ser múltiplo de 0.25");
        }
    }
}

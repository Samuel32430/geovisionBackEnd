package pe.edu.upeu.geovisionBackEnd.util;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.MedidaOjoDTO;
import pe.edu.upeu.geovisionBackEnd.dto.MedidaRequestDTO;
import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OpticaValidator {

    private static final BigDecimal PASO_DIOPTRICO = new BigDecimal("0.25");

    public void validarMedidas(List<MedidaRequestDTO> medidas) {
        for (MedidaRequestDTO medida : medidas) {
            validarOjo(medida.getOjoDerecho());
            validarOjo(medida.getOjoIzquierdo());
        }
    }

    private void validarOjo(MedidaOjoDTO ojo) {
        validarPaso(ojo.getEsfera(), "esfera");
        validarPaso(ojo.getCilindro(), "cilindro");
        validarPaso(ojo.getAdicion(), "adición");
        if (ojo.getEje() != null && (ojo.getEje() < 0 || ojo.getEje() > 180)) {
            throw new ReglaNegocioException("El eje debe estar entre 0 y 180 grados");
        }
    }

    private void validarPaso(BigDecimal valor, String campo) {
        if (valor != null && valor.remainder(PASO_DIOPTRICO).compareTo(BigDecimal.ZERO) != 0) {
            throw new ReglaNegocioException("El valor de " + campo + " debe ser múltiplo de 0.25");
        }
    }
}

package pe.edu.upeu.geovisionBackEnd.util;

import java.math.BigDecimal;
import java.util.*;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.*;
import pe.edu.upeu.geovisionBackEnd.entity.*;
import pe.edu.upeu.geovisionBackEnd.enums.TipoMedidaEnum;
import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;

/**
 * Compara la misma convencion de cilindro; no transforma ni modifica la receta.
 */
@Component
public class ComparadorLensometria {
    public boolean comparar(List<Medida> referencia, List<MedidaRequestDTO> control, PoliticaCalidadDTO p) {
        if (!p.configurada()) throw new ReglaNegocioException("Politica de calidad no configurada");
        var referencias = new EnumMap<TipoMedidaEnum, Medida>(TipoMedidaEnum.class);
        for (var m : referencia) {
            if (m.getTipoMedida() == null || referencias.put(m.getTipoMedida(), m) != null)
                throw new ReglaNegocioException("Las medidas del lente deben tener tipos unicos");
        }
        if (referencias.isEmpty() || control == null || control.size() != referencias.size())
            throw new ReglaNegocioException("Registre lensometria para todos los tipos de medida asociados al lente");
        var tipos = new HashSet<TipoMedidaEnum>();
        boolean aprobado = true;
        for (var m : control) {
            if (m == null || m.getTipoMedida() == null || !tipos.add(m.getTipoMedida()) || !referencias.containsKey(m.getTipoMedida()))
                throw new ReglaNegocioException("La lensometria contiene tipos repetidos o ajenos al lente");
            if (m.getIdReceta() != null || m.getIdEnvioLaboratorio() != null || m.getOjoDerecho() == null || m.getOjoIzquierdo() == null
                    || m.getOjoDerecho().getIdMedidaOjo() != null || m.getOjoIzquierdo().getIdMedidaOjo() != null)
                throw new ReglaNegocioException("Ingrese medidas nuevas de ambos ojos sin identificadores internos");
            var original = referencias.get(m.getTipoMedida());
            validarOriginal(original);
            validarControl(m.getOjoDerecho());
            validarControl(m.getOjoIzquierdo());
            if (m.getDistanciaPupilar() == null || m.getDistanciaPupilar().signum() <= 0)
                throw new ReglaNegocioException("La distancia pupilar medida debe ser positiva");
            aprobado &= diferencia(original.getDistanciaPupilar(), m.getDistanciaPupilar(), p.distanciaPupilarMm());
            aprobado &= diferencia(original.getPrisma(), m.getPrisma(), p.prisma());
            aprobado &= ojo(original.getMedidaOjoDerecha(), m.getOjoDerecho(), p);
            aprobado &= ojo(original.getMedidaOjoIzquierda(), m.getOjoIzquierdo(), p);
        }
        return aprobado;
    }

    public void validarOriginal(Medida m) {
        if (m == null || m.getDistanciaPupilar() == null || m.getDistanciaPupilar().signum() <= 0
                || !completo(m.getMedidaOjoDerecha()) || !completo(m.getMedidaOjoIzquierda()))
            throw new ReglaNegocioException("El lente debe tener medidas completas de ambos ojos y distancia pupilar");
    }

    private boolean completo(MedidaOjo o) {
        return o != null && o.getEsfera() != null && o.getCilindro() != null && o.getEje() != null && o.getEje() >= 0 && o.getEje() <= 180;
    }

    private void validarControl(MedidaOjoDTO o) {
        if (o.getEsfera() == null || o.getCilindro() == null || o.getEje() == null || o.getEje() < 0 || o.getEje() > 180)
            throw new ReglaNegocioException("La lensometria requiere esfera, cilindro y eje validos para ambos ojos");
    }

    private boolean ojo(MedidaOjo r, MedidaOjoDTO c, PoliticaCalidadDTO p) {
        int diff = Math.abs(r.getEje() - c.getEje());
        boolean cumple = diferencia(r.getEsfera(), c.getEsfera(), p.dioptrias());
        cumple &= diferencia(r.getCilindro(), c.getCilindro(), p.dioptrias());
        cumple &= diferencia(r.getAdicion(), c.getAdicion(), p.dioptrias());
        cumple &= BigDecimal.valueOf(Math.min(diff, 180 - diff)).compareTo(p.ejeGrados()) <= 0;
        return cumple;
    }

    private boolean diferencia(BigDecimal referencia, BigDecimal medida, BigDecimal tolerancia) {
        if (referencia == null) return medida == null || medida.signum() == 0;
        if (medida == null) throw new ReglaNegocioException("Complete todas las magnitudes presentes en la receta");
        return referencia.subtract(medida).abs().compareTo(tolerancia) <= 0;
    }
}

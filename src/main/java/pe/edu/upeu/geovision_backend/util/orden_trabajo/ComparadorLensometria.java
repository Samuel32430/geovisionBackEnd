package pe.edu.upeu.geovision_backend.util.orden_trabajo;

import java.math.BigDecimal;
import java.util.*;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovision_backend.dto.atencion.MedidaOjoDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.MedidaRequestDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.PoliticaCalidadDTO;
import pe.edu.upeu.geovision_backend.entity.atencion.Medida;
import pe.edu.upeu.geovision_backend.entity.atencion.MedidaOjo;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoMedidaEnum;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;

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
            if (m == null || m.tipoMedida() == null || !tipos.add(m.tipoMedida()) || !referencias.containsKey(m.tipoMedida()))
                throw new ReglaNegocioException("La lensometria contiene tipos repetidos o ajenos al lente");
            if (m.idReceta() != null || m.idEnvioLaboratorio() != null || m.ojoDerecho() == null || m.ojoIzquierdo() == null
                    || m.ojoDerecho().idMedidaOjo() != null || m.ojoIzquierdo().idMedidaOjo() != null)
                throw new ReglaNegocioException("Ingrese medidas nuevas de ambos ojos sin identificadores internos");
            var original = referencias.get(m.tipoMedida());
            validarOriginal(original);
            validarControl(m.ojoDerecho());
            validarControl(m.ojoIzquierdo());
            if (m.distanciaPupilar() == null || m.distanciaPupilar().signum() <= 0)
                throw new ReglaNegocioException("La distancia pupilar medida debe ser positiva");
            aprobado &= diferencia(original.getDistanciaPupilar(), m.distanciaPupilar(), p.distanciaPupilarMm());
            aprobado &= diferencia(original.getPrisma(), m.prisma(), p.prisma());
            aprobado &= ojo(original.getMedidaOjoDerecha(), m.ojoDerecho(), p);
            aprobado &= ojo(original.getMedidaOjoIzquierda(), m.ojoIzquierdo(), p);
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
        if (o.esfera() == null || o.cilindro() == null || o.eje() == null || o.eje() < 0 || o.eje() > 180)
            throw new ReglaNegocioException("La lensometria requiere esfera, cilindro y eje validos para ambos ojos");
    }

    private boolean ojo(MedidaOjo r, MedidaOjoDTO c, PoliticaCalidadDTO p) {
        int diff = Math.abs(r.getEje() - c.eje());
        boolean cumple = diferencia(r.getEsfera(), c.esfera(), p.dioptrias());
        cumple &= diferencia(r.getCilindro(), c.cilindro(), p.dioptrias());
        cumple &= diferencia(r.getAdicion(), c.adicion(), p.dioptrias());
        cumple &= BigDecimal.valueOf(Math.min(diff, 180 - diff)).compareTo(p.ejeGrados()) <= 0;
        return cumple;
    }

    private boolean diferencia(BigDecimal referencia, BigDecimal medida, BigDecimal tolerancia) {
        if (referencia == null) return medida == null || medida.signum() == 0;
        if (medida == null) throw new ReglaNegocioException("Complete todas las magnitudes presentes en la receta");
        return referencia.subtract(medida).abs().compareTo(tolerancia) <= 0;
    }
}

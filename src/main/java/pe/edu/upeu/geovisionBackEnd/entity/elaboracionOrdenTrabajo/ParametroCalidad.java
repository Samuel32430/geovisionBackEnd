package pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import org.hibernate.type.NumericBooleanConverter;
import pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento.TipoParametroCalidad;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.MedidaOjo;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "PARAMETRO_CALIDAD", uniqueConstraints = @UniqueConstraint(
        name = "PARAM_CALIDAD_par_uq", columnNames = {"id_control_calidad", "id_tipo_parametro_calidad", "id_medida_ojo"}))
public class ParametroCalidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parametro_calidad")
    private Long idParametroCalidad;

    @Column(name = "tolerancia", nullable = false, precision = 10, scale = 4)
    private BigDecimal tolerancia;

    @Column(name = "valor_esperado", nullable = false, precision = 10, scale = 4)
    private BigDecimal valorEsperado;

    @Column(name = "valor_obtenido", nullable = false, precision = 10, scale = 4)
    private BigDecimal valorObtenido;

    @Column(name = "periodo", precision = 10, scale = 4)
    private BigDecimal periodo;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "aprobado", nullable = false)
    private Boolean aprobado = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_control_calidad", nullable = false)
    private ControlCalidad controlCalidad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tipo_parametro_calidad", nullable = false)
    private TipoParametroCalidad tipoParametroCalidad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_medida_ojo", nullable = false)
    private MedidaOjo medidaOjo;

    /**
     * Captura los valores del criterio vigente. Los cambios posteriores del catalogo
     * no deben modificar las mediciones historicas.
     */
    public static ParametroCalidad crear(ControlCalidad control, TipoParametroCalidad criterio,
                                         MedidaOjo ojo, BigDecimal valorObtenido) {
        if (control == null || criterio == null || ojo == null) {
            throw new IllegalArgumentException("Control, criterio y medida de ojo son obligatorios");
        }
        validarPertenencia(control, ojo);
        var parametro = new ParametroCalidad();
        parametro.setControlCalidad(control);
        parametro.setTipoParametroCalidad(criterio);
        parametro.setMedidaOjo(ojo);
        parametro.setTolerancia(criterio.getTolerancia());
        parametro.setValorEsperado(criterio.getValorEsperado());
        parametro.setPeriodo(criterio.getPeriodo());
        parametro.setValorObtenido(valorObtenido);
        parametro.recalcularAprobacion();
        return parametro;
    }

    private static void validarPertenencia(ControlCalidad control, MedidaOjo ojo) {
        if (control.getEnvioLaboratorio() == null
                || control.getEnvioLaboratorio().getOrdenTrabajo() == null
                || control.getEnvioLaboratorio().getOrdenTrabajo().getLente() == null
                || ojo.getMedida() == null || ojo.getMedida().getIdMedida() == null) {
            throw new IllegalArgumentException("No se puede comprobar la pertenencia de la medida al lente");
        }
        var lente = control.getEnvioLaboratorio().getOrdenTrabajo().getLente();
        boolean pertenece = lente.getMedidas() != null && lente.getMedidas().stream()
                .anyMatch(linea -> linea.getMedida() != null
                        && ojo.getMedida().getIdMedida().equals(linea.getMedida().getIdMedida())
                        && lente.getReceta() != null && linea.getReceta() != null
                        && lente.getReceta().getIdReceta() != null
                        && lente.getReceta().getIdReceta().equals(linea.getReceta().getIdReceta())
                        && ojo.getMedida().getExamen() != null && linea.getExamen() != null
                        && lente.getReceta().getExamen() != null
                        && ojo.getMedida().getExamen().getIdExamen() != null
                        && ojo.getMedida().getExamen().getIdExamen()
                                .equals(lente.getReceta().getExamen().getIdExamen())
                        && ojo.getMedida().getExamen().getIdExamen().equals(linea.getExamen().getIdExamen()));
        if (!pertenece) {
            throw new IllegalArgumentException("La medida de ojo no pertenece al lente de la orden");
        }
    }

    @PrePersist
    @PreUpdate
    public void recalcularAprobacion() {
        if (valorEsperado == null || valorObtenido == null || tolerancia == null) {
            throw new IllegalArgumentException("El criterio debe tener valor esperado y tolerancia configurados");
        }
        if (tolerancia.signum() < 0 || (periodo != null && periodo.signum() <= 0)) {
            throw new IllegalArgumentException("Tolerancia no negativa y periodo positivo requeridos");
        }
        if (periodo != null && tolerancia.compareTo(periodo.divide(BigDecimal.valueOf(2))) > 0) {
            throw new IllegalArgumentException("La tolerancia no puede superar la mitad del periodo");
        }
        BigDecimal diferencia = valorObtenido.subtract(valorEsperado).abs();
        if (periodo != null) {
            BigDecimal resto = diferencia.remainder(periodo);
            diferencia = resto.min(periodo.subtract(resto));
        }
        aprobado = diferencia.compareTo(tolerancia) <= 0;
    }
}

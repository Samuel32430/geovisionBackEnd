package pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import org.hibernate.type.NumericBooleanConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.Lente;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Empleado;
import pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento.EstadoOrdenTrabajo;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ORDEN_TRABAJO")
public class OrdenTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden_trabajo")
    private Long idOrdenTrabajo;

    @Column(name = "numero_orden", nullable = false, unique = true, length = 50)
    private String numeroOrden;

    @Column(name = "fecha_registro", nullable = false, updatable = false, columnDefinition = "DATE")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(name = "fecha_entrega_estimada", columnDefinition = "DATE")
    private LocalDateTime fechaEntregaEstimada;

    @Column(name = "fecha_entrega_paciente", columnDefinition = "DATE")
    private LocalDateTime fechaEntregaPaciente;

    @Column(name = "fecha_notificacion", columnDefinition = "DATE")
    private LocalDateTime fechaNotificacion;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "es_urgente", nullable = false)
    private Boolean esUrgente = false;

    @Column(name = "descripcion", length = 2000)
    private String descripcion;

    @Column(name = "fecha_entrega_real", columnDefinition = "DATE")
    private LocalDateTime fechaEntregaReal;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_lente", nullable = false, unique = true)
    private Lente lente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_estado_orden_trabajo", nullable = false)
    private EstadoOrdenTrabajo estadoOrdenTrabajo;

    @JsonIgnore
    public boolean isFechasValidas() {
        if (fechaRegistro == null) {
            return true;
        }
        return noEsAnterior(fechaEntregaEstimada) && noEsAnterior(fechaEntregaPaciente)
                && noEsAnterior(fechaNotificacion) && noEsAnterior(fechaEntregaReal);
    }

    private boolean noEsAnterior(LocalDateTime fecha) {
        return fecha == null || !fecha.isBefore(fechaRegistro);
    }

    @PrePersist
    void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        validarFechas();
    }

    @PreUpdate
    void validarFechas() {
        if (!isFechasValidas()) {
            throw new IllegalArgumentException("Las fechas de la orden no pueden ser anteriores al registro");
        }
    }
}

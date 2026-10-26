package pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import org.hibernate.type.NumericBooleanConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Empleado;
import pe.edu.upeu.geovisionBackEnd.enums.elaboracionOrdenTrabajo.EstadoControlCalidad;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "CONTROL_CALIDAD")
public class ControlCalidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_control_calidad")
    private Long idControlCalidad;

    @Column(name = "fecha_registro", nullable = false, updatable = false, columnDefinition = "DATE")
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 15)
    private EstadoControlCalidad estado = EstadoControlCalidad.EN_REVISION;

    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "aprobado", nullable = false)
    private Boolean aprobado = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_envio_laboratorio", nullable = false)
    private EnvioLaboratorio envioLaboratorio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    public boolean isAprobacionCoherente() {
        return estado == null || aprobado == null || aprobado.equals(estado == EstadoControlCalidad.APROBADO);
    }

    @PrePersist
    void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        sincronizarAprobacion();
    }

    @PreUpdate
    void sincronizarAprobacion() {
        if (estado == null) {
            throw new IllegalArgumentException("El estado del control es obligatorio");
        }
        aprobado = estado == EstadoControlCalidad.APROBADO;
    }
}

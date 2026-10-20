package pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

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

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_entrega_estimada")
    private LocalDateTime fechaEntregaEstimada;

    @Column(name = "fecha_entrega_paciente")
    private LocalDateTime fechaEntregaPaciente;

    @Column(name = "fecha_notificacion")
    private LocalDateTime fechaNotificacion;

    @Column(name = "es_urgente", nullable = false)
    private Boolean esUrgente = false;

    @Column(name = "descripcion", length = 2000)
    private String descripcion;

    @Column(name = "fecha_entrega_real")
    private LocalDateTime fechaEntregaReal;

    @Column(name = "id_lente", nullable = false, unique = true)
    private Long idLente;

    @Column(name = "id_empleado", nullable = false)
    private Long idEmpleado;

    @Column(name = "id_estado_orden_trabajo", nullable = false)
    private Long idEstadoOrdenTrabajo;

    @PrePersist
    void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }
}

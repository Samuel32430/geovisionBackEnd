package pe.edu.upeu.geovision_backend.entity.orden_trabajo;

import pe.edu.upeu.geovision_backend.entity.atencion.Lente;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.geovision_backend.enums.orden_trabajo.EstadoOrdenTrabajoEnum;

import java.time.LocalDateTime;

@Entity
@Table(name = "ORDEN_TRABAJO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenTrabajo {

    // >>>> FIELDS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden_trabajo")
    private Long id;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(name = "fecha_notificacion")
    private LocalDateTime fechaNotificacion;

    @Convert(converter = EstadoOrdenTrabajoEnum.PersistenceConverter.class)
    @Column(name = "estado", nullable = false)
    private EstadoOrdenTrabajoEnum estado;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_lente", nullable = false)
    private Lente lente;

    // >>>> LIFECYCLE
    @PrePersist
    public void prePersist() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
    }

    // >>>> OPERATIONS
    public Integer getEstadoCodigo() {
        return this.estado != null ? this.estado.getCodigo() : null;
    }

    public void setEstadoCodigo(Integer codigo) {
        this.estado = EstadoOrdenTrabajoEnum.fromCodigo(codigo);
    }

}

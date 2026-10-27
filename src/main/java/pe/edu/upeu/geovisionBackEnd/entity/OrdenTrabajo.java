package pe.edu.upeu.geovisionBackEnd.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoOrdenTrabajoEnum;

import java.time.LocalDateTime;

@Entity
@Table(name = "ORDEN_TRABAJO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden_trabajo")
    private Long idOrdenTrabajo;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(name = "fecha_notificacion")
    private LocalDateTime fechaNotificacion;

    @Convert(converter = EstadoOrdenTrabajoConverter.class)
    @Column(name = "estado", nullable = false)
    private EstadoOrdenTrabajoEnum estado;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_lente", nullable = false)
    private Lente lente;

    @PrePersist
    public void prePersist() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
    }

    public Integer getEstadoCodigo() {
        return this.estado != null ? this.estado.getCodigo() : null;
    }

    public void setEstadoCodigo(Integer codigo) {
        this.estado = EstadoOrdenTrabajoEnum.fromCodigo(codigo);
    }

    @Converter
    public static class EstadoOrdenTrabajoConverter implements AttributeConverter<EstadoOrdenTrabajoEnum, Integer> {
        @Override
        public Integer convertToDatabaseColumn(EstadoOrdenTrabajoEnum attribute) {
            return attribute != null ? attribute.getCodigo() : null;
        }

        @Override
        public EstadoOrdenTrabajoEnum convertToEntityAttribute(Integer dbData) {
            return dbData != null ? EstadoOrdenTrabajoEnum.fromCodigo(dbData) : null;
        }
    }
}

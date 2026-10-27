package pe.edu.upeu.geovisionBackEnd.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;

import java.time.LocalDate;

@Entity
@Table(name = "EMPLEADO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empleado {

    @Id
    @Column(name = "id_persona")
    private Long idPersona;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @Convert(converter = EstadoGeneralConverter.class)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoGeneralEnum estado = EstadoGeneralEnum.ACTIVO;

    @Column(name = "fecha_contrato", nullable = false)
    private LocalDate fechaContrato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @PrePersist
    public void prePersist() {
        if (this.fechaContrato == null) {
            this.fechaContrato = LocalDate.now();
        }
        if (this.estado == null) {
            this.estado = EstadoGeneralEnum.ACTIVO;
        }
    }

    public Integer getEstadoCodigo() {
        return this.estado != null ? this.estado.getCodigo() : null;
    }

    public void setEstadoCodigo(Integer codigo) {
        this.estado = EstadoGeneralEnum.fromCodigo(codigo);
    }

    @Converter
    public static class EstadoGeneralConverter implements AttributeConverter<EstadoGeneralEnum, Integer> {
        @Override
        public Integer convertToDatabaseColumn(EstadoGeneralEnum attribute) {
            return attribute != null ? attribute.getCodigo() : null;
        }

        @Override
        public EstadoGeneralEnum convertToEntityAttribute(Integer dbData) {
            return dbData != null ? EstadoGeneralEnum.fromCodigo(dbData) : null;
        }
    }
}

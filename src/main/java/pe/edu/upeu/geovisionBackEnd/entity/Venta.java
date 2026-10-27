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
import pe.edu.upeu.geovisionBackEnd.enums.EstadoVentaEnum;
import pe.edu.upeu.geovisionBackEnd.enums.TipoComprobanteEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "VENTA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Long idVenta;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Convert(converter = EstadoVentaConverter.class)
    @Column(name = "estado", nullable = false)
    private EstadoVentaEnum estado;

    @Column(name = "numero_comprobante", length = 100)
    private String numeroComprobante;

    @Convert(converter = TipoComprobanteConverter.class)
    @Column(name = "tipo_comprobante", nullable = false)
    private TipoComprobanteEnum tipoComprobante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_receta")
    private Receta receta;

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
        this.estado = EstadoVentaEnum.fromCodigo(codigo);
    }

    public Integer getTipoComprobanteCodigo() {
        return this.tipoComprobante != null ? this.tipoComprobante.getCodigo() : null;
    }

    public void setTipoComprobanteCodigo(Integer codigo) {
        this.tipoComprobante = TipoComprobanteEnum.fromCodigo(codigo);
    }

    @Converter
    public static class EstadoVentaConverter implements AttributeConverter<EstadoVentaEnum, Integer> {
        @Override
        public Integer convertToDatabaseColumn(EstadoVentaEnum attribute) {
            return attribute != null ? attribute.getCodigo() : null;
        }

        @Override
        public EstadoVentaEnum convertToEntityAttribute(Integer dbData) {
            return dbData != null ? EstadoVentaEnum.fromCodigo(dbData) : null;
        }
    }

    @Converter
    public static class TipoComprobanteConverter implements AttributeConverter<TipoComprobanteEnum, Integer> {
        @Override
        public Integer convertToDatabaseColumn(TipoComprobanteEnum attribute) {
            return attribute != null ? attribute.getCodigo() : null;
        }

        @Override
        public TipoComprobanteEnum convertToEntityAttribute(Integer dbData) {
            return dbData != null ? TipoComprobanteEnum.fromCodigo(dbData) : null;
        }
    }
}

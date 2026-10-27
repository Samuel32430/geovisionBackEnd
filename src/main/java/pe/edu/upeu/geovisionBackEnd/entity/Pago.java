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
import pe.edu.upeu.geovisionBackEnd.enums.MetodoPagoEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAGO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idPago;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "numero_operacion", length = 100)
    private String numeroOperacion;

    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Convert(converter = MetodoPagoConverter.class)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPagoEnum metodoPago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @PrePersist
    public void prePersist() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
    }

    public Integer getMetodoPagoCodigo() {
        return this.metodoPago != null ? this.metodoPago.getCodigo() : null;
    }

    public void setMetodoPagoCodigo(Integer codigo) {
        this.metodoPago = MetodoPagoEnum.fromCodigo(codigo);
    }

    @Converter
    public static class MetodoPagoConverter implements AttributeConverter<MetodoPagoEnum, Integer> {
        @Override
        public Integer convertToDatabaseColumn(MetodoPagoEnum attribute) {
            return attribute != null ? attribute.getCodigo() : null;
        }

        @Override
        public MetodoPagoEnum convertToEntityAttribute(Integer dbData) {
            return dbData != null ? MetodoPagoEnum.fromCodigo(dbData) : null;
        }
    }
}

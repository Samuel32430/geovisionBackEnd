package pe.edu.upeu.geovision_backend.enums.ventas;

import jakarta.persistence.Converter;

import jakarta.persistence.AttributeConverter;
import lombok.Getter;

@Getter
public enum MetodoPagoEnum {
    EFECTIVO(1, "Efectivo"),
    TARJETA_DEBITO(2, "Tarjeta de Débito"),
    TARJETA_CREDITO(3, "Tarjeta de Crédito"),
    TRANSFERENCIA(4, "Transferencia Bancaria"),
    QR_YAPE_PLIN(5, "Billetera Digital / QR (Yape, Plin)");

    private final Integer codigo;
    private final String descripcion;

    // >>>> CONSTRUCTOR
    MetodoPagoEnum(Integer codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public static MetodoPagoEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (MetodoPagoEnum m : values()) {
            if (m.codigo.equals(codigo)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Código de método de pago no válido: " + codigo);
    }

    // >>>> CONVERTER
    @Converter
    public static class PersistenceConverter implements AttributeConverter<MetodoPagoEnum, Integer> {
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

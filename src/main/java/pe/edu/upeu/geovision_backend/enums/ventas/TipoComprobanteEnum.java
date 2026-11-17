package pe.edu.upeu.geovision_backend.enums.ventas;

import jakarta.persistence.Converter;

import jakarta.persistence.AttributeConverter;
import lombok.Getter;

@Getter
public enum TipoComprobanteEnum {
    BOLETA(1, "Boleta de Venta"),
    FACTURA(2, "Factura"),
    NOTA_VENTA(3, "Nota de Venta");

    private final Integer codigo;
    private final String descripcion;

    // >>>> CONSTRUCTOR
    TipoComprobanteEnum(Integer codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public static TipoComprobanteEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (TipoComprobanteEnum t : values()) {
            if (t.codigo.equals(codigo)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Código de tipo de comprobante no válido: " + codigo);
    }

    // >>>> CONVERTER
    @Converter
    public static class PersistenceConverter implements AttributeConverter<TipoComprobanteEnum, Integer> {
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

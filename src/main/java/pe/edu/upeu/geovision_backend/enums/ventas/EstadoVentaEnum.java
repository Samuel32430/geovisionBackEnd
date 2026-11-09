package pe.edu.upeu.geovision_backend.enums.ventas;

import jakarta.persistence.Converter;

import jakarta.persistence.AttributeConverter;
import lombok.Getter;

@Getter
public enum EstadoVentaEnum {
    PENDIENTE(1, "Pendiente de Saldo / Pago"),
    PAGADA(2, "Pagada Totalmente"),
    ANULADA(3, "Anulada");

    private final Integer codigo;
    private final String descripcion;

    // >>>> CONSTRUCTOR
    EstadoVentaEnum(Integer codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public static EstadoVentaEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (EstadoVentaEnum e : values()) {
            if (e.codigo.equals(codigo)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Código de estado de venta no válido: " + codigo);
    }

    // >>>> CONVERTER
    @Converter
    public static class PersistenceConverter implements AttributeConverter<EstadoVentaEnum, Integer> {
        @Override
        public Integer convertToDatabaseColumn(EstadoVentaEnum attribute) {
            return attribute != null ? attribute.getCodigo() : null;
        }

        @Override
        public EstadoVentaEnum convertToEntityAttribute(Integer dbData) {
            return dbData != null ? EstadoVentaEnum.fromCodigo(dbData) : null;
        }
    }
}

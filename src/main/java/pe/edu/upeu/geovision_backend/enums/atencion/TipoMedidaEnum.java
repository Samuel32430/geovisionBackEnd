package pe.edu.upeu.geovision_backend.enums.atencion;

import jakarta.persistence.Converter;

import jakarta.persistence.AttributeConverter;
import lombok.Getter;

@Getter
public enum TipoMedidaEnum {
    LEJOS(1, "Lejos"),
    MEDIO(2, "Medio"),
    CERCA(3, "Cerca");

    private final Integer codigo;
    private final String descripcion;

    // >>>> CONSTRUCTOR
    TipoMedidaEnum(Integer codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public static TipoMedidaEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (TipoMedidaEnum tipo : values()) {
            if (tipo.codigo.equals(codigo)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Codigo de tipo de medida no valido: " + codigo);
    }

    // >>>> CONVERTER
    @Converter
    public static class PersistenceConverter implements AttributeConverter<TipoMedidaEnum, Integer> {
        @Override
        public Integer convertToDatabaseColumn(TipoMedidaEnum attribute) {
            return attribute != null ? attribute.getCodigo() : null;
        }

        @Override
        public TipoMedidaEnum convertToEntityAttribute(Integer dbData) {
            return dbData != null ? TipoMedidaEnum.fromCodigo(dbData) : null;
        }
    }
}

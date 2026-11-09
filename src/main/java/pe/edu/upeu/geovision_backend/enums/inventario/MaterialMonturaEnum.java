package pe.edu.upeu.geovision_backend.enums.inventario;

import jakarta.persistence.Converter;

import jakarta.persistence.AttributeConverter;
import lombok.Getter;

@Getter
public enum MaterialMonturaEnum {
    ACETATO(1, "Acetato"),
    METAL(2, "Metal"),
    TITANIO(3, "Titanio"),
    TR90(4, "TR90"),
    MIXTO(5, "Mixto");

    private final Integer codigo;
    private final String descripcion;

    // >>>> CONSTRUCTOR
    MaterialMonturaEnum(Integer codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public static MaterialMonturaEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (MaterialMonturaEnum m : values()) {
            if (m.codigo.equals(codigo)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Código de material de montura no válido: " + codigo);
    }

    // >>>> CONVERTER
    @Converter
    public static class PersistenceConverter implements AttributeConverter<MaterialMonturaEnum, Integer> {
        @Override
        public Integer convertToDatabaseColumn(MaterialMonturaEnum attribute) {
            return attribute != null ? attribute.getCodigo() : null;
        }

        @Override
        public MaterialMonturaEnum convertToEntityAttribute(Integer dbData) {
            return dbData != null ? MaterialMonturaEnum.fromCodigo(dbData) : null;
        }
    }
}

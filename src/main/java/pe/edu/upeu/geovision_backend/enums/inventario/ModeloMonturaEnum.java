package pe.edu.upeu.geovision_backend.enums.inventario;

import jakarta.persistence.Converter;

import jakarta.persistence.AttributeConverter;
import lombok.Getter;

@Getter
public enum ModeloMonturaEnum {
    ARO_COMPLETO(1, "Aro Completo"),
    SEMI_AL_AIRE(2, "Semi al Aire"),
    AL_AIRE(3, "Al Aire");

    private final Integer codigo;
    private final String descripcion;

    // >>>> CONSTRUCTOR
    ModeloMonturaEnum(Integer codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public static ModeloMonturaEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (ModeloMonturaEnum m : values()) {
            if (m.codigo.equals(codigo)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Código de modelo de montura no válido: " + codigo);
    }

    // >>>> CONVERTER
    @Converter
    public static class PersistenceConverter implements AttributeConverter<ModeloMonturaEnum, Integer> {
        @Override
        public Integer convertToDatabaseColumn(ModeloMonturaEnum attribute) {
            return attribute != null ? attribute.getCodigo() : null;
        }

        @Override
        public ModeloMonturaEnum convertToEntityAttribute(Integer dbData) {
            return dbData != null ? ModeloMonturaEnum.fromCodigo(dbData) : null;
        }
    }
}

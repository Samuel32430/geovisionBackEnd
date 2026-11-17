package pe.edu.upeu.geovision_backend.enums.atencion;

import jakarta.persistence.Converter;

import jakarta.persistence.AttributeConverter;
import lombok.Getter;

@Getter
public enum SexoEnum {
    MASCULINO(1, "Masculino"),
    FEMENINO(2, "Femenino");

    private final Integer codigo;
    private final String descripcion;

    // >>>> CONSTRUCTOR
    SexoEnum(Integer codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public static SexoEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (SexoEnum sexo : values()) {
            if (sexo.codigo.equals(codigo)) {
                return sexo;
            }
        }
        throw new IllegalArgumentException("Código de sexo no válido: " + codigo);
    }

    // >>>> CONVERTER
    @Converter
    public static class PersistenceConverter implements AttributeConverter<SexoEnum, Integer> {
        @Override
        public Integer convertToDatabaseColumn(SexoEnum attribute) {
            return attribute != null ? attribute.getCodigo() : null;
        }

        @Override
        public SexoEnum convertToEntityAttribute(Integer dbData) {
            return dbData != null ? SexoEnum.fromCodigo(dbData) : null;
        }
    }
}

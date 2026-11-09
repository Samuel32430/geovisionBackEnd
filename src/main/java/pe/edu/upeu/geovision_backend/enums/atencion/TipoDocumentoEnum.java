package pe.edu.upeu.geovision_backend.enums.atencion;

import jakarta.persistence.Converter;

import jakarta.persistence.AttributeConverter;
import lombok.Getter;

@Getter
public enum TipoDocumentoEnum {
    DNI(1, "Documento Nacional de Identidad"),
    RUC(2, "Registro Único de Contribuyentes"),
    CARNET_EXTRANJERIA(3, "Carnet de Extranjería"),
    PASAPORTE(4, "Pasaporte");

    private final Integer codigo;
    private final String descripcion;

    // >>>> CONSTRUCTOR
    TipoDocumentoEnum(Integer codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public static TipoDocumentoEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (TipoDocumentoEnum doc : values()) {
            if (doc.codigo.equals(codigo)) {
                return doc;
            }
        }
        throw new IllegalArgumentException("Código de tipo de documento no válido: " + codigo);
    }

    // >>>> CONVERTER
    @Converter
    public static class PersistenceConverter implements AttributeConverter<TipoDocumentoEnum, Integer> {
        @Override
        public Integer convertToDatabaseColumn(TipoDocumentoEnum attribute) {
            return attribute != null ? attribute.getCodigo() : null;
        }

        @Override
        public TipoDocumentoEnum convertToEntityAttribute(Integer dbData) {
            return dbData != null ? TipoDocumentoEnum.fromCodigo(dbData) : null;
        }
    }
}

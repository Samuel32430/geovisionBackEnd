package pe.edu.upeu.geovision_backend.enums;

import jakarta.persistence.Converter;

import jakarta.persistence.AttributeConverter;
import lombok.Getter;

@Getter
public enum TipoLogEnum {
    LOGIN(1, "Inicio de Sesión"),
    LOGOUT(2, "Cierre de Sesión"),
    CAMBIO_PASSWORD(3, "Cambio de Contraseña"),
    INTENTO_FALLIDO(4, "Intento Fallido de Inicio de Sesión");

    private final Integer codigo;
    private final String descripcion;

    // >>>> CONSTRUCTOR
    TipoLogEnum(Integer codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public static TipoLogEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (TipoLogEnum log : values()) {
            if (log.codigo.equals(codigo)) {
                return log;
            }
        }
        throw new IllegalArgumentException("Código de tipo de log no válido: " + codigo);
    }

    // >>>> CONVERTER
    @Converter
    public static class PersistenceConverter implements AttributeConverter<TipoLogEnum, Integer> {
        @Override
        public Integer convertToDatabaseColumn(TipoLogEnum attribute) {
            return attribute != null ? attribute.getCodigo() : null;
        }

        @Override
        public TipoLogEnum convertToEntityAttribute(Integer dbData) {
            return dbData != null ? TipoLogEnum.fromCodigo(dbData) : null;
        }
    }
}

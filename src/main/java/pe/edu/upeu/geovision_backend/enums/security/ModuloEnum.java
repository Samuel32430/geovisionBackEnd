package pe.edu.upeu.geovision_backend.enums.security;

import jakarta.persistence.Converter;

import jakarta.persistence.AttributeConverter;
import lombok.Getter;

@Getter
public enum ModuloEnum {
    SEGURIDAD(1, "Seguridad"),
    ATENCION_PACIENTES(2, "Atención a Pacientes"),
    VENTAS(3, "Ventas"),
    ORDENES_TRABAJO(4, "Órdenes de Trabajo"),
    INVENTARIO(5, "Inventario"),
    GESTION_MANTENIMIENTO(6, "Gestión y Mantenimiento"),
    REPORTES(7, "Reportes");

    private final Integer codigo;
    private final String descripcion;

    // >>>> CONSTRUCTOR
    ModuloEnum(Integer codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public static ModuloEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (ModuloEnum m : values()) {
            if (m.codigo.equals(codigo)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Código de módulo no válido: " + codigo);
    }

    // >>>> CONVERTER
    @Converter
    public static class PersistenceConverter implements AttributeConverter<ModuloEnum, Integer> {
        @Override
        public Integer convertToDatabaseColumn(ModuloEnum attribute) {
            return attribute != null ? attribute.getCodigo() : null;
        }

        @Override
        public ModuloEnum convertToEntityAttribute(Integer dbData) {
            return dbData != null ? ModuloEnum.fromCodigo(dbData) : null;
        }
    }
}

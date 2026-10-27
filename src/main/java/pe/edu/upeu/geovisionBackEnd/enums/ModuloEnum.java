package pe.edu.upeu.geovisionBackEnd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
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

    public static ModuloEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (ModuloEnum m : values()) {
            if (m.codigo.equals(codigo)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Código de módulo no válido: " + codigo);
    }
}

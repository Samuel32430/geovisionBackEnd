package pe.edu.upeu.geovisionBackEnd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EstadoGeneralEnum {
    INACTIVO(0, "Inactivo"),
    ACTIVO(1, "Activo");

    private final Integer codigo;
    private final String descripcion;

    public static EstadoGeneralEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (EstadoGeneralEnum e : values()) {
            if (e.codigo.equals(codigo)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Código de estado general no válido: " + codigo);
    }
}

package pe.edu.upeu.geovisionBackEnd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoLogEnum {
    LOGIN(1, "Inicio de Sesión"),
    LOGOUT(2, "Cierre de Sesión"),
    CAMBIO_PASSWORD(3, "Cambio de Contraseña"),
    INTENTO_FALLIDO(4, "Intento Fallido de Inicio de Sesión");

    private final Integer codigo;
    private final String descripcion;

    public static TipoLogEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (TipoLogEnum log : values()) {
            if (log.codigo.equals(codigo)) {
                return log;
            }
        }
        throw new IllegalArgumentException("Código de tipo de log no válido: " + codigo);
    }
}

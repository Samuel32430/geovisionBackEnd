package pe.edu.upeu.geovisionBackEnd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModeloMonturaEnum {
    ARO_COMPLETO(1, "Aro Completo"),
    SEMI_AL_AIRE(2, "Semi al Aire"),
    AL_AIRE(3, "Al Aire");

    private final Integer codigo;
    private final String descripcion;

    public static ModeloMonturaEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (ModeloMonturaEnum m : values()) {
            if (m.codigo.equals(codigo)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Código de modelo de montura no válido: " + codigo);
    }
}

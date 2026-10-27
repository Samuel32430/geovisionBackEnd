package pe.edu.upeu.geovisionBackEnd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MaterialMonturaEnum {
    ACETATO(1, "Acetato"),
    METAL(2, "Metal"),
    TITANIO(3, "Titanio"),
    TR90(4, "TR90"),
    MIXTO(5, "Mixto");

    private final Integer codigo;
    private final String descripcion;

    public static MaterialMonturaEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (MaterialMonturaEnum m : values()) {
            if (m.codigo.equals(codigo)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Código de material de montura no válido: " + codigo);
    }
}

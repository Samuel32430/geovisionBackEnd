package pe.edu.upeu.geovisionBackEnd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SexoEnum {
    MASCULINO(1, "Masculino"),
    FEMENINO(2, "Femenino");

    private final Integer codigo;
    private final String descripcion;

    public static SexoEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (SexoEnum sexo : values()) {
            if (sexo.codigo.equals(codigo)) {
                return sexo;
            }
        }
        throw new IllegalArgumentException("Código de sexo no válido: " + codigo);
    }
}

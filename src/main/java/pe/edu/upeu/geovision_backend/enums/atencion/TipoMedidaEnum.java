package pe.edu.upeu.geovision_backend.enums.atencion;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoMedidaEnum {
    LEJOS(1, "Lejos"),
    MEDIO(2, "Medio"),
    CERCA(3, "Cerca");

    private final Integer codigo;
    private final String descripcion;

    public static TipoMedidaEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (TipoMedidaEnum tipo : values()) {
            if (tipo.codigo.equals(codigo)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Codigo de tipo de medida no valido: " + codigo);
    }
}

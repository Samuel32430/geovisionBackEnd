package pe.edu.upeu.geovisionBackEnd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoComprobanteEnum {
    BOLETA(1, "Boleta de Venta"),
    FACTURA(2, "Factura"),
    NOTA_VENTA(3, "Nota de Venta");

    private final Integer codigo;
    private final String descripcion;

    public static TipoComprobanteEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (TipoComprobanteEnum t : values()) {
            if (t.codigo.equals(codigo)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Código de tipo de comprobante no válido: " + codigo);
    }
}

package pe.edu.upeu.geovisionBackEnd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EstadoVentaEnum {
    PENDIENTE(1, "Pendiente de Saldo / Pago"),
    PAGADA(2, "Pagada Totalmente"),
    ANULADA(3, "Anulada");

    private final Integer codigo;
    private final String descripcion;

    public static EstadoVentaEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (EstadoVentaEnum e : values()) {
            if (e.codigo.equals(codigo)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Código de estado de venta no válido: " + codigo);
    }
}

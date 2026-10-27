package pe.edu.upeu.geovisionBackEnd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MetodoPagoEnum {
    EFECTIVO(1, "Efectivo"),
    TARJETA_DEBITO(2, "Tarjeta de Débito"),
    TARJETA_CREDITO(3, "Tarjeta de Crédito"),
    TRANSFERENCIA(4, "Transferencia Bancaria"),
    QR_YAPE_PLIN(5, "Billetera Digital / QR (Yape, Plin)");

    private final Integer codigo;
    private final String descripcion;

    public static MetodoPagoEnum fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (MetodoPagoEnum m : values()) {
            if (m.codigo.equals(codigo)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Código de método de pago no válido: " + codigo);
    }
}

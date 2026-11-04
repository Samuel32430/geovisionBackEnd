package pe.edu.upeu.geovisionBackEnd.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MotivoMovimientoStockEnum {

    ENTRADA_POR_COMPRA(1, "Entrada por compra"),
    AJUSTE_POR_DIFERENCIA(2, "Ajuste por diferencia"),
    DANIO_DETERIORO(3, "Daño / Deterioro"),
    PERDIDA(4, "Pérdida"),
    SALIDA_POR_VENTA(5, "Salida por venta");

    private final Integer codigo;
    private final String descripcion;

    public static MotivoMovimientoStockEnum fromCodigo(Integer codigo) {
        if (codigo == null) {
            return null;
        }
        for (MotivoMovimientoStockEnum motivo : values()) {
            if (motivo.codigo.equals(codigo)) {
                return motivo;
            }
        }
        throw new IllegalArgumentException("Código de motivo de movimiento no válido: " + codigo);
    }
}

package pe.edu.upeu.geovisionBackEnd.enums.controlInventario;

/**
 * Causas o motivos de variación física de existencias en MOVIMIENTO_INVENTARIO.
 * Restricción CHECK: motivo IN ('INICIAL', 'COMPRA', 'VENTA', 'DEVOLUCION', 'ROTURA', 'AJUSTE')
 */
public enum MotivoMovimiento {
    INICIAL,
    COMPRA,
    VENTA,
    DEVOLUCION,
    ROTURA,
    AJUSTE
}

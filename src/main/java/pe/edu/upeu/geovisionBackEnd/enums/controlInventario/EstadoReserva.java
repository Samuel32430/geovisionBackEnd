package pe.edu.upeu.geovisionBackEnd.enums.controlInventario;

/**
 * Estados del ciclo de vida de una reserva de inventario en RESERVA_INVENTARIO.
 * Restricción CHECK: estado IN ('ACTIVA', 'LIBERADA', 'CONSUMIDA')
 */
public enum EstadoReserva {
    ACTIVA,
    LIBERADA,
    CONSUMIDA
}

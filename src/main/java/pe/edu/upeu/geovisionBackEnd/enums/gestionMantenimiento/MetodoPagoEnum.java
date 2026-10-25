package pe.edu.upeu.geovisionBackEnd.enums.gestionMantenimiento;

public enum MetodoPagoEnum {
    EFECTIVO,
    TARJETA,
    YAPE,
    PLIN,
    TRANSFERENCIA;

    public boolean requiereVoucher() {
        return this == TARJETA;
    }
}

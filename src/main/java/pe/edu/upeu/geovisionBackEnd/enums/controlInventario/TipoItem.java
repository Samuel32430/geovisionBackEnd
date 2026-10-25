package pe.edu.upeu.geovisionBackEnd.enums.controlInventario;

/**
 * Clasificación de los ítems en catálogo según el DDL oficial.
 * Restricción CHECK: tipo IN ('PRODUCTO', 'MONTURA', 'CRISTAL')
 */
public enum TipoItem {
    PRODUCTO,
    MONTURA,
    CRISTAL
}

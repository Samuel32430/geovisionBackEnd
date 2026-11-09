-- ============================================================================
-- PROYECTO GEOVISION - MODULO DE VENTAS Y REPORTES
-- Objeto: FUNCION 1 / 2
-- Nombre: FN_CALCULAR_SALDO_VENTA
-- Finalidad: Retornar de manera deterministica el saldo deudor neto de una venta,
--            deduciendo los pagos validos acumulados en la tabla PAGO.
-- ============================================================================

CREATE OR REPLACE FUNCTION FN_CALCULAR_SALDO_VENTA(p_id_venta IN NUMBER)
RETURN NUMBER
IS
    v_total NUMBER(12,2) := 0;
    v_pagado NUMBER(12,2) := 0;
    v_estado NUMBER := 0;
    v_saldo NUMBER(12,2) := 0;
BEGIN
    IF p_id_venta IS NULL THEN
        RETURN 0;
    END IF;

    SELECT total, estado
    INTO v_total, v_estado
    FROM VENTA
    WHERE id_venta = p_id_venta;

    -- Si la venta esta anulada (estado 3), no existe saldo exigible
    IF v_estado = 3 THEN
        RETURN 0;
    END IF;

    SELECT COALESCE(SUM(monto), 0)
    INTO v_pagado
    FROM PAGO
    WHERE id_venta = p_id_venta;

    v_saldo := v_total - v_pagado;
    IF v_saldo < 0 THEN
        v_saldo := 0;
    END IF;

    RETURN v_saldo;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 0;
    WHEN OTHERS THEN
        RETURN 0;
END FN_CALCULAR_SALDO_VENTA;
/

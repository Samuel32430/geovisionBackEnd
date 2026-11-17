-- ============================================================================
-- PROYECTO GEOVISION - MODULO DE VENTAS Y REPORTES
-- Objeto: PROCEDIMIENTO ALMACENADO 3 / 3
-- Nombre: SP_REGISTRAR_PAGO_PENDIENTE
-- Finalidad: Registrar abonos y liquidacion de saldo pendiente (CUS07),
--            garantizando bloqueo pesimista y actualizacion de estado a PAGADA.
-- ============================================================================

CREATE OR REPLACE PROCEDURE SP_REGISTRAR_PAGO_PENDIENTE (
    p_id_venta           IN  NUMBER,
    p_monto              IN  NUMBER,
    p_metodo_pago        IN  NUMBER,
    p_numero_operacion   IN  VARCHAR2,
    p_id_pago            OUT NUMBER,
    p_saldo_restante     OUT NUMBER,
    p_nuevo_estado       OUT NUMBER
)
IS
    v_total NUMBER(12,2);
    v_estado_actual NUMBER;
    v_saldo_actual NUMBER(12,2);
    v_nuevo_saldo NUMBER(12,2);
BEGIN
    IF p_id_venta IS NULL THEN
        RAISE_APPLICATION_ERROR(-20020, 'El ID de la venta es obligatorio');
    END IF;

    IF p_monto IS NULL OR p_monto <= 0 THEN
        RAISE_APPLICATION_ERROR(-20021, 'El monto del abono debe ser mayor a cero');
    END IF;

    -- Bloqueo pesimista para evitar concurrencia y sobrepagos
    SELECT total, estado
    INTO v_total, v_estado_actual
    FROM VENTA
    WHERE id_venta = p_id_venta
    FOR UPDATE;

    IF v_estado_actual = 3 THEN
        RAISE_APPLICATION_ERROR(-20022, 'No se pueden registrar pagos en una venta anulada');
    END IF;

    IF v_estado_actual = 2 THEN
        RAISE_APPLICATION_ERROR(-20023, 'La venta ya se encuentra pagada en su totalidad');
    END IF;

    -- Calcular saldo actual usando la funcion
    v_saldo_actual := FN_CALCULAR_SALDO_VENTA(p_id_venta);

    IF p_monto > v_saldo_actual THEN
        RAISE_APPLICATION_ERROR(-20024, 'El monto a abonar excede el saldo pendiente restante (Saldo actual: S/ ' || v_saldo_actual || ')');
    END IF;

    -- Insertar nuevo pago
    INSERT INTO PAGO (
        fecha_registro, numero_operacion, monto, metodo_pago, id_venta
    ) VALUES (
        SYSDATE, p_numero_operacion, p_monto, p_metodo_pago, p_id_venta
    ) RETURNING id_pago INTO p_id_pago;

    v_nuevo_saldo := v_saldo_actual - p_monto;
    p_saldo_restante := v_nuevo_saldo;

    -- Si el saldo restante llega a 0, actualizar estado de la venta a PAGADA (2)
    IF v_nuevo_saldo = 0 THEN
        UPDATE VENTA 
        SET estado = 2 -- PAGADA
        WHERE id_venta = p_id_venta;
        p_nuevo_estado := 2;
    ELSE
        p_nuevo_estado := v_estado_actual;
    END IF;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END SP_REGISTRAR_PAGO_PENDIENTE;
/

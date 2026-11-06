-- ============================================================================
-- PROYECTO GEOVISION - MODULO DE VENTAS Y REPORTES
-- Objeto: TRIGGER 2 / 2
-- Nombre: TRG_DESCONTAR_STOCK_POR_VENTA
-- Tabla Disparadora: PRODUCTO_VENDIDO (AFTER INSERT)
-- Finalidad: Descontar automaticamente el stock de PRODUCTO al vender un item
--            y registrar de inmediato el movimiento en MOVIMIENTO_STOCK_LOG (motivo 5).
-- ============================================================================

CREATE OR REPLACE TRIGGER TRG_DESCONTAR_STOCK_POR_VENTA
AFTER INSERT ON PRODUCTO_VENDIDO
FOR EACH ROW
DECLARE
    v_stock_actual NUMBER;
    v_stock_nuevo NUMBER;
    v_id_usuario NUMBER;
    v_ya_registrado NUMBER;
BEGIN
    -- Verificar si la aplicacion backend ya registro la salida de inventario (evitar doble descuento)
    SELECT COUNT(*)
    INTO v_ya_registrado
    FROM MOVIMIENTO_STOCK_LOG
    WHERE id_producto = :NEW.id_producto
      AND motivo = 5
      AND (observaciones LIKE '%ID: ' || :NEW.id_venta || '%' 
           OR observaciones LIKE '%(' || :NEW.id_venta || ')%');

    IF v_ya_registrado > 0 THEN
        RETURN; -- Ya procesado atomicamente por la capa de aplicacion
    END IF;

    -- Obtener stock actual con bloqueo pesimista
    SELECT stock 
    INTO v_stock_actual
    FROM PRODUCTO
    WHERE id_producto = :NEW.id_producto
    FOR UPDATE;

    IF v_stock_actual < :NEW.cantidad THEN
        RAISE_APPLICATION_ERROR(-20030, 'Stock insuficiente para el producto ID ' || :NEW.id_producto || 
                                       '. Stock disponible: ' || v_stock_actual || ', Solicitado: ' || :NEW.cantidad);
    END IF;

    v_stock_nuevo := v_stock_actual - :NEW.cantidad;

    -- Descontar stock disponible
    UPDATE PRODUCTO
    SET stock = v_stock_nuevo
    WHERE id_producto = :NEW.id_producto;

    -- Obtener el usuario del vendedor responsable de la venta
    BEGIN
        SELECT emp.id_usuario
        INTO v_id_usuario
        FROM VENTA v
        JOIN EMPLEADO emp ON v.id_empleado = emp.id_persona
        WHERE v.id_venta = :NEW.id_venta;
    EXCEPTION
        WHEN OTHERS THEN
            v_id_usuario := 1; -- Fallback seguro de usuario administrador
    END;

    -- Registrar auditoria en MOVIMIENTO_STOCK_LOG (motivo 5: SALIDA_POR_VENTA)
    INSERT INTO MOVIMIENTO_STOCK_LOG (
        fecha_registro, cantidad, stock_previo, stock_posterior, 
        motivo, observaciones, id_producto, id_usuario
    ) VALUES (
        SYSDATE, :NEW.cantidad, v_stock_actual, v_stock_nuevo, 
        5, 'Salida automatica por venta comprobante ID: ' || :NEW.id_venta, :NEW.id_producto, v_id_usuario
    );
END TRG_DESCONTAR_STOCK_POR_VENTA;
/

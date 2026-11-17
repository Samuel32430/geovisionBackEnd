-- ============================================================================
-- PROYECTO GEOVISION - MODULO DE VENTAS Y REPORTES
-- Objeto: TRIGGER 1 / 2
-- Nombre: TRG_ACTUALIZAR_ESTADO_VENTA_PAGO
-- Tabla Disparadora: PAGO (AFTER INSERT OR UPDATE)
-- Finalidad: Compound Trigger que sincroniza automaticamente el estado de la
--            venta (a PAGADA = 2 o PENDIENTE = 1) cuando se registran abonos,
--            evitando el error de tabla mutante (ORA-04091).
-- ============================================================================

CREATE OR REPLACE TRIGGER TRG_ACTUALIZAR_ESTADO_VENTA_PAGO
FOR INSERT OR UPDATE ON PAGO
COMPOUND TRIGGER

    TYPE t_ventas IS TABLE OF NUMBER INDEX BY PLS_INTEGER;
    v_ventas t_ventas;
    v_total NUMBER(12,2);
    v_pagado NUMBER(12,2);
    v_estado_actual NUMBER;

    -- Capturar IDs de venta afectados a nivel de fila
    AFTER EACH ROW IS
    BEGIN
        IF :NEW.id_venta IS NOT NULL THEN
            v_ventas(v_ventas.COUNT + 1) := :NEW.id_venta;
        END IF;
    END AFTER EACH ROW;

    -- Procesar a nivel de sentencia despues de completar la insercion en PAGO
    AFTER STATEMENT IS
    BEGIN
        FOR i IN 1 .. v_ventas.COUNT LOOP
            BEGIN
                SELECT total, estado
                INTO v_total, v_estado_actual
                FROM VENTA
                WHERE id_venta = v_ventas(i);

                -- Si la venta esta anulada (3), no alterar su estado
                IF v_estado_actual != 3 THEN
                    SELECT COALESCE(SUM(monto), 0)
                    INTO v_pagado
                    FROM PAGO
                    WHERE id_venta = v_ventas(i);

                    -- Si la suma de pagos cubre el total, actualizar a PAGADA (2)
                    IF v_pagado >= v_total AND v_estado_actual != 2 THEN
                        UPDATE VENTA 
                        SET estado = 2 
                        WHERE id_venta = v_ventas(i);
                    -- Si la suma de pagos es menor al total, asegurar estado PENDIENTE (1)
                    ELSIF v_pagado < v_total AND v_estado_actual != 1 THEN
                        UPDATE VENTA 
                        SET estado = 1 
                        WHERE id_venta = v_ventas(i);
                    END IF;
                END IF;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    NULL;
            END;
        END LOOP;
    END AFTER STATEMENT;

END TRG_ACTUALIZAR_ESTADO_VENTA_PAGO;
/

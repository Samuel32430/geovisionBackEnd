-- ============================================================================
-- PROYECTO GEOVISION - MODULO DE VENTAS Y REPORTES
-- Objeto: PROCEDIMIENTO ALMACENADO 1 / 3
-- Nombre: SP_REGISTRAR_VENTA
-- Finalidad: Persistencia atomica transaccional de la cabecera de venta,
--            generacion de correlativo y registro del pago inicial.
-- ============================================================================

CREATE OR REPLACE PROCEDURE SP_REGISTRAR_VENTA (
    p_id_empleado         IN  NUMBER,
    p_id_paciente         IN  NUMBER,
    p_id_receta           IN  NUMBER,
    p_tipo_comprobante    IN  NUMBER,
    p_total               IN  NUMBER,
    p_monto_inicial_pago  IN  NUMBER,
    p_metodo_pago         IN  NUMBER,
    p_numero_operacion    IN  VARCHAR2,
    p_id_venta            OUT NUMBER,
    p_numero_comprobante  OUT VARCHAR2
)
IS
    v_estado_inicial NUMBER;
    v_correlativo VARCHAR2(100);
BEGIN
    IF p_id_empleado IS NULL THEN
        RAISE_APPLICATION_ERROR(-20010, 'El ID de empleado vendedor es obligatorio');
    END IF;

    IF p_total IS NULL OR p_total <= 0 THEN
        RAISE_APPLICATION_ERROR(-20011, 'El total de la venta debe ser mayor a cero');
    END IF;

    -- Generar correlativo secuencial consecutivo
    v_correlativo := FN_GENERAR_CORRELATIVO_COMPROBANTE(p_tipo_comprobante);

    -- Determinar estado inicial (1 = PENDIENTE, 2 = PAGADA)
    IF p_monto_inicial_pago >= p_total THEN
        v_estado_inicial := 2; -- PAGADA
    ELSE
        v_estado_inicial := 1; -- PENDIENTE
    END IF;

    -- Insertar cabecera de VENTA
    INSERT INTO VENTA (
        fecha_registro, total, estado, numero_comprobante, 
        tipo_comprobante, id_empleado, id_paciente, id_receta
    ) VALUES (
        SYSDATE, p_total, v_estado_inicial, v_correlativo, 
        p_tipo_comprobante, p_id_empleado, p_id_paciente, p_id_receta
    ) RETURNING id_venta INTO p_id_venta;

    p_numero_comprobante := v_correlativo;

    -- Registrar pago inicial si se proporciono
    IF p_monto_inicial_pago > 0 THEN
        INSERT INTO PAGO (
            fecha_registro, numero_operacion, monto, metodo_pago, id_venta
        ) VALUES (
            SYSDATE, p_numero_operacion, p_monto_inicial_pago, p_metodo_pago, p_id_venta
        );
    END IF;

    -- Se prescinde de COMMIT explicito para permitir transaccionalidad atomica desde la aplicacion
    -- y asegurar capacidad de ROLLBACK integral ante excepciones concurrentes.
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END SP_REGISTRAR_VENTA;
/

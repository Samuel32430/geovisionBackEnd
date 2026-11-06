-- ============================================================================
-- PROYECTO GEOVISION - SCRIPT MAESTRO DE OBJETOS ORACLE (MODULO VENTAS)
-- Esquema: geovisionBackEnd (Oracle Database 19c)
-- Ejecutar en: Oracle SQL Developer / SQL*Plus
-- ============================================================================

SET SERVEROUTPUT ON;
PROMPT >>> INICIANDO INSTALACION DE OBJETOS PL/SQL PARA EL MODULO DE VENTAS...

-- ----------------------------------------------------------------------------
-- 1. VISTAS
-- ----------------------------------------------------------------------------
PROMPT >>> [1/4] Creando Vista 1: VW_REPORTE_VENTAS_CONSOLIDADO...
CREATE OR REPLACE VIEW VW_REPORTE_VENTAS_CONSOLIDADO AS
SELECT 
    v.id_venta,
    v.fecha_registro,
    v.numero_comprobante,
    v.tipo_comprobante,
    CASE v.tipo_comprobante
        WHEN 1 THEN 'BOLETA'
        WHEN 2 THEN 'FACTURA'
        ELSE 'NOTA DE VENTA'
    END AS nombre_tipo_comprobante,
    v.estado AS codigo_estado,
    CASE v.estado
        WHEN 1 THEN 'PENDIENTE'
        WHEN 2 THEN 'PAGADA'
        WHEN 3 THEN 'ANULADA'
        ELSE 'DESCONOCIDO'
    END AS nombre_estado,
    v.total AS total_venta,
    COALESCE((SELECT SUM(p.monto) FROM PAGO p WHERE p.id_venta = v.id_venta), 0) AS total_pagado,
    GREATEST(0, v.total - COALESCE((SELECT SUM(p.monto) FROM PAGO p WHERE p.id_venta = v.id_venta), 0)) AS saldo_pendiente,
    CASE 
        WHEN v.estado = 3 THEN 'ANULADA'
        WHEN COALESCE((SELECT SUM(p.monto) FROM PAGO p WHERE p.id_venta = v.id_venta), 0) >= v.total THEN 'CANCELADO'
        WHEN COALESCE((SELECT SUM(p.monto) FROM PAGO p WHERE p.id_venta = v.id_venta), 0) > 0 THEN 'ADELANTO'
        ELSE 'SIN PAGO'
    END AS condicion_pago,
    v.id_paciente,
    per_cli.numero_documento AS doc_cliente,
    TRIM(CONCAT(CONCAT(per_cli.nombres, ' '), per_cli.apellidos)) AS nombre_cliente,
    per_cli.telefono AS telefono_cliente,
    v.id_empleado,
    per_emp.numero_documento AS doc_empleado,
    TRIM(CONCAT(CONCAT(per_emp.nombres, ' '), per_emp.apellidos)) AS nombre_empleado,
    v.id_receta,
    (SELECT COUNT(pv.id_producto_vendido) FROM PRODUCTO_VENDIDO pv WHERE pv.id_venta = v.id_venta) AS cantidad_productos,
    (SELECT COUNT(sv.id_servicio_vendido) FROM SERVICIO_VENDIDO sv WHERE sv.id_venta = v.id_venta) AS cantidad_servicios,
    (SELECT COUNT(p.id_pago) FROM PAGO p WHERE p.id_venta = v.id_venta) AS cantidad_pagos
FROM VENTA v
LEFT JOIN PACIENTE pac ON v.id_paciente = pac.id_persona
LEFT JOIN PERSONA per_cli ON pac.id_persona = per_cli.id_persona
JOIN EMPLEADO emp ON v.id_empleado = emp.id_persona
JOIN PERSONA per_emp ON emp.id_persona = per_emp.id_persona;

PROMPT >>> [1/4] Creando Vista 2: VW_RANKING_PRODUCTOS_SERVICIOS_TOP...
CREATE OR REPLACE VIEW VW_RANKING_PRODUCTOS_SERVICIOS_TOP AS
WITH ConsolidadoItems AS (
    SELECT 
        'PRODUCTO' AS tipo_item,
        p.id_producto AS id_item,
        p.codigo AS codigo_item,
        p.nombre AS nombre_item,
        COALESCE(c.nombre, 'SIN CATEGORIA') AS categoria_o_tipo,
        p.precio AS precio_actual,
        pv.cantidad AS cantidad_vendida,
        pv.sub_total AS ingreso_total,
        v.fecha_registro AS fecha_venta,
        v.estado AS estado_venta
    FROM PRODUCTO_VENDIDO pv
    JOIN PRODUCTO p ON pv.id_producto = p.id_producto
    LEFT JOIN CATEGORIA c ON p.id_categoria = c.id_categoria
    JOIN VENTA v ON pv.id_venta = v.id_venta
    WHERE v.estado != 3

    UNION ALL

    SELECT 
        'SERVICIO' AS tipo_item,
        s.id_servicio AS id_item,
        CONCAT('SERV-', TO_CHAR(s.id_servicio)) AS codigo_item,
        s.nombre AS nombre_item,
        'SERVICIO PROFESIONAL' AS categoria_o_tipo,
        s.precio AS precio_actual,
        1 AS cantidad_vendida,
        sv.precio AS ingreso_total,
        v.fecha_registro AS fecha_venta,
        v.estado AS estado_venta
    FROM SERVICIO_VENDIDO sv
    JOIN SERVICIO s ON sv.id_servicio = s.id_servicio
    JOIN VENTA v ON sv.id_venta = v.id_venta
    WHERE v.estado != 3
)
SELECT 
    tipo_item,
    id_item,
    codigo_item,
    nombre_item,
    categoria_o_tipo,
    precio_actual,
    SUM(cantidad_vendida) AS unidades_vendidas,
    SUM(ingreso_total) AS monto_total_recaudado,
    ROUND(AVG(ingreso_total / GREATEST(1, cantidad_vendida)), 2) AS precio_promedio_venta,
    COUNT(DISTINCT TRUNC(fecha_venta)) AS dias_con_ventas,
    DENSE_RANK() OVER (PARTITION BY tipo_item ORDER BY SUM(ingreso_total) DESC) AS ranking_por_ingresos,
    DENSE_RANK() OVER (PARTITION BY tipo_item ORDER BY SUM(cantidad_vendida) DESC) AS ranking_por_unidades
FROM ConsolidadoItems
GROUP BY 
    tipo_item,
    id_item,
    codigo_item,
    nombre_item,
    categoria_o_tipo,
    precio_actual;

PROMPT >>> [1/4] Creando Vista 3: VW_VENTAS_SALDOS_PENDIENTES...
CREATE OR REPLACE VIEW VW_VENTAS_SALDOS_PENDIENTES AS
SELECT 
    v.id_venta,
    v.fecha_registro AS fecha_venta,
    v.numero_comprobante,
    v.total AS total_venta,
    COALESCE(SUM(p.monto), 0) AS monto_adelanto_pagado,
    (v.total - COALESCE(SUM(p.monto), 0)) AS saldo_por_cobrar,
    ROUND((COALESCE(SUM(p.monto), 0) / NULLIF(v.total, 0)) * 100, 2) AS porcentaje_pagado,
    TRUNC(SYSDATE) - TRUNC(v.fecha_registro) AS dias_antiguedad_deuda,
    pac.id_persona AS id_paciente,
    per_cli.numero_documento AS documento_paciente,
    TRIM(CONCAT(CONCAT(per_cli.nombres, ' '), per_cli.apellidos)) AS nombre_completo_paciente,
    per_cli.telefono AS telefono_contacto,
    v.id_receta,
    TRIM(CONCAT(CONCAT(per_emp.nombres, ' '), per_emp.apellidos)) AS vendedor_responsable
FROM VENTA v
LEFT JOIN PAGO p ON v.id_venta = p.id_venta
JOIN PACIENTE pac ON v.id_paciente = pac.id_persona
JOIN PERSONA per_cli ON pac.id_persona = per_cli.id_persona
JOIN EMPLEADO emp ON v.id_empleado = emp.id_persona
JOIN PERSONA per_emp ON emp.id_persona = per_emp.id_persona
WHERE v.estado = 1
GROUP BY 
    v.id_venta,
    v.fecha_registro,
    v.numero_comprobante,
    v.total,
    pac.id_persona,
    per_cli.numero_documento,
    per_cli.nombres,
    per_cli.apellidos,
    per_cli.telefono,
    v.id_receta,
    per_emp.nombres,
    per_emp.apellidos
HAVING (v.total - COALESCE(SUM(p.monto), 0)) > 0;

-- ----------------------------------------------------------------------------
-- 2. FUNCIONES
-- ----------------------------------------------------------------------------
PROMPT >>> [2/4] Creando Funcion 1: FN_CALCULAR_SALDO_VENTA...
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

PROMPT >>> [2/4] Creando Funcion 2: FN_GENERAR_CORRELATIVO_COMPROBANTE...
CREATE OR REPLACE FUNCTION FN_GENERAR_CORRELATIVO_COMPROBANTE(p_tipo_comprobante IN NUMBER)
RETURN VARCHAR2
IS
    v_prefijo VARCHAR2(10);
    v_ultimo VARCHAR2(100);
    v_siguiente NUMBER := 1;
    v_num_str VARCHAR2(50);
BEGIN
    CASE p_tipo_comprobante
        WHEN 1 THEN v_prefijo := 'B001-';
        WHEN 2 THEN v_prefijo := 'F001-';
        ELSE v_prefijo := 'NV01-';
    END CASE;

    SELECT MAX(numero_comprobante)
    INTO v_ultimo
    FROM VENTA
    WHERE numero_comprobante LIKE v_prefijo || '%';

    IF v_ultimo IS NOT NULL AND LENGTH(v_ultimo) > LENGTH(v_prefijo) THEN
        v_num_str := SUBSTR(v_ultimo, LENGTH(v_prefijo) + 1);
        BEGIN
            v_siguiente := TO_NUMBER(v_num_str) + 1;
        EXCEPTION
            WHEN VALUE_ERROR THEN
                v_siguiente := 1;
        END;
    ELSE
        v_siguiente := 1;
    END IF;

    RETURN v_prefijo || LPAD(TO_CHAR(v_siguiente), 8, '0');
EXCEPTION
    WHEN OTHERS THEN
        RETURN v_prefijo || '00000001';
END FN_GENERAR_CORRELATIVO_COMPROBANTE;
/

-- ----------------------------------------------------------------------------
-- 3. PROCEDIMIENTOS ALMACENADOS
-- ----------------------------------------------------------------------------
PROMPT >>> [3/4] Creando Procedimiento 1: SP_REGISTRAR_VENTA...
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

    v_correlativo := FN_GENERAR_CORRELATIVO_COMPROBANTE(p_tipo_comprobante);

    IF p_monto_inicial_pago >= p_total THEN
        v_estado_inicial := 2;
    ELSE
        v_estado_inicial := 1;
    END IF;

    INSERT INTO VENTA (
        fecha_registro, total, estado, numero_comprobante, 
        tipo_comprobante, id_empleado, id_paciente, id_receta
    ) VALUES (
        SYSDATE, p_total, v_estado_inicial, v_correlativo, 
        p_tipo_comprobante, p_id_empleado, p_id_paciente, p_id_receta
    ) RETURNING id_venta INTO p_id_venta;

    p_numero_comprobante := v_correlativo;

    IF p_monto_inicial_pago > 0 THEN
        INSERT INTO PAGO (
            fecha_registro, numero_operacion, monto, metodo_pago, id_venta
        ) VALUES (
            SYSDATE, p_numero_operacion, p_monto_inicial_pago, p_metodo_pago, p_id_venta
        );
    END IF;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END SP_REGISTRAR_VENTA;
/

PROMPT >>> [3/4] Creando Procedimiento 2: SP_REPORTE_VENTAS_FILTRADO...
CREATE OR REPLACE PROCEDURE SP_REPORTE_VENTAS_FILTRADO (
    p_fecha_inicio       IN  DATE,
    p_fecha_fin          IN  DATE,
    p_id_empleado        IN  NUMBER,
    p_tipo_comprobante   IN  NUMBER,
    p_estado             IN  NUMBER,
    p_total_vendido      OUT NUMBER,
    p_total_cobrado      OUT NUMBER,
    p_total_saldo        OUT NUMBER,
    p_cantidad_ventas    OUT NUMBER,
    p_ticket_promedio    OUT NUMBER,
    p_cursor             OUT SYS_REFCURSOR
)
IS
    v_inicio DATE := NULL;
    v_fin    DATE := NULL;
BEGIN
    IF p_fecha_inicio IS NOT NULL THEN
        v_inicio := TRUNC(p_fecha_inicio);
    END IF;

    IF p_fecha_fin IS NOT NULL THEN
        v_fin := TRUNC(p_fecha_fin) + 1 - (1/86400);
    END IF;

    SELECT 
        COALESCE(SUM(v.total), 0),
        COALESCE(SUM((SELECT SUM(p.monto) FROM PAGO p WHERE p.id_venta = v.id_venta)), 0),
        COALESCE(SUM(GREATEST(0, v.total - COALESCE((SELECT SUM(p.monto) FROM PAGO p WHERE p.id_venta = v.id_venta), 0))), 0),
        COUNT(v.id_venta),
        ROUND(COALESCE(AVG(v.total), 0), 2)
    INTO 
        p_total_vendido,
        p_total_cobrado,
        p_total_saldo,
        p_cantidad_ventas,
        p_ticket_promedio
    FROM VENTA v
    WHERE (v_inicio IS NULL OR v.fecha_registro >= v_inicio)
      AND (v_fin IS NULL OR v.fecha_registro <= v_fin)
      AND (p_id_empleado IS NULL OR v.id_empleado = p_id_empleado)
      AND (p_tipo_comprobante IS NULL OR v.tipo_comprobante = p_tipo_comprobante)
      AND (p_estado IS NULL OR v.estado = p_estado);

    OPEN p_cursor FOR
        SELECT 
            v.id_venta,
            v.fecha_registro,
            v.numero_comprobante,
            v.tipo_comprobante,
            CASE v.tipo_comprobante
                WHEN 1 THEN 'BOLETA'
                WHEN 2 THEN 'FACTURA'
                ELSE 'NOTA DE VENTA'
            END AS nombre_tipo_comprobante,
            v.estado AS codigo_estado,
            CASE v.estado
                WHEN 1 THEN 'PENDIENTE'
                WHEN 2 THEN 'PAGADA'
                WHEN 3 THEN 'ANULADA'
                ELSE 'DESCONOCIDO'
            END AS nombre_estado,
            v.total AS total_venta,
            COALESCE((SELECT SUM(p.monto) FROM PAGO p WHERE p.id_venta = v.id_venta), 0) AS total_pagado,
            FN_CALCULAR_SALDO_VENTA(v.id_venta) AS saldo_pendiente,
            per_cli.numero_documento AS doc_cliente,
            TRIM(CONCAT(CONCAT(per_cli.nombres, ' '), per_cli.apellidos)) AS nombre_cliente,
            TRIM(CONCAT(CONCAT(per_emp.nombres, ' '), per_emp.apellidos)) AS nombre_empleado
        FROM VENTA v
        LEFT JOIN PACIENTE pac ON v.id_paciente = pac.id_persona
        LEFT JOIN PERSONA per_cli ON pac.id_persona = per_cli.id_persona
        JOIN EMPLEADO emp ON v.id_empleado = emp.id_persona
        JOIN PERSONA per_emp ON emp.id_persona = per_emp.id_persona
        WHERE (v_inicio IS NULL OR v.fecha_registro >= v_inicio)
          AND (v_fin IS NULL OR v.fecha_registro <= v_fin)
          AND (p_id_empleado IS NULL OR v.id_empleado = p_id_empleado)
          AND (p_tipo_comprobante IS NULL OR v.tipo_comprobante = p_tipo_comprobante)
          AND (p_estado IS NULL OR v.estado = p_estado)
        ORDER BY v.fecha_registro DESC;
END SP_REPORTE_VENTAS_FILTRADO;
/

PROMPT >>> [3/4] Creando Procedimiento 3: SP_REGISTRAR_PAGO_PENDIENTE...
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

    v_saldo_actual := FN_CALCULAR_SALDO_VENTA(p_id_venta);

    IF p_monto > v_saldo_actual THEN
        RAISE_APPLICATION_ERROR(-20024, 'El monto a abonar excede el saldo pendiente restante (Saldo actual: S/ ' || v_saldo_actual || ')');
    END IF;

    INSERT INTO PAGO (
        fecha_registro, numero_operacion, monto, metodo_pago, id_venta
    ) VALUES (
        SYSDATE, p_numero_operacion, p_monto, p_metodo_pago, p_id_venta
    ) RETURNING id_pago INTO p_id_pago;

    v_nuevo_saldo := v_saldo_actual - p_monto;
    p_saldo_restante := v_nuevo_saldo;

    IF v_nuevo_saldo = 0 THEN
        UPDATE VENTA 
        SET estado = 2 
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

-- ----------------------------------------------------------------------------
-- 4. TRIGGERS
-- ----------------------------------------------------------------------------
PROMPT >>> [4/4] Creando Trigger 1: TRG_ACTUALIZAR_ESTADO_VENTA_PAGO...
CREATE OR REPLACE TRIGGER TRG_ACTUALIZAR_ESTADO_VENTA_PAGO
FOR INSERT OR UPDATE ON PAGO
COMPOUND TRIGGER

    TYPE t_ventas IS TABLE OF NUMBER INDEX BY PLS_INTEGER;
    v_ventas t_ventas;
    v_total NUMBER(12,2);
    v_pagado NUMBER(12,2);
    v_estado_actual NUMBER;

    AFTER EACH ROW IS
    BEGIN
        IF :NEW.id_venta IS NOT NULL THEN
            v_ventas(v_ventas.COUNT + 1) := :NEW.id_venta;
        END IF;
    END AFTER EACH ROW;

    AFTER STATEMENT IS
    BEGIN
        FOR i IN 1 .. v_ventas.COUNT LOOP
            BEGIN
                SELECT total, estado
                INTO v_total, v_estado_actual
                FROM VENTA
                WHERE id_venta = v_ventas(i);

                IF v_estado_actual != 3 THEN
                    SELECT COALESCE(SUM(monto), 0)
                    INTO v_pagado
                    FROM PAGO
                    WHERE id_venta = v_ventas(i);

                    IF v_pagado >= v_total AND v_estado_actual != 2 THEN
                        UPDATE VENTA 
                        SET estado = 2 
                        WHERE id_venta = v_ventas(i);
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

PROMPT >>> [4/4] Creando Trigger 2: TRG_DESCONTAR_STOCK_POR_VENTA...
CREATE OR REPLACE TRIGGER TRG_DESCONTAR_STOCK_POR_VENTA
AFTER INSERT ON PRODUCTO_VENDIDO
FOR EACH ROW
DECLARE
    v_stock_actual NUMBER;
    v_stock_nuevo NUMBER;
    v_id_usuario NUMBER;
BEGIN
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

    UPDATE PRODUCTO
    SET stock = v_stock_nuevo
    WHERE id_producto = :NEW.id_producto;

    BEGIN
        SELECT emp.id_usuario
        INTO v_id_usuario
        FROM VENTA v
        JOIN EMPLEADO emp ON v.id_empleado = emp.id_persona
        WHERE v.id_venta = :NEW.id_venta;
    EXCEPTION
        WHEN OTHERS THEN
            v_id_usuario := 1;
    END;

    INSERT INTO MOVIMIENTO_STOCK_LOG (
        fecha_registro, cantidad, stock_previo, stock_posterior, 
        motivo, observaciones, id_producto, id_usuario
    ) VALUES (
        SYSDATE, :NEW.cantidad, v_stock_actual, v_stock_nuevo, 
        5, 'Salida automatica por venta comprobante ID: ' || :NEW.id_venta, :NEW.id_producto, v_id_usuario
    );
END TRG_DESCONTAR_STOCK_POR_VENTA;
/

PROMPT >>> INSTALACION DE OBJETOS PL/SQL DE VENTAS COMPLETADA CON EXITO.

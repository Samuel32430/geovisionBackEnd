-- ============================================================================
-- PROYECTO GEOVISION - MODULO DE VENTAS Y REPORTES
-- Objeto: PROCEDIMIENTO ALMACENADO 2 / 3
-- Nombre: SP_REPORTE_VENTAS_FILTRADO
-- Finalidad: Calcular en una sola operacion las metricas agregadas globales
--            (parametros OUT) y abrir un cursor SYS_REFCURSOR con el detalle.
-- ============================================================================

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

    -- 1. Calcular metricas globales agregadas del periodo
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

    -- 2. Abrir cursor referenciado con el detalle tabular para la interfaz o PDF
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

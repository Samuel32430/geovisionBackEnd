-- ============================================================================
-- PROYECTO GEOVISION - MODULO DE VENTAS Y REPORTES
-- Objeto: VISTA 1 / 3
-- Nombre: VW_REPORTE_VENTAS_CONSOLIDADO
-- Finalidad: Centralizar y enriquecer en una consulta unica el detalle de ventas,
--            vinculando cliente, vendedor, comprobantes, pagos, saldos y cantidades.
-- ============================================================================

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

-- ============================================================================
-- PROYECTO GEOVISION - MODULO DE VENTAS Y REPORTES
-- Objeto: VISTA 3 / 3
-- Nombre: VW_VENTAS_SALDOS_PENDIENTES
-- Finalidad: Filtrar y proyectar unicamente las ventas que mantienen saldos
--            pendientes por cobrar (CUS07 - Revisar Pago Pendiente).
-- ============================================================================

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
WHERE v.estado = 1 -- Solo ventas en estado PENDIENTE
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

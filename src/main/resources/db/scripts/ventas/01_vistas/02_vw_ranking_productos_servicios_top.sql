-- ============================================================================
-- PROYECTO GEOVISION - MODULO DE VENTAS Y REPORTES
-- Objeto: VISTA 2 / 3
-- Nombre: VW_RANKING_PRODUCTOS_SERVICIOS_TOP
-- Finalidad: Agrupar la rotacion de productos fisicos y servicios profesionales,
--            calculando unidades, recaudacion y posicion analitica (DENSE_RANK).
-- ============================================================================

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
    WHERE v.estado != 3 -- Excluir ventas anuladas

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
    WHERE v.estado != 3 -- Excluir ventas anuladas
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

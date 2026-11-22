-- CONSULTA 01 / RF-55: carga de trabajo por dia de venta.
-- Una venta con varios lentes cuenta una vez en ventas y varias veces en lentes.
SELECT TRUNC(fecha_registro) AS dia_venta,
       COUNT(DISTINCT id_venta) AS ventas_pendientes,
       COUNT(*) AS lentes_pendientes
FROM V_S_VENTAS_PENDIENTES_OT
GROUP BY TRUNC(fecha_registro)
ORDER BY dia_venta DESC;

-- CONSULTA 02 / RF-57 a RF-59: resumen logistico por laboratorio.
-- Incluye laboratorios sin envios y resume todos los envios, incluso reenvios.
SELECT l.id_laboratorio, l.nombre AS laboratorio,
       DECODE(l.estado, 1, 'Activo', 'Inactivo') AS estado_laboratorio,
       COUNT(e.id_envio_laboratorio) AS total_envios,
       COUNT(e.fecha_recepcion) AS recibidos,
       COUNT(e.id_envio_laboratorio) - COUNT(e.fecha_recepcion) AS por_recibir
FROM envio_laboratorio e
RIGHT JOIN laboratorio l ON l.id_laboratorio = e.id_laboratorio
GROUP BY l.id_laboratorio, l.nombre, l.estado
ORDER BY laboratorio, l.id_laboratorio;

-- CONSULTA 03 / RF-60 a RF-63, RF-65 y RF-66: estado antes de entregar.
-- El saldo solo se muestra cuando la receta identifica una venta sin ambiguedad.
SELECT s.id_orden_trabajo, s.estado_ot, s.laboratorio,
       CASE s.aprobado WHEN 1 THEN 'Aprobado' WHEN 0 THEN 'Rechazado'
                        ELSE 'Sin evaluar' END AS control_calidad,
       s.fecha_notificacion, v.total - NVL(pg.pagado, 0) AS saldo_pendiente,
       CASE WHEN v.id_venta IS NULL THEN 'Venta no identificada'
            WHEN v.estado = 3 THEN 'Venta anulada'
            WHEN s.estado = 6 AND s.fecha_entrega IS NULL AND s.aprobado = 1
                 AND s.fecha_recepcion IS NOT NULL AND s.fecha_revision IS NOT NULL
                 AND EXISTS (SELECT 1 FROM medida m WHERE m.id_envio_laboratorio = s.id_envio_laboratorio)
                 AND v.id_paciente = r.id_paciente AND v.total > 0 AND v.total = pg.pagado
                THEN 'Cumple condiciones de entrega'
            ELSE 'No habilitado'
       END AS situacion_entrega
FROM V_S_SEGUIMIENTO_ORDEN_TRABAJO s
JOIN lente l ON l.id_lente = s.id_lente
JOIN receta r ON r.id_receta = l.id_receta
LEFT JOIN venta v ON v.id_receta = l.id_receta
                 AND (SELECT COUNT(*) FROM venta v2 WHERE v2.id_receta = l.id_receta) = 1
LEFT JOIN (
    SELECT id_venta, SUM(monto) AS pagado FROM pago GROUP BY id_venta
) pg ON pg.id_venta = v.id_venta
ORDER BY s.id_orden_trabajo;

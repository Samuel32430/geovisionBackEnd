-- Bloques independientes para ejecutar en el esquema de practica.

-- PARTICIONAMIENTO / RF-57 a RF-60: copia de los envios para reportes mensuales.
-- Requiere una edicion/opcion Oracle que habilite particionamiento por intervalo.
CREATE TABLE ENVIO_LABORATORIO_HIST
PARTITION BY RANGE (fecha_envio)
INTERVAL (NUMTOYMINTERVAL(1, 'MONTH'))
(
    PARTITION p_anteriores_2026 VALUES LESS THAN (DATE '2026-01-01')
)
AS
SELECT id_envio_laboratorio, id_orden_trabajo, id_laboratorio, fecha_envio,
       fecha_recepcion_estimada, fecha_recepcion, fecha_revision, aprobado
FROM envio_laboratorio;

SELECT partition_name, partition_position, interval
FROM user_tab_partitions
WHERE table_name = 'ENVIO_LABORATORIO_HIST'
ORDER BY partition_position;

-- ENMASCARAMIENTO / RF-55: muestra hasta los ultimos cuatro caracteres del documento.
-- Para un reporte restringido, conceder al rol lector solo SELECT sobre esta vista.
CREATE OR REPLACE VIEW V_S_PENDIENTES_OT_MASCARADOS AS
SELECT id_venta, numero_comprobante, id_lente,
       CASE WHEN LENGTH(numero_documento) <= 4
            THEN RPAD('*', LENGTH(numero_documento), '*')
            ELSE RPAD('*', LENGTH(numero_documento) - 4, '*') || SUBSTR(numero_documento, -4)
       END AS documento_enmascarado
FROM V_S_VENTAS_PENDIENTES_OT;

-- AUDITORIA / RF-56 a RF-63, RF-65 y entrega: ejecutar en la PDB de practica
-- como propietario con AUDIT_ADMIN, o calificar las tablas con su esquema desde el DBA.
CREATE AUDIT POLICY POL_AUD_ORDEN_TRABAJO
    ACTIONS INSERT ON orden_trabajo, UPDATE ON orden_trabajo, DELETE ON orden_trabajo,
            INSERT ON envio_laboratorio, UPDATE ON envio_laboratorio, DELETE ON envio_laboratorio;

AUDIT POLICY POL_AUD_ORDEN_TRABAJO;

-- Consultar desde una nueva sesion con AUDIT_VIEWER o AUDIT_ADMIN.
SELECT event_timestamp, dbusername, action_name, object_schema, object_name, return_code
FROM unified_audit_trail
WHERE unified_audit_policies LIKE '%POL_AUD_ORDEN_TRABAJO%'
ORDER BY event_timestamp DESC;

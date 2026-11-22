-- GeoVision / Elaboracion de Orden de Trabajo / Oracle Database 19c
-- Ejecutar como script en el esquema de desarrollo, despues del DDL base.

-- VISTA 01 / RF-55: una fila por lente elegible de una venta sin OT para ese lente.
CREATE OR REPLACE VIEW V_S_VENTAS_PENDIENTES_OT AS
SELECT v.id_venta, v.numero_comprobante, v.fecha_registro, v.total,
       l.id_lente, v.id_receta, v.id_paciente,
       p.nombres, p.apellidos, p.numero_documento
FROM venta v
JOIN lente l ON l.id_receta = v.id_receta
JOIN paciente pa ON pa.id_persona = v.id_paciente
JOIN persona p ON p.id_persona = pa.id_persona
WHERE v.estado IN (1, 2)
  AND (SELECT COUNT(*) FROM venta v2 WHERE v2.id_receta = v.id_receta) = 1
  AND EXISTS (SELECT 1 FROM pago pg WHERE pg.id_venta = v.id_venta AND pg.monto > 0)
  AND EXISTS (SELECT 1 FROM lente_medida lm WHERE lm.id_lente = l.id_lente)
  AND NOT EXISTS (SELECT 1 FROM orden_trabajo ot WHERE ot.id_lente = l.id_lente);

-- VISTA 02 / RF-56: ficha de OT, una fila por medida de referencia.
CREATE OR REPLACE VIEW V_S_DETALLE_ORDEN_TRABAJO AS
SELECT ot.id_orden_trabajo, ot.estado, ot.fecha_registro, l.id_lente,
       r.id_receta, r.id_paciente, per.nombres, per.apellidos,
       mat.nombre AS material_cristal, pr.nombre AS montura,
       m.id_medida, m.tipo_medida, m.distancia_pupilar, m.prisma,
       od.esfera AS esfera_od, od.cilindro AS cilindro_od,
       od.eje AS eje_od, od.adicion AS adicion_od,
       oi.esfera AS esfera_oi, oi.cilindro AS cilindro_oi,
       oi.eje AS eje_oi, oi.adicion AS adicion_oi,
       (SELECT LISTAGG(tc.nombre, ', ' ON OVERFLOW TRUNCATE)
                   WITHIN GROUP (ORDER BY tc.nombre)
        FROM lente_tratamiento_cristal lt
        JOIN tratamiento_cristal tc ON tc.id_tratamiento_cristal = lt.id_tratamiento_cristal
        WHERE lt.id_lente = l.id_lente) AS tratamientos
FROM orden_trabajo ot
JOIN lente l ON l.id_lente = ot.id_lente
JOIN receta r ON r.id_receta = l.id_receta
JOIN persona per ON per.id_persona = r.id_paciente
JOIN material_cristal mat ON mat.id_material_cristal = l.id_material_cristal
LEFT JOIN producto pr ON pr.id_producto = l.id_producto
LEFT JOIN (
    SELECT lm.id_lente, me.*
    FROM lente_medida lm
    JOIN medida me ON me.id_medida = lm.id_medida
    WHERE me.id_envio_laboratorio IS NULL
) m ON m.id_lente = l.id_lente
LEFT JOIN medida_ojo od ON od.id_medida_ojo = m.id_medida_ojo_derecha
LEFT JOIN medida_ojo oi ON oi.id_medida_ojo = m.id_medida_ojo_izquierda;

-- VISTA 03 / RF-57 a RF-63, RF-65 y RF-66: una fila por OT, con su ultimo envio.
CREATE OR REPLACE VIEW V_S_SEGUIMIENTO_ORDEN_TRABAJO AS
SELECT ot.id_orden_trabajo, ot.id_lente, ot.estado,
       CASE ot.estado
           WHEN 1 THEN 'Pendiente de Procesar'
           WHEN 2 THEN 'Enviado a Laboratorio'
           WHEN 3 THEN 'Recibido de Laboratorio'
           WHEN 4 THEN 'Control de Calidad Aprobado'
           WHEN 5 THEN 'Retrasado'
           WHEN 6 THEN 'Listo para Entrega'
           WHEN 7 THEN 'Entregado'
           WHEN 8 THEN 'Anulado'
           ELSE 'Estado desconocido'
       END AS estado_ot,
       e.id_envio_laboratorio, lab.id_laboratorio, lab.nombre AS laboratorio,
       e.fecha_envio, e.fecha_recepcion_estimada, e.fecha_recepcion,
       e.fecha_revision, e.aprobado, ot.fecha_notificacion, ot.fecha_entrega
FROM orden_trabajo ot
LEFT JOIN (
    SELECT el.*,
           ROW_NUMBER() OVER (PARTITION BY id_orden_trabajo
                              ORDER BY fecha_envio DESC, id_envio_laboratorio DESC) AS orden_envio
    FROM envio_laboratorio el
) e ON e.id_orden_trabajo = ot.id_orden_trabajo AND e.orden_envio = 1
LEFT JOIN laboratorio lab ON lab.id_laboratorio = e.id_laboratorio;

-- PROCEDIMIENTO 02 / RF-55: consulta con filtro literal opcional.
CREATE OR REPLACE PROCEDURE SP_LISTAR_VENTAS_PENDIENTES_OT (
    p_termino IN VARCHAR2,
    p_ventas  OUT SYS_REFCURSOR
) IS
    v_termino VARCHAR2(32767) := LOWER(TRIM(p_termino));
BEGIN
    OPEN p_ventas FOR
        SELECT id_venta, numero_comprobante, id_lente, id_receta, id_paciente,
               nombres || ' ' || apellidos AS paciente, total
        FROM V_S_VENTAS_PENDIENTES_OT
        WHERE v_termino IS NULL
           OR INSTR(LOWER(nombres || ' ' || apellidos), v_termino) > 0
           OR INSTR(LOWER(numero_documento), v_termino) > 0
           OR INSTR(LOWER(numero_comprobante), v_termino) > 0
        ORDER BY fecha_registro DESC, id_venta DESC, id_lente;
END SP_LISTAR_VENTAS_PENDIENTES_OT;
/

-- PROCEDIMIENTO 03 / RF-59: recibe el ultimo envio y deja la OT en estado 3.
CREATE OR REPLACE PROCEDURE SP_REGISTRAR_RECEPCION_OT (
    p_id_envio IN envio_laboratorio.id_envio_laboratorio%TYPE
) IS
    v_id_ot orden_trabajo.id_orden_trabajo%TYPE;
    v_estado orden_trabajo.estado%TYPE;
BEGIN
    SAVEPOINT sp_recepcion_ot;

    SELECT id_orden_trabajo INTO v_id_ot
    FROM envio_laboratorio WHERE id_envio_laboratorio = p_id_envio;

    SELECT estado INTO v_estado
    FROM orden_trabajo WHERE id_orden_trabajo = v_id_ot FOR UPDATE;

    IF v_estado <> 2 THEN
        RAISE_APPLICATION_ERROR(-20220, 'La OT debe estar Enviada a Laboratorio');
    END IF;

    UPDATE envio_laboratorio
    SET fecha_recepcion = SYSDATE
    WHERE id_envio_laboratorio = p_id_envio
      AND fecha_recepcion IS NULL
      AND id_envio_laboratorio = (
          SELECT id_envio_laboratorio FROM envio_laboratorio
          WHERE id_orden_trabajo = v_id_ot
          ORDER BY fecha_envio DESC, id_envio_laboratorio DESC FETCH FIRST 1 ROW ONLY
      );

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20221, 'Solo se puede recibir el ultimo envio pendiente');
    END IF;

    UPDATE orden_trabajo SET estado = 3 WHERE id_orden_trabajo = v_id_ot;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        ROLLBACK TO sp_recepcion_ot;
        RAISE_APPLICATION_ERROR(-20222, 'El envio o la OT no existe');
    WHEN OTHERS THEN
        ROLLBACK TO sp_recepcion_ot;
        RAISE;
END SP_REGISTRAR_RECEPCION_OT;
/

-- PROCEDIMIENTO 01 / RF-61 y RF-62: codigo original de Caleb.

-- Oracle 19c. Propuesta NO compilada/ejecutada. Solo lectura al invocarla.
-- Descubre atributos; compara por igualdad salvo regla por TABLA.COLUMNA.
CREATE OR REPLACE PROCEDURE SP_AP_COMPARAR_MEDIDA (
    p_id_referencia IN MEDIDA.id_medida%TYPE,
    p_medida        IN CLOB,
    p_reglas        IN CLOB,
    p_aprobado      OUT NUMBER,
    p_detalle       OUT CLOB
) AUTHID DEFINER IS
    CURSOR campos IS
        SELECT g.grupo, c.table_name, c.column_name, c.data_type, c.nullable
        FROM user_tab_columns c
        JOIN (
            SELECT 'MEDIDA' tabla, 'MEDIDA' grupo FROM dual UNION ALL
            SELECT 'MEDIDA_OJO', 'OJO_DERECHO' FROM dual UNION ALL
            SELECT 'MEDIDA_OJO', 'OJO_IZQUIERDO' FROM dual
        ) g ON g.tabla = c.table_name
        WHERE NOT EXISTS (
            SELECT 1 FROM user_cons_columns cc
            JOIN user_constraints k ON k.constraint_name = cc.constraint_name
                AND k.table_name = cc.table_name
            WHERE cc.table_name = c.table_name AND cc.column_name = c.column_name
              AND k.constraint_type IN ('P', 'R')
        )
        ORDER BY g.grupo, c.column_id;
    v_json CLOB;
    v_ref JSON_OBJECT_T;
    v_control JSON_OBJECT_T;
    v_reglas JSON_OBJECT_T := JSON_OBJECT_T();
    v_regla JSON_OBJECT_T;
    v_claves JSON_KEY_LIST;
    v_r JSON_OBJECT_T;
    v_c JSON_OBJECT_T;
    v_a JSON_ELEMENT_T;
    v_b JSON_ELEMENT_T;
    v_filas JSON_ARRAY_T := JSON_ARRAY_T();
    v_fila JSON_OBJECT_T;
    v_clave VARCHAR2(300);
    v_modo VARCHAR2(20);
    v_tolerancia NUMBER;
    v_periodo NUMBER;
    v_diferencia NUMBER;
    v_ok NUMBER;
    v_total PLS_INTEGER := 0;
    v_existe PLS_INTEGER;
BEGIN
    p_aprobado := NULL;
    p_detalle := NULL;
    v_control := JSON_OBJECT_T.parse(p_medida);
    v_control.on_error(1);
    IF p_reglas IS NOT NULL THEN v_reglas := JSON_OBJECT_T.parse(p_reglas); END IF;
    v_reglas.on_error(1);
    -- Detecta reglas obsoletas despues de renombrar/eliminar columnas.
    v_claves := v_reglas.get_keys();
    FOR i IN 1 .. v_claves.count LOOP
        SELECT COUNT(*) INTO v_existe FROM user_tab_columns
        WHERE table_name IN ('MEDIDA', 'MEDIDA_OJO')
          AND table_name || '.' || column_name = v_claves(i);
        IF v_existe = 0 THEN
            RAISE_APPLICATION_ERROR(-20201, 'Regla sin columna: ' || v_claves(i));
        END IF;
    END LOOP;
    -- SQL fijo ejecutado dinamicamente: expande comodines contra el esquema actual.
    -- Un bind para el ID; ninguna entrada del usuario se concatena como SQL.
    EXECUTE IMMEDIATE q'~
        SELECT JSON_OBJECT(m.*,
                   'OJO_DERECHO' VALUE JSON_OBJECT(od.* RETURNING CLOB),
                   'OJO_IZQUIERDO' VALUE JSON_OBJECT(oi.* RETURNING CLOB) RETURNING CLOB)
        FROM medida m
        JOIN medida_ojo od ON od.id_medida_ojo = m.id_medida_ojo_derecha
        JOIN medida_ojo oi ON oi.id_medida_ojo = m.id_medida_ojo_izquierda
        WHERE m.id_medida = :id AND m.id_envio_laboratorio IS NULL
    ~' INTO v_json USING p_id_referencia;
    v_ref := JSON_OBJECT_T.parse(v_json);
    p_aprobado := 1;
    FOR campo IN campos LOOP
        v_clave := campo.table_name || '.' || campo.column_name;
        v_modo := 'EXACTO';
        v_tolerancia := 0;
        v_periodo := NULL;
        IF v_reglas.has(v_clave) THEN
            v_regla := v_reglas.get_object(v_clave);
            v_regla.on_error(1);
            v_modo := v_regla.get_string('modo');
            IF v_modo IN ('ABS', 'CIRCULAR') THEN
                v_tolerancia := v_regla.get_number('tolerancia');
                IF v_tolerancia IS NULL OR v_tolerancia < 0 THEN
                    RAISE_APPLICATION_ERROR(-20202, 'Tolerancia invalida: ' || v_clave);
                END IF;
            END IF;
            IF v_modo = 'CIRCULAR' THEN
                v_periodo := v_regla.get_number('periodo');
                IF v_periodo IS NULL OR v_periodo <= 0 THEN
                    RAISE_APPLICATION_ERROR(-20202, 'Periodo invalido: ' || v_clave);
                END IF;
            END IF;
        END IF;
        IF v_modo = 'IGNORAR' THEN CONTINUE; END IF;
        IF v_modo IS NULL OR v_modo NOT IN ('EXACTO', 'ABS', 'CIRCULAR') THEN
            RAISE_APPLICATION_ERROR(-20202, 'Modo invalido: ' || v_clave);
        END IF;
        IF campo.data_type NOT IN ('NUMBER', 'VARCHAR2', 'NVARCHAR2', 'CHAR', 'NCHAR')
           OR (campo.data_type <> 'NUMBER' AND v_modo <> 'EXACTO') THEN
            RAISE_APPLICATION_ERROR(-20203, 'Tipo o regla no compatible: ' || v_clave);
        END IF;
        IF campo.grupo = 'MEDIDA' THEN v_r := v_ref; v_c := v_control;
        ELSE v_r := v_ref.get_object(campo.grupo); v_c := v_control.get_object(campo.grupo);
        END IF;
        v_c.on_error(1);
        v_a := v_r.get(campo.column_name);
        v_b := v_c.get(campo.column_name); -- Clave ausente es error, no null.
        IF NOT v_b.is_null() AND
           ((campo.data_type = 'NUMBER' AND NOT v_b.is_number()) OR
            (campo.data_type <> 'NUMBER' AND NOT v_b.is_string())) THEN
            RAISE_APPLICATION_ERROR(-20204, 'Tipo JSON incorrecto: ' || v_clave);
        END IF;
        v_diferencia := NULL;
        IF v_a.is_null() OR v_b.is_null() THEN
            IF campo.nullable = 'N' THEN
                RAISE_APPLICATION_ERROR(-20204, 'Null en columna obligatoria: ' || v_clave);
            END IF;
            v_ok := CASE WHEN v_a.is_null() AND v_b.is_null() THEN 1 ELSE 0 END;
        ELSIF campo.data_type = 'NUMBER' THEN
            v_diferencia := ABS(v_c.get_number(campo.column_name) - v_r.get_number(campo.column_name));
            IF v_modo = 'CIRCULAR' THEN
                IF v_c.get_number(campo.column_name) NOT BETWEEN 0 AND v_periodo
                   OR v_r.get_number(campo.column_name) NOT BETWEEN 0 AND v_periodo THEN
                    RAISE_APPLICATION_ERROR(-20204, 'Valor fuera del periodo: ' || v_clave);
                END IF;
                v_diferencia := LEAST(v_diferencia, v_periodo - v_diferencia);
            END IF;
            v_ok := CASE WHEN v_diferencia <= v_tolerancia THEN 1 ELSE 0 END;
        ELSE
            v_ok := CASE WHEN v_c.get_string(campo.column_name) = v_r.get_string(campo.column_name) THEN 1 ELSE 0 END;
        END IF;
        p_aprobado := LEAST(p_aprobado, v_ok);
        v_fila := JSON_OBJECT_T();
        v_fila.put('campo', campo.grupo || '.' || campo.column_name);
        v_fila.put('referencia', v_a);
        v_fila.put('recibido', v_b);
        v_fila.put('modo', v_modo);
        v_fila.put('diferencia', v_diferencia);
        v_fila.put('tolerancia', v_tolerancia);
        v_fila.put('cumple', v_ok);
        v_filas.append(v_fila);
        v_total := v_total + 1;
    END LOOP;
    IF v_total = 0 THEN RAISE_APPLICATION_ERROR(-20205, 'No hay atributos comparables'); END IF;
    p_detalle := v_filas.to_clob();
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20206, 'Referencia inexistente, de laboratorio o sin ambos ojos');
END SP_AP_COMPARAR_MEDIDA;
/

-- FUNCION 01 / RF-61 y RF-62: distancia entre ejes validos.

CREATE OR REPLACE FUNCTION FN_DIFERENCIA_ANGULAR (
    p_valor1   IN NUMBER,
    p_valor2   IN NUMBER,
    p_periodo  IN NUMBER DEFAULT 180
) RETURN NUMBER IS
    v_diff NUMBER;
BEGIN
    -- Si algun valor es nulo o el periodo es invalido, no se puede calcular
    IF p_valor1 IS NULL OR p_valor2 IS NULL OR p_periodo IS NULL OR p_periodo <= 0
       OR p_valor1 NOT BETWEEN 0 AND p_periodo OR p_valor2 NOT BETWEEN 0 AND p_periodo THEN
        RETURN NULL;
    END IF;
    -- 1. Diferencia lineal directa
    v_diff := ABS(p_valor1 - p_valor2);
    -- 2. Retorna la distancia mas corta en el semicirculo/circulo
    RETURN LEAST(v_diff, p_periodo - v_diff);
END FN_DIFERENCIA_ANGULAR;
/

-- FUNCION 02 / RF-61 y RF-62: resultado de la comparacion.

CREATE OR REPLACE FUNCTION FN_ES_MEDIDA_APROBADA (
    p_id_referencia IN MEDIDA.id_medida%TYPE,
    p_medida_json   IN CLOB,
    p_reglas_json   IN CLOB DEFAULT NULL
) RETURN VARCHAR2 IS
    v_aprobado NUMBER;
    v_detalle  CLOB;
BEGIN
    -- Ejecutamos el procedimiento que compara
    SP_AP_COMPARAR_MEDIDA(
        p_id_referencia => p_id_referencia,
        p_medida        => p_medida_json,
        p_reglas        => p_reglas_json,
        p_aprobado      => v_aprobado,
        p_detalle       => v_detalle
    );
    -- Convertimos el resultado numerico (1 o 0) en un texto amigable
    IF v_aprobado = 1 THEN
        RETURN 'APROBADO';
    ELSE
        RETURN 'RECHAZADO';
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 'ERROR: ' || SQLERRM;
END FN_ES_MEDIDA_APROBADA;
/

-- TRIGGER 01 / RF-60: valida los ejes registrados.

CREATE OR REPLACE TRIGGER TRG_VALIDAR_RANGO_EJE
BEFORE INSERT OR UPDATE ON MEDIDA_OJO
FOR EACH ROW
BEGIN
    -- En optometria, el eje debe estar entre 0 y 180 grados, inclusive
    IF :NEW.eje IS NOT NULL AND (:NEW.eje < 0 OR :NEW.eje > 180) THEN
        RAISE_APPLICATION_ERROR(
            -20001,
            'Error de validacion: El eje del ojo debe estar entre 0 y 180 grados.'
        );
    END IF;
END TRG_VALIDAR_RANGO_EJE;
/

-- TRIGGER 02 / RF-60: protege la fila de lensometria vinculada a un envio.

CREATE OR REPLACE TRIGGER TRG_PROTEGER_MEDIDA_LABORATORIO
BEFORE UPDATE OR DELETE ON MEDIDA
FOR EACH ROW
BEGIN
    -- Si la medida corresponde a un control de laboratorio, protege su fila
    IF :OLD.id_envio_laboratorio IS NOT NULL THEN
        RAISE_APPLICATION_ERROR(
            -20002,
            'Operacion denegada: La medida del control de laboratorio no puede modificarse ni eliminarse.'
        );
    END IF;
END TRG_PROTEGER_MEDIDA_LABORATORIO;
/

-- ============================================================================
-- PROYECTO GEOVISION - MODULO DE VENTAS Y REPORTES
-- Objeto: FUNCION 2 / 2
-- Nombre: FN_GENERAR_CORRELATIVO_COMPROBANTE
-- Finalidad: Calcular de forma atomica y consecutiva el siguiente correlativo
--            de comprobante segun su tipo fiscal (B001-, F001-, NV01-).
-- ============================================================================

CREATE OR REPLACE FUNCTION FN_GENERAR_CORRELATIVO_COMPROBANTE(p_tipo_comprobante IN NUMBER)
RETURN VARCHAR2
IS
    v_prefijo VARCHAR2(10);
    v_ultimo VARCHAR2(100);
    v_siguiente NUMBER := 1;
    v_num_str VARCHAR2(50);
BEGIN
    CASE p_tipo_comprobante
        WHEN 1 THEN v_prefijo := 'B001-';  -- Boleta
        WHEN 2 THEN v_prefijo := 'F001-';  -- Factura
        ELSE v_prefijo := 'NV01-';         -- Nota de Venta
    END CASE;

    -- Obtener el maximo correlativo existente con ese prefijo
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

-- ÓPTICA GEO VISIÓN — DDL Oracle 19c+
-- Esquema NUEVO. No contiene DROP. No ejecutar sobre un esquema que ya posea las tablas.
-- Versión corregida conservando entidades, atributos, PK, UNIQUE y CHECK del DDL proporcionado.
-- FK individuales al final; catálogo PENDIENTE agregado.
-- Todo usuario está asociado a un empleado (FK circular diferida).
-- Escala agudeza_visual: decimal. No se impone rango clínico no definido.

/* ==================================================
   FASE 1: TABLAS, CLAVES PRIMARIAS, UNIQUE Y CHECK
   ================================================== */
CREATE TABLE CATEGORIA (
    id_categoria NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    descripcion VARCHAR2(1000),
    estado NUMBER(1) DEFAULT 1 NOT NULL CHECK (estado IN (0,1)),
    fecha_registro DATE DEFAULT SYSDATE NOT NULL,
    CONSTRAINT CATEGORIA_pk PRIMARY KEY (id_categoria),
    CONSTRAINT CATEGORIA_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]'))
);

CREATE TABLE MARCA (
    id_marca NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    CONSTRAINT MARCA_pk PRIMARY KEY (id_marca),
    CONSTRAINT MARCA_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]'))
);

CREATE TABLE MATERIAL_CRISTAL (
    id_material_cristal NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    CONSTRAINT MATERIAL_CRISTAL_pk PRIMARY KEY (id_material_cristal),
    CONSTRAINT MATERIAL_CRISTAL_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]'))
);

CREATE TABLE TRATAMIENTO_CRISTAL (
    id_tratamiento_cristal NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    CONSTRAINT TRATAMIENTO_CRISTAL_pk PRIMARY KEY (id_tratamiento_cristal),
    CONSTRAINT TRATAMIENTO_CRISTAL_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]'))
);

CREATE TABLE MODELO_MONTURA (
    id_modelo_montura NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    CONSTRAINT MODELO_MONTURA_pk PRIMARY KEY (id_modelo_montura),
    CONSTRAINT MODELO_MONTURA_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]'))
);

CREATE TABLE MATERIAL_MONTURA (
    id_material_montura NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    CONSTRAINT MATERIAL_MONTURA_pk PRIMARY KEY (id_material_montura),
    CONSTRAINT MATERIAL_MONTURA_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]'))
);

CREATE TABLE DISENO_LENTE (
    id_diseno_lente NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    CONSTRAINT DISENO_LENTE_pk PRIMARY KEY (id_diseno_lente),
    CONSTRAINT DISENO_LENTE_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]'))
);

CREATE TABLE TIPO_DOCUMENTO (
    id_tipo_documento NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    CONSTRAINT TIPO_DOCUMENTO_pk PRIMARY KEY (id_tipo_documento),
    CONSTRAINT TIPO_DOCUMENTO_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]'))
);

CREATE TABLE CARGO (
    id_cargo NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    CONSTRAINT CARGO_pk PRIMARY KEY (id_cargo),
    CONSTRAINT CARGO_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]'))
);

CREATE TABLE TIPO_MEDIDA (
    id_tipo_medida NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    CONSTRAINT TIPO_MEDIDA_pk PRIMARY KEY (id_tipo_medida),
    CONSTRAINT TIPO_MEDIDA_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]'))
);

CREATE TABLE TIPO_OJO (
    id_tipo_ojo NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    CONSTRAINT TIPO_OJO_pk PRIMARY KEY (id_tipo_ojo),
    CONSTRAINT TIPO_OJO_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]'))
);

CREATE TABLE ESTADO_VENTA (
    id_estado_venta NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    CONSTRAINT ESTADO_VENTA_pk PRIMARY KEY (id_estado_venta),
    CONSTRAINT ESTADO_VENTA_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]'))
);

CREATE TABLE TIPO_COMPROBANTE (
    id_tipo_comprobante NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    CONSTRAINT TIPO_COMPROBANTE_pk PRIMARY KEY (id_tipo_comprobante),
    CONSTRAINT TIPO_COMPROBANTE_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]'))
);

CREATE TABLE METODO_PAGO (
    id_metodo_pago NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    CONSTRAINT METODO_PAGO_pk PRIMARY KEY (id_metodo_pago),
    CONSTRAINT METODO_PAGO_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]'))
);

CREATE TABLE ESTADO_ORDEN_TRABAJO (
    id_estado_orden_trabajo NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    CONSTRAINT ESTADO_ORDEN_TRABAJO_pk PRIMARY KEY (id_estado_orden_trabajo),
    CONSTRAINT ESTADO_ORDEN_TRABAJO_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]'))
);

CREATE TABLE ROL (
    id_rol NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    CONSTRAINT ROL_pk PRIMARY KEY (id_rol),
    CONSTRAINT ROL_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]'))
);

CREATE TABLE MODULO (
    id_modulo NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    CONSTRAINT MODULO_pk PRIMARY KEY (id_modulo),
    CONSTRAINT MODULO_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]'))
);

CREATE TABLE PERMISO (
    id_permiso NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    id_modulo NUMBER NOT NULL,
    CONSTRAINT PERMISO_pk PRIMARY KEY (id_permiso),
    CONSTRAINT PERMISO_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]'))
);

CREATE TABLE PERSONA (
    id_persona NUMBER GENERATED ALWAYS AS IDENTITY,
    nombres VARCHAR2(200) NOT NULL,
    apellidos VARCHAR2(200) NOT NULL,
    telefono VARCHAR2(50),
    fecha_nacimiento DATE,
    sexo NUMBER(1) CHECK (sexo IN (0,1)),
    direccion VARCHAR2(500),
    CONSTRAINT PERSONA_pk PRIMARY KEY (id_persona),
    CONSTRAINT PERSONA_nombres_texto_ck CHECK (REGEXP_LIKE(nombres, '[^[:space:]]')),
    CONSTRAINT PERSONA_apellidos_texto_ck CHECK (REGEXP_LIKE(apellidos, '[^[:space:]]'))
);

CREATE TABLE DOCUMENTO (
    id_documento NUMBER GENERATED ALWAYS AS IDENTITY,
    numero VARCHAR2(50) NOT NULL,
    id_persona NUMBER NOT NULL,
    id_tipo_documento NUMBER NOT NULL,
    CONSTRAINT DOCUMENTO_pk PRIMARY KEY (id_documento),
    CONSTRAINT DOCUMENTO_numero_uq UNIQUE (id_tipo_documento, numero),
    CONSTRAINT DOCUMENTO_numero_texto_ck CHECK (REGEXP_LIKE(numero, '[^[:space:]]'))
);

CREATE TABLE PACIENTE (
    id_persona NUMBER,
    estado NUMBER(1) DEFAULT 1 NOT NULL CHECK (estado IN (0,1)),
    fecha_registro DATE DEFAULT SYSDATE NOT NULL,
    CONSTRAINT PACIENTE_pk PRIMARY KEY (id_persona)
);

CREATE TABLE USUARIO (
    id_usuario NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    password VARCHAR2(500) NOT NULL,
    correo VARCHAR2(200) NOT NULL UNIQUE,
    estado NUMBER(1) DEFAULT 1 NOT NULL CHECK (estado IN (0,1)),
    CONSTRAINT USUARIO_pk PRIMARY KEY (id_usuario),
    CONSTRAINT USUARIO_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]')),
    CONSTRAINT USUARIO_password_texto_ck CHECK (REGEXP_LIKE(password, '[^[:space:]]')),
    CONSTRAINT USUARIO_correo_texto_ck CHECK (REGEXP_LIKE(correo, '[^[:space:]]'))
);

CREATE TABLE EMPLEADO (
    id_persona NUMBER,
    estado NUMBER(1) DEFAULT 1 NOT NULL CHECK (estado IN (0,1)),
    fecha_contrato DATE DEFAULT SYSDATE NOT NULL,
    id_usuario NUMBER NOT NULL UNIQUE,
    id_cargo NUMBER NOT NULL,
    CONSTRAINT EMPLEADO_pk PRIMARY KEY (id_persona)
);

CREATE TABLE USUARIO_ROL (
    id_usuario_rol NUMBER GENERATED ALWAYS AS IDENTITY,
    id_usuario NUMBER NOT NULL,
    id_rol NUMBER NOT NULL,
    CONSTRAINT USUARIO_ROL_pk PRIMARY KEY (id_usuario_rol),
    CONSTRAINT USUARIO_ROL_par_uq UNIQUE (id_usuario, id_rol)
);

CREATE TABLE ROL_PERMISO (
    id_rol_permiso NUMBER GENERATED ALWAYS AS IDENTITY,
    id_rol NUMBER NOT NULL,
    id_permiso NUMBER NOT NULL,
    CONSTRAINT ROL_PERMISO_pk PRIMARY KEY (id_rol_permiso),
    CONSTRAINT ROL_PERMISO_par_uq UNIQUE (id_rol, id_permiso)
);

CREATE TABLE ITEM (
    id_item NUMBER GENERATED ALWAYS AS IDENTITY,
    codigo VARCHAR2(100) NOT NULL UNIQUE,
    nombre VARCHAR2(200) NOT NULL,
    descripcion VARCHAR2(1000),
    precio NUMBER(14,2) NOT NULL CHECK (precio >= 0),
    tipo VARCHAR2(20) NOT NULL CHECK (tipo IN ('PRODUCTO','MONTURA','CRISTAL')),
    unidad_venta VARCHAR2(10) NOT NULL CHECK (unidad_venta IN ('UNIDAD','PAR')),
    controla_stock NUMBER(1) DEFAULT 1 NOT NULL CHECK (controla_stock IN (0,1)),
    estado NUMBER(1) DEFAULT 1 NOT NULL CHECK (estado IN (0,1)),
    fecha_registro DATE DEFAULT SYSDATE NOT NULL,
    stock NUMBER(10) DEFAULT 0 NOT NULL CHECK (stock >= 0),
    id_categoria NUMBER NOT NULL,
    id_marca NUMBER,
    CONSTRAINT ITEM_pk PRIMARY KEY (id_item),
    CONSTRAINT ITEM_cristal_stock_ck CHECK (tipo <> 'CRISTAL' OR controla_stock = 0),
    CONSTRAINT ITEM_tipo_uq UNIQUE (id_item, tipo),
    CONSTRAINT ITEM_stock_cristal_ck CHECK (controla_stock = 1 OR stock = 0),
    CONSTRAINT ITEM_codigo_texto_ck CHECK (REGEXP_LIKE(codigo, '[^[:space:]]')),
    CONSTRAINT ITEM_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]')),
    CONSTRAINT ITEM_tipo_texto_ck CHECK (REGEXP_LIKE(tipo, '[^[:space:]]')),
    CONSTRAINT ITEM_unidad_venta_texto_ck CHECK (REGEXP_LIKE(unidad_venta, '[^[:space:]]'))
);

CREATE TABLE CRISTAL (
    id_item NUMBER,
    tipo VARCHAR2(20) DEFAULT 'CRISTAL' NOT NULL CHECK (tipo = 'CRISTAL'),
    indice_refraccion NUMBER(5,3),
    id_material_cristal NUMBER NOT NULL,
    CONSTRAINT CRISTAL_pk PRIMARY KEY (id_item),
    CONSTRAINT CRISTAL_tipo_texto_ck CHECK (REGEXP_LIKE(tipo, '[^[:space:]]')),
    CONSTRAINT CRISTAL_indice_ck CHECK (indice_refraccion > 1)
);

CREATE TABLE MONTURA (
    id_item NUMBER,
    tipo VARCHAR2(20) DEFAULT 'MONTURA' NOT NULL CHECK (tipo = 'MONTURA'),
    color VARCHAR2(100),
    medidas VARCHAR2(200),
    id_modelo_montura NUMBER,
    id_material_montura NUMBER,
    CONSTRAINT MONTURA_pk PRIMARY KEY (id_item),
    CONSTRAINT MONTURA_tipo_texto_ck CHECK (REGEXP_LIKE(tipo, '[^[:space:]]'))
);

CREATE TABLE CRISTAL_TRATAMIENTO (
    id_cristal_tratamiento NUMBER GENERATED ALWAYS AS IDENTITY,
    id_cristal NUMBER NOT NULL,
    id_tratamiento NUMBER NOT NULL,
    CONSTRAINT CRISTAL_TRATAMIENTO_pk PRIMARY KEY (id_cristal_tratamiento),
    CONSTRAINT CRISTAL_TRAT_par_uq UNIQUE (id_cristal,id_tratamiento)
);

CREATE TABLE EXAMEN (
    id_examen NUMBER GENERATED ALWAYS AS IDENTITY,
    fecha_registro DATE DEFAULT SYSDATE NOT NULL,
    es_externo NUMBER(1) DEFAULT 0 NOT NULL CHECK (es_externo IN (0,1)),
    profesional_externo VARCHAR2(200),
    documento_externo VARCHAR2(1000),
    observaciones VARCHAR2(2000),
    id_paciente NUMBER NOT NULL,
    id_empleado_registro NUMBER NOT NULL,
    CONSTRAINT EXAMEN_pk PRIMARY KEY (id_examen)
);

CREATE TABLE MEDIDA (
    id_medida NUMBER GENERATED ALWAYS AS IDENTITY,
    observaciones VARCHAR2(1000),
    id_examen NUMBER NOT NULL,
    id_tipo_medida NUMBER NOT NULL,
    CONSTRAINT MEDIDA_pk PRIMARY KEY (id_medida),
    CONSTRAINT MEDIDA_tipo_uq UNIQUE (id_examen,id_tipo_medida),
    CONSTRAINT MEDIDA_examen_uq UNIQUE (id_medida,id_examen)
);

CREATE TABLE MEDIDA_OJO (
    id_medida_ojo NUMBER GENERATED ALWAYS AS IDENTITY,
    agudeza_visual NUMBER, -- escala decimal acordada; sin rango no autorizado
    esfera NUMBER(7,2),
    cilindro NUMBER(7,2),
    eje NUMBER(6,2) CHECK (eje BETWEEN 0 AND 180),
    adicion NUMBER(7,2),
    distancia_pupilar_mm NUMBER(6,2) CHECK (distancia_pupilar_mm > 0),
    prisma NUMBER(7,2) CHECK (prisma >= 0),
    base_prisma VARCHAR2(100),
    id_medida NUMBER NOT NULL,
    id_tipo_ojo NUMBER NOT NULL,
    CONSTRAINT MEDIDA_OJO_pk PRIMARY KEY (id_medida_ojo),
    CONSTRAINT MEDIDA_OJO_par_uq UNIQUE (id_medida,id_tipo_ojo)
);

CREATE TABLE RECETA (
    id_receta NUMBER GENERATED ALWAYS AS IDENTITY,
    fecha_registro DATE DEFAULT SYSDATE NOT NULL,
    observaciones VARCHAR2(2000),
    fecha_de_entrega DATE,
    es_externo NUMBER(1) DEFAULT 0 NOT NULL CHECK (es_externo IN (0,1)),
    id_examen NUMBER NOT NULL UNIQUE,
    id_empleado_registro NUMBER NOT NULL,
    CONSTRAINT RECETA_pk PRIMARY KEY (id_receta),
    CONSTRAINT RECETA_examen_uq UNIQUE (id_receta,id_examen),
    CONSTRAINT RECETA_entrega_fecha_ck CHECK (fecha_de_entrega IS NULL OR fecha_de_entrega >= fecha_registro)
);

CREATE TABLE LENTE (
    id_lente NUMBER GENERATED ALWAYS AS IDENTITY,
    descripcion_montura_propia VARCHAR2(1000),
    observaciones VARCHAR2(1000),
    sub_total NUMBER(14,2) DEFAULT 0 NOT NULL CHECK (sub_total >= 0),
    id_receta NUMBER NOT NULL,
    id_diseno_lente NUMBER NOT NULL,
    id_montura NUMBER,
    id_cristal NUMBER NOT NULL,
    CONSTRAINT LENTE_pk PRIMARY KEY (id_lente),
    CONSTRAINT LENTE_montura_ck CHECK (id_montura IS NULL OR descripcion_montura_propia IS NULL),
    CONSTRAINT LENTE_receta_uq UNIQUE (id_lente,id_receta)
);

CREATE TABLE LENTE_MEDIDA (
    id_lente_medida NUMBER GENERATED ALWAYS AS IDENTITY,
    id_lente NUMBER NOT NULL,
    id_receta NUMBER NOT NULL,
    id_examen NUMBER NOT NULL,
    id_medida NUMBER NOT NULL,
    CONSTRAINT LENTE_MEDIDA_pk PRIMARY KEY (id_lente_medida),
    CONSTRAINT LENTE_MEDIDA_par_uq UNIQUE (id_lente,id_medida)
);

CREATE TABLE VENTA (
    id_venta NUMBER GENERATED ALWAYS AS IDENTITY,
    fecha_registro DATE DEFAULT SYSDATE NOT NULL,
    moneda CHAR(3) DEFAULT 'PEN' NOT NULL,
    observaciones VARCHAR2(1000),
    total NUMBER(14,2) NOT NULL CHECK (total >= 0),
    id_paciente NUMBER,
    id_comprador NUMBER,
    id_empleado NUMBER NOT NULL,
    id_receta NUMBER,
    id_estado_venta NUMBER NOT NULL,
    CONSTRAINT VENTA_pk PRIMARY KEY (id_venta),
    CONSTRAINT VENTA_moneda_ck CHECK (REGEXP_LIKE(moneda, '^[A-Z]{3}$', 'c'))
);

CREATE TABLE ITEM_VENDIDO (
    id_item_vendido NUMBER GENERATED ALWAYS AS IDENTITY,
    descripcion VARCHAR2(1000),
    cantidad NUMBER(10) NOT NULL CHECK (cantidad > 0),
    precio_unit NUMBER(14,2) CHECK (precio_unit >= 0),
    descuento NUMBER(14,2) DEFAULT 0 NOT NULL CHECK (descuento >= 0),
    sub_total NUMBER(14,2) DEFAULT 0 NOT NULL CHECK (sub_total >= 0),
    id_venta NUMBER NOT NULL,
    id_item NUMBER NOT NULL,
    id_lente NUMBER,
    CONSTRAINT ITEM_VENDIDO_pk PRIMARY KEY (id_item_vendido),
    CONSTRAINT ITEM_VENDIDO_desc_ck CHECK (descuento <= cantidad * precio_unit),
    CONSTRAINT ITEM_VENDIDO_item_uq UNIQUE (id_item_vendido,id_item)
);

CREATE TABLE SERVICIO (
    id_servicio NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    descripcion VARCHAR2(1000),
    precio NUMBER(14,2) NOT NULL CHECK (precio >= 0),
    estado NUMBER(1) DEFAULT 1 NOT NULL CHECK (estado IN (0,1)),
    CONSTRAINT SERVICIO_pk PRIMARY KEY (id_servicio),
    CONSTRAINT SERVICIO_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]'))
);

CREATE TABLE SERVICIO_VENDIDO (
    id_servicio_vendido NUMBER GENERATED ALWAYS AS IDENTITY,
    descripcion VARCHAR2(1000),
    cantidad NUMBER(10) DEFAULT 1 NOT NULL CHECK (cantidad > 0),
    precio NUMBER(14,2) NOT NULL CHECK (precio >= 0),
    sub_total NUMBER(14,2) GENERATED ALWAYS AS (cantidad * precio) VIRTUAL,
    id_venta NUMBER NOT NULL,
    id_servicio NUMBER NOT NULL,
    CONSTRAINT SERVICIO_VENDIDO_pk PRIMARY KEY (id_servicio_vendido)
);

CREATE TABLE COMPROBANTE (
    id_comprobante NUMBER GENERATED ALWAYS AS IDENTITY,
    serie VARCHAR2(20) NOT NULL,
    numero VARCHAR2(30) NOT NULL,
    fecha_emision DATE DEFAULT SYSDATE NOT NULL,
    total_emitido NUMBER(14,2) NOT NULL CHECK (total_emitido >= 0),
    estado_documento VARCHAR2(10) DEFAULT 'EMITIDO' NOT NULL CHECK (estado_documento IN ('EMITIDO','ANULADO')),
    fecha_anulacion DATE,
    motivo_anulacion VARCHAR2(1000),
    estado_cobro VARCHAR2(15) DEFAULT 'PENDIENTE' NOT NULL CHECK (estado_cobro IN ('PENDIENTE','CON_ADELANTO','CANCELADO')),
    fecha_cancelacion DATE,
    id_venta NUMBER NOT NULL UNIQUE,
    id_tipo_comprobante NUMBER NOT NULL,
    id_estado_comprobante NUMBER NOT NULL,
    CONSTRAINT COMPROBANTE_pk PRIMARY KEY (id_comprobante),
    CONSTRAINT COMPROBANTE_numero_uq UNIQUE (id_tipo_comprobante, serie, numero),
    CONSTRAINT COMPROBANTE_anula_ck CHECK (
        (estado_documento = 'EMITIDO' AND fecha_anulacion IS NULL)
        OR (estado_documento = 'ANULADO' AND fecha_anulacion IS NOT NULL AND motivo_anulacion IS NOT NULL)
    ),
    CONSTRAINT COMP_cancelacion_fecha_ck CHECK (fecha_cancelacion IS NULL OR fecha_cancelacion >= fecha_emision),
    CONSTRAINT COMP_cancelacion_estado_ck CHECK (
        (estado_cobro = 'CANCELADO' AND fecha_cancelacion IS NOT NULL)
        OR (estado_cobro IN ('PENDIENTE','CON_ADELANTO') AND fecha_cancelacion IS NULL)
    ),
    CONSTRAINT COMPROBANTE_serie_texto_ck CHECK (REGEXP_LIKE(serie, '[^[:space:]]')),
    CONSTRAINT COMPROBANTE_numero_texto_ck CHECK (REGEXP_LIKE(numero, '[^[:space:]]')),
    CONSTRAINT COMPROBANTE_estado_documento_texto_ck CHECK (REGEXP_LIKE(estado_documento, '[^[:space:]]')),
    CONSTRAINT COMPROBANTE_estado_cobro_texto_ck CHECK (REGEXP_LIKE(estado_cobro, '[^[:space:]]')),
    CONSTRAINT COMP_fecha_anulacion_ck CHECK (fecha_anulacion IS NULL OR fecha_anulacion >= fecha_emision),
    CONSTRAINT COMP_motivo_texto_ck CHECK (motivo_anulacion IS NULL OR REGEXP_LIKE(motivo_anulacion, '[^[:space:]]'))
);

CREATE TABLE PAGO (
    id_pago NUMBER GENERATED ALWAYS AS IDENTITY,
    fecha_registro DATE DEFAULT SYSDATE NOT NULL,
    numero_operacion VARCHAR2(200),
    monto NUMBER(14,2) NOT NULL CHECK (monto > 0),
    anulado NUMBER(1) DEFAULT 0 NOT NULL CHECK (anulado IN (0,1)),
    motivo_anulacion VARCHAR2(1000),
    id_comprobante NUMBER NOT NULL,
    id_metodo_pago NUMBER NOT NULL,
    id_empleado NUMBER NOT NULL,
    CONSTRAINT PAGO_pk PRIMARY KEY (id_pago),
    CONSTRAINT PAGO_anula_ck CHECK (anulado = 0 OR motivo_anulacion IS NOT NULL),
    CONSTRAINT PAGO_motivo_texto_ck CHECK (motivo_anulacion IS NULL OR REGEXP_LIKE(motivo_anulacion, '[^[:space:]]'))
);

CREATE TABLE DEVOLUCION_PAGO (
    id_devolucion_pago NUMBER GENERATED ALWAYS AS IDENTITY,
    fecha_registro DATE DEFAULT SYSDATE NOT NULL,
    monto NUMBER(14,2) NOT NULL CHECK (monto > 0),
    motivo VARCHAR2(1000) NOT NULL,
    referencia_operacion VARCHAR2(200),
    id_pago NUMBER NOT NULL,
    id_empleado NUMBER NOT NULL,
    CONSTRAINT DEVOLUCION_PAGO_pk PRIMARY KEY (id_devolucion_pago),
    CONSTRAINT DEVOLUCION_PAGO_motivo_texto_ck CHECK (REGEXP_LIKE(motivo, '[^[:space:]]'))
);

CREATE TABLE MOVIMIENTO_INVENTARIO (
    id_movimiento_inventario NUMBER GENERATED ALWAYS AS IDENTITY,
    fecha_registro DATE DEFAULT SYSDATE NOT NULL,
    cantidad NUMBER(10) NOT NULL CHECK (cantidad <> 0),
    motivo VARCHAR2(20) NOT NULL CHECK (motivo IN ('INICIAL','COMPRA','VENTA','DEVOLUCION','ROTURA','AJUSTE')),
    observaciones VARCHAR2(1000),
    id_item NUMBER NOT NULL,
    id_item_vendido NUMBER,
    id_empleado NUMBER NOT NULL,
    CONSTRAINT MOVIMIENTO_INVENTARIO_pk PRIMARY KEY (id_movimiento_inventario),
    CONSTRAINT MOV_venta_ck CHECK (motivo <> 'VENTA' OR (cantidad < 0 AND id_item_vendido IS NOT NULL)),
    CONSTRAINT MOV_entrada_ck CHECK (motivo NOT IN ('INICIAL','COMPRA','DEVOLUCION') OR cantidad > 0),
    CONSTRAINT MOV_rotura_ck CHECK (motivo <> 'ROTURA' OR cantidad < 0),
    CONSTRAINT MOVIMIENTO_INVENTARIO_motivo_texto_ck CHECK (REGEXP_LIKE(motivo, '[^[:space:]]'))
);

CREATE TABLE RESERVA_INVENTARIO (
    id_reserva_inventario NUMBER GENERATED ALWAYS AS IDENTITY,
    fecha_registro DATE DEFAULT SYSDATE NOT NULL,
    cantidad NUMBER(10) NOT NULL CHECK (cantidad > 0),
    estado VARCHAR2(10) DEFAULT 'ACTIVA' NOT NULL CHECK (estado IN ('ACTIVA','LIBERADA','CONSUMIDA')),
    id_item_vendido NUMBER NOT NULL,
    id_item NUMBER NOT NULL,
    CONSTRAINT RESERVA_INVENTARIO_pk PRIMARY KEY (id_reserva_inventario),
    CONSTRAINT RESERVA_INVENTARIO_estado_texto_ck CHECK (REGEXP_LIKE(estado, '[^[:space:]]'))
);

CREATE TABLE ORDEN_TRABAJO (
    id_orden_trabajo NUMBER GENERATED ALWAYS AS IDENTITY,
    numero_orden VARCHAR2(50) NOT NULL UNIQUE,
    fecha_registro DATE DEFAULT SYSDATE NOT NULL,
    fecha_entrega_estimada DATE,
    fecha_entrega_paciente DATE,
    fecha_notificacion DATE,
    es_urgente NUMBER(1) DEFAULT 0 NOT NULL CHECK (es_urgente IN (0,1)),
    descripcion VARCHAR2(2000),
    fecha_entrega_real DATE,
    id_lente NUMBER NOT NULL UNIQUE,
    id_empleado NUMBER NOT NULL,
    id_estado_orden_trabajo NUMBER NOT NULL,
    CONSTRAINT ORDEN_TRABAJO_pk PRIMARY KEY (id_orden_trabajo),
    CONSTRAINT OT_entrega_real_fecha_ck CHECK (fecha_entrega_real IS NULL OR fecha_entrega_real >= fecha_registro),
    CONSTRAINT ORDEN_TRABAJO_numero_orden_texto_ck CHECK (REGEXP_LIKE(numero_orden, '[^[:space:]]')),
    CONSTRAINT OT_fecha_entrega_estimada_ck CHECK (fecha_entrega_estimada IS NULL OR fecha_entrega_estimada >= fecha_registro),
    CONSTRAINT OT_fecha_entrega_paciente_ck CHECK (fecha_entrega_paciente IS NULL OR fecha_entrega_paciente >= fecha_registro),
    CONSTRAINT OT_fecha_notificacion_ck CHECK (fecha_notificacion IS NULL OR fecha_notificacion >= fecha_registro)
);

CREATE TABLE LABORATORIO (
    id_laboratorio NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL,
    ruc VARCHAR2(20) NOT NULL UNIQUE,
    telefono VARCHAR2(50),
    direccion VARCHAR2(500),
    correo VARCHAR2(200),
    estado NUMBER(1) DEFAULT 1 NOT NULL CHECK (estado IN (0,1)),
    CONSTRAINT LABORATORIO_pk PRIMARY KEY (id_laboratorio),
    CONSTRAINT LABORATORIO_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]')),
    CONSTRAINT LABORATORIO_ruc_texto_ck CHECK (REGEXP_LIKE(ruc, '[^[:space:]]'))
);

CREATE TABLE ENVIO_LABORATORIO (
    id_envio_laboratorio NUMBER GENERATED ALWAYS AS IDENTITY,
    fecha_envio DATE DEFAULT SYSDATE NOT NULL,
    fecha_recepcion DATE,
    motivo VARCHAR2(20) DEFAULT 'FABRICACION' NOT NULL CHECK (motivo IN ('FABRICACION','CORRECCION')),
    descripcion VARCHAR2(1000),
    costo_optica NUMBER(14,2) DEFAULT 0 NOT NULL CHECK (costo_optica >= 0),
    id_orden_trabajo NUMBER NOT NULL,
    id_laboratorio NUMBER NOT NULL,
    CONSTRAINT ENVIO_LABORATORIO_pk PRIMARY KEY (id_envio_laboratorio),
    CONSTRAINT ENVIO_fechas_ck CHECK (fecha_recepcion IS NULL OR fecha_recepcion >= fecha_envio),
    CONSTRAINT ENVIO_LABORATORIO_motivo_texto_ck CHECK (REGEXP_LIKE(motivo, '[^[:space:]]'))
);

CREATE TABLE CONTROL_CALIDAD (
    id_control_calidad NUMBER GENERATED ALWAYS AS IDENTITY,
    fecha_registro DATE DEFAULT SYSDATE NOT NULL,
    estado VARCHAR2(15) DEFAULT 'EN_REVISION' NOT NULL CHECK (estado IN ('EN_REVISION','APROBADO','OBSERVADO')),
    observaciones VARCHAR2(2000),
    aprobado NUMBER(1) NOT NULL CHECK (aprobado IN (0,1)),
    id_envio_laboratorio NUMBER NOT NULL,
    id_empleado NUMBER NOT NULL,
    CONSTRAINT CONTROL_CALIDAD_pk PRIMARY KEY (id_control_calidad),
    CONSTRAINT CC_aprobado_estado_ck CHECK (
        (estado = 'APROBADO' AND aprobado = 1)
        OR (estado IN ('EN_REVISION','OBSERVADO') AND aprobado = 0)
    ),
    CONSTRAINT CONTROL_CALIDAD_estado_texto_ck CHECK (REGEXP_LIKE(estado, '[^[:space:]]'))
);

CREATE TABLE TIPO_PARAMETRO_CALIDAD (
    id_tipo_parametro_calidad NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    unidad VARCHAR2(50) NOT NULL,
    tolerancia NUMBER(10,4) CHECK (tolerancia >= 0),
    periodo NUMBER(10,4) CHECK (periodo > 0),
    valor_esperado NUMBER(10,4) NOT NULL,
    CONSTRAINT TIPO_PARAMETRO_CALIDAD_pk PRIMARY KEY (id_tipo_parametro_calidad),
    CONSTRAINT TIPO_PARAMETRO_CALIDAD_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]')),
    CONSTRAINT TIPO_PARAMETRO_CALIDAD_unidad_texto_ck CHECK (REGEXP_LIKE(unidad, '[^[:space:]]')),
    CONSTRAINT TIPO_PC_tolerancia_periodo_ck CHECK (
        periodo IS NULL OR tolerancia IS NULL OR tolerancia <= periodo / 2
    )
);

CREATE TABLE PARAMETRO_CALIDAD (
    id_parametro_calidad NUMBER GENERATED ALWAYS AS IDENTITY,
    tolerancia NUMBER(10,4) NOT NULL CHECK (tolerancia >= 0),
    valor_esperado NUMBER(10,4) NOT NULL,
    valor_obtenido NUMBER(10,4) NOT NULL,
    periodo NUMBER(10,4) CHECK (periodo > 0),
    aprobado NUMBER(1) NOT NULL CHECK (aprobado IN (0,1)),
    id_control_calidad NUMBER NOT NULL,
    id_tipo_parametro_calidad NUMBER NOT NULL,
    id_medida_ojo NUMBER NOT NULL,
    CONSTRAINT PARAMETRO_CALIDAD_pk PRIMARY KEY (id_parametro_calidad),
    CONSTRAINT PARAM_CALIDAD_par_uq UNIQUE (id_control_calidad,id_tipo_parametro_calidad,id_medida_ojo),
    CONSTRAINT PC_aprobado_valores_ck CHECK (
        aprobado = CASE WHEN
            (CASE WHEN periodo IS NULL THEN ABS(valor_obtenido - valor_esperado)
              ELSE LEAST(
                MOD(ABS(valor_obtenido - valor_esperado), periodo),
                periodo - MOD(ABS(valor_obtenido - valor_esperado), periodo)
              )
            END) <= tolerancia
            THEN 1 ELSE 0 END
    ),
    CONSTRAINT PC_tolerancia_periodo_ck CHECK (periodo IS NULL OR tolerancia <= periodo / 2)
);

CREATE TABLE AUDITORIA (
    id_auditoria NUMBER GENERATED ALWAYS AS IDENTITY,
    fecha_log DATE DEFAULT SYSDATE NOT NULL,
    entidad VARCHAR2(100) NOT NULL,
    id_entidad NUMBER NOT NULL,
    accion VARCHAR2(100) NOT NULL,
    datos_anteriores CLOB,
    datos_nuevos CLOB,
    id_usuario NUMBER NOT NULL,
    CONSTRAINT AUDITORIA_pk PRIMARY KEY (id_auditoria),
    CONSTRAINT AUDITORIA_entidad_texto_ck CHECK (REGEXP_LIKE(entidad, '[^[:space:]]')),
    CONSTRAINT AUDITORIA_accion_texto_ck CHECK (REGEXP_LIKE(accion, '[^[:space:]]')),
    CONSTRAINT AUDITORIA_entidad_id_ck CHECK (id_entidad > 0 AND id_entidad = TRUNC(id_entidad))
);

CREATE TABLE CONFIGURACION_EMPRESA (
    id_configuracion_empresa NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre_legal VARCHAR2(200) NOT NULL,
    nombre_comercial VARCHAR2(200),
    ruc VARCHAR2(20) NOT NULL UNIQUE,
    direccion VARCHAR2(500),
    telefono VARCHAR2(50),
    correo VARCHAR2(200),
    logo VARCHAR2(1000),
    CONSTRAINT CONFIGURACION_EMPRESA_pk PRIMARY KEY (id_configuracion_empresa),
    CONSTRAINT CONFIGURACION_EMPRESA_nombre_legal_texto_ck CHECK (REGEXP_LIKE(nombre_legal, '[^[:space:]]')),
    CONSTRAINT CONFIGURACION_EMPRESA_ruc_texto_ck CHECK (REGEXP_LIKE(ruc, '[^[:space:]]'))
);

CREATE TABLE TIPO_PARAMETRO_SISTEMA (
    id_tipo_parametro_sistema NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    CONSTRAINT TIPO_PARAMETRO_SISTEMA_pk PRIMARY KEY (id_tipo_parametro_sistema),
    CONSTRAINT TIPO_PARAMETRO_SISTEMA_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]'))
);

CREATE TABLE PARAMETRO_SISTEMA (
    id_parametro_sistema NUMBER GENERATED ALWAYS AS IDENTITY,
    clave VARCHAR2(200) NOT NULL UNIQUE,
    valor VARCHAR2(2000),
    id_tipo_parametro_sistema NUMBER NOT NULL,
    CONSTRAINT PARAMETRO_SISTEMA_pk PRIMARY KEY (id_parametro_sistema),
    CONSTRAINT PARAMETRO_SISTEMA_clave_texto_ck CHECK (REGEXP_LIKE(clave, '[^[:space:]]'))
);

CREATE TABLE ESTADO_COMPROBANTE (
    id_estado_comprobante NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(200) NOT NULL UNIQUE,
    CONSTRAINT ESTADO_COMPROBANTE_pk PRIMARY KEY (id_estado_comprobante),
    CONSTRAINT ESTADO_COMPROBANTE_nombre_texto_ck CHECK (REGEXP_LIKE(nombre, '[^[:space:]]'))
);

CREATE TABLE PACIENTE_LOG (
    id_paciente_log NUMBER GENERATED ALWAYS AS IDENTITY,
    fecha_log DATE DEFAULT SYSDATE NOT NULL,
    id_usuario NUMBER NOT NULL,
    id_persona NUMBER NOT NULL,
    CONSTRAINT PACIENTE_LOG_pk PRIMARY KEY (id_paciente_log)
);

CREATE TABLE EMPLEADO_LOG (
    id_empleado_log NUMBER GENERATED ALWAYS AS IDENTITY,
    fecha_log DATE DEFAULT SYSDATE NOT NULL,
    id_usuario NUMBER NOT NULL,
    id_persona NUMBER NOT NULL,
    CONSTRAINT EMPLEADO_LOG_pk PRIMARY KEY (id_empleado_log)
);

/* ==================================================
   FASE 2: CLAVES FORÁNEAS (UNA POR ALTER TABLE)
   ================================================== */

-- Relaciones de PERMISO
ALTER TABLE PERMISO
    ADD CONSTRAINT PERMISO_id_modulo_fk
    FOREIGN KEY (id_modulo) REFERENCES MODULO (id_modulo);

-- Relaciones de DOCUMENTO
ALTER TABLE DOCUMENTO
    ADD CONSTRAINT DOCUMENTO_id_persona_fk
    FOREIGN KEY (id_persona) REFERENCES PERSONA (id_persona);
ALTER TABLE DOCUMENTO
    ADD CONSTRAINT DOCUMENTO_id_tipo_documento_fk
    FOREIGN KEY (id_tipo_documento) REFERENCES TIPO_DOCUMENTO (id_tipo_documento);

-- Relaciones de PACIENTE
ALTER TABLE PACIENTE
    ADD CONSTRAINT PACIENTE_base_fk
    FOREIGN KEY (id_persona) REFERENCES PERSONA (id_persona);

-- Relaciones de USUARIO
ALTER TABLE USUARIO
    ADD CONSTRAINT USUARIO_empleado_fk
    FOREIGN KEY (id_usuario) REFERENCES EMPLEADO (id_usuario) DEFERRABLE INITIALLY DEFERRED;

-- Relaciones de EMPLEADO
ALTER TABLE EMPLEADO
    ADD CONSTRAINT EMPLEADO_base_fk
    FOREIGN KEY (id_persona) REFERENCES PERSONA (id_persona);
ALTER TABLE EMPLEADO
    ADD CONSTRAINT EMPLEADO_id_usuario_fk
    FOREIGN KEY (id_usuario) REFERENCES USUARIO (id_usuario);
ALTER TABLE EMPLEADO
    ADD CONSTRAINT EMPLEADO_id_cargo_fk
    FOREIGN KEY (id_cargo) REFERENCES CARGO (id_cargo);

-- Relaciones de USUARIO_ROL
ALTER TABLE USUARIO_ROL
    ADD CONSTRAINT USUARIO_ROL_id_usuario_fk
    FOREIGN KEY (id_usuario) REFERENCES USUARIO (id_usuario);
ALTER TABLE USUARIO_ROL
    ADD CONSTRAINT USUARIO_ROL_id_rol_fk
    FOREIGN KEY (id_rol) REFERENCES ROL (id_rol);

-- Relaciones de ROL_PERMISO
ALTER TABLE ROL_PERMISO
    ADD CONSTRAINT ROL_PERMISO_id_rol_fk
    FOREIGN KEY (id_rol) REFERENCES ROL (id_rol);
ALTER TABLE ROL_PERMISO
    ADD CONSTRAINT ROL_PERMISO_id_permiso_fk
    FOREIGN KEY (id_permiso) REFERENCES PERMISO (id_permiso);

-- Relaciones de ITEM
ALTER TABLE ITEM
    ADD CONSTRAINT ITEM_id_categoria_fk
    FOREIGN KEY (id_categoria) REFERENCES CATEGORIA (id_categoria);
ALTER TABLE ITEM
    ADD CONSTRAINT ITEM_id_marca_fk
    FOREIGN KEY (id_marca) REFERENCES MARCA (id_marca);

-- Relaciones de CRISTAL
ALTER TABLE CRISTAL
    ADD CONSTRAINT CRISTAL_base_fk
    FOREIGN KEY (id_item) REFERENCES ITEM (id_item);
ALTER TABLE CRISTAL
    ADD CONSTRAINT CRISTAL_id_material_cristal_fk
    FOREIGN KEY (id_material_cristal) REFERENCES MATERIAL_CRISTAL (id_material_cristal);
ALTER TABLE CRISTAL
    ADD CONSTRAINT CRISTAL_tipo_fk
    FOREIGN KEY (id_item, tipo) REFERENCES ITEM (id_item, tipo);

-- Relaciones de MONTURA
ALTER TABLE MONTURA
    ADD CONSTRAINT MONTURA_base_fk
    FOREIGN KEY (id_item) REFERENCES ITEM (id_item);
ALTER TABLE MONTURA
    ADD CONSTRAINT MONTURA_tipo_fk
    FOREIGN KEY (id_item, tipo) REFERENCES ITEM (id_item, tipo);
ALTER TABLE MONTURA
    ADD CONSTRAINT MONTURA_id_modelo_montura_fk
    FOREIGN KEY (id_modelo_montura) REFERENCES MODELO_MONTURA (id_modelo_montura);
ALTER TABLE MONTURA
    ADD CONSTRAINT MONTURA_id_material_montura_fk
    FOREIGN KEY (id_material_montura) REFERENCES MATERIAL_MONTURA (id_material_montura);

-- Relaciones de CRISTAL_TRATAMIENTO
ALTER TABLE CRISTAL_TRATAMIENTO
    ADD CONSTRAINT CRISTAL_TRATAMIENTO_id_cristal_fk
    FOREIGN KEY (id_cristal) REFERENCES CRISTAL (id_item);
ALTER TABLE CRISTAL_TRATAMIENTO
    ADD CONSTRAINT CRISTAL_TRATAMIENTO_id_tratamiento_fk
    FOREIGN KEY (id_tratamiento) REFERENCES TRATAMIENTO_CRISTAL (id_tratamiento_cristal);

-- Relaciones de EXAMEN
ALTER TABLE EXAMEN
    ADD CONSTRAINT EXAMEN_id_paciente_fk
    FOREIGN KEY (id_paciente) REFERENCES PACIENTE (id_persona);
ALTER TABLE EXAMEN
    ADD CONSTRAINT EXAMEN_id_empleado_registro_fk
    FOREIGN KEY (id_empleado_registro) REFERENCES EMPLEADO (id_persona);

-- Relaciones de MEDIDA
ALTER TABLE MEDIDA
    ADD CONSTRAINT MEDIDA_id_examen_fk
    FOREIGN KEY (id_examen) REFERENCES EXAMEN (id_examen);
ALTER TABLE MEDIDA
    ADD CONSTRAINT MEDIDA_id_tipo_medida_fk
    FOREIGN KEY (id_tipo_medida) REFERENCES TIPO_MEDIDA (id_tipo_medida);

-- Relaciones de MEDIDA_OJO
ALTER TABLE MEDIDA_OJO
    ADD CONSTRAINT MEDIDA_OJO_id_medida_fk
    FOREIGN KEY (id_medida) REFERENCES MEDIDA (id_medida);
ALTER TABLE MEDIDA_OJO
    ADD CONSTRAINT MEDIDA_OJO_id_tipo_ojo_fk
    FOREIGN KEY (id_tipo_ojo) REFERENCES TIPO_OJO (id_tipo_ojo);

-- Relaciones de RECETA
ALTER TABLE RECETA
    ADD CONSTRAINT RECETA_id_examen_fk
    FOREIGN KEY (id_examen) REFERENCES EXAMEN (id_examen);
ALTER TABLE RECETA
    ADD CONSTRAINT RECETA_id_empleado_registro_fk
    FOREIGN KEY (id_empleado_registro) REFERENCES EMPLEADO (id_persona);

-- Relaciones de LENTE
ALTER TABLE LENTE
    ADD CONSTRAINT LENTE_id_receta_fk
    FOREIGN KEY (id_receta) REFERENCES RECETA (id_receta);
ALTER TABLE LENTE
    ADD CONSTRAINT LENTE_id_diseno_lente_fk
    FOREIGN KEY (id_diseno_lente) REFERENCES DISENO_LENTE (id_diseno_lente);
ALTER TABLE LENTE
    ADD CONSTRAINT LENTE_id_montura_fk
    FOREIGN KEY (id_montura) REFERENCES MONTURA (id_item);
ALTER TABLE LENTE
    ADD CONSTRAINT LENTE_id_cristal_fk
    FOREIGN KEY (id_cristal) REFERENCES CRISTAL (id_item);

-- Relaciones de LENTE_MEDIDA
ALTER TABLE LENTE_MEDIDA
    ADD CONSTRAINT LM_lente_fk
    FOREIGN KEY (id_lente, id_receta) REFERENCES LENTE (id_lente, id_receta);
ALTER TABLE LENTE_MEDIDA
    ADD CONSTRAINT LM_receta_fk
    FOREIGN KEY (id_receta, id_examen) REFERENCES RECETA (id_receta, id_examen);
ALTER TABLE LENTE_MEDIDA
    ADD CONSTRAINT LM_medida_fk
    FOREIGN KEY (id_medida, id_examen) REFERENCES MEDIDA (id_medida, id_examen);

-- Relaciones de VENTA
ALTER TABLE VENTA
    ADD CONSTRAINT VENTA_id_paciente_fk
    FOREIGN KEY (id_paciente) REFERENCES PACIENTE (id_persona);
ALTER TABLE VENTA
    ADD CONSTRAINT VENTA_id_comprador_fk
    FOREIGN KEY (id_comprador) REFERENCES PERSONA (id_persona);
ALTER TABLE VENTA
    ADD CONSTRAINT VENTA_id_empleado_fk
    FOREIGN KEY (id_empleado) REFERENCES EMPLEADO (id_persona);
ALTER TABLE VENTA
    ADD CONSTRAINT VENTA_id_receta_fk
    FOREIGN KEY (id_receta) REFERENCES RECETA (id_receta);
ALTER TABLE VENTA
    ADD CONSTRAINT VENTA_id_estado_venta_fk
    FOREIGN KEY (id_estado_venta) REFERENCES ESTADO_VENTA (id_estado_venta);

-- Relaciones de ITEM_VENDIDO
ALTER TABLE ITEM_VENDIDO
    ADD CONSTRAINT ITEM_VENDIDO_id_venta_fk
    FOREIGN KEY (id_venta) REFERENCES VENTA (id_venta);
ALTER TABLE ITEM_VENDIDO
    ADD CONSTRAINT ITEM_VENDIDO_id_item_fk
    FOREIGN KEY (id_item) REFERENCES ITEM (id_item);
ALTER TABLE ITEM_VENDIDO
    ADD CONSTRAINT ITEM_VENDIDO_id_lente_fk
    FOREIGN KEY (id_lente) REFERENCES LENTE (id_lente);

-- Relaciones de SERVICIO_VENDIDO
ALTER TABLE SERVICIO_VENDIDO
    ADD CONSTRAINT SERVICIO_VENDIDO_id_venta_fk
    FOREIGN KEY (id_venta) REFERENCES VENTA (id_venta);
ALTER TABLE SERVICIO_VENDIDO
    ADD CONSTRAINT SERVICIO_VENDIDO_id_servicio_fk
    FOREIGN KEY (id_servicio) REFERENCES SERVICIO (id_servicio);

-- Relaciones de COMPROBANTE
ALTER TABLE COMPROBANTE
    ADD CONSTRAINT COMPROBANTE_id_venta_fk
    FOREIGN KEY (id_venta) REFERENCES VENTA (id_venta);
ALTER TABLE COMPROBANTE
    ADD CONSTRAINT COMPROBANTE_id_tipo_comprobante_fk
    FOREIGN KEY (id_tipo_comprobante) REFERENCES TIPO_COMPROBANTE (id_tipo_comprobante);
ALTER TABLE COMPROBANTE
    ADD CONSTRAINT COMPROBANTE_id_estado_comprobante_fk
    FOREIGN KEY (id_estado_comprobante) REFERENCES ESTADO_COMPROBANTE (id_estado_comprobante);

-- Relaciones de PAGO
ALTER TABLE PAGO
    ADD CONSTRAINT PAGO_id_comprobante_fk
    FOREIGN KEY (id_comprobante) REFERENCES COMPROBANTE (id_comprobante);
ALTER TABLE PAGO
    ADD CONSTRAINT PAGO_id_metodo_pago_fk
    FOREIGN KEY (id_metodo_pago) REFERENCES METODO_PAGO (id_metodo_pago);
ALTER TABLE PAGO
    ADD CONSTRAINT PAGO_id_empleado_fk
    FOREIGN KEY (id_empleado) REFERENCES EMPLEADO (id_persona);

-- Relaciones de DEVOLUCION_PAGO
ALTER TABLE DEVOLUCION_PAGO
    ADD CONSTRAINT DEVOLUCION_PAGO_id_pago_fk
    FOREIGN KEY (id_pago) REFERENCES PAGO (id_pago);
ALTER TABLE DEVOLUCION_PAGO
    ADD CONSTRAINT DEVOLUCION_PAGO_id_empleado_fk
    FOREIGN KEY (id_empleado) REFERENCES EMPLEADO (id_persona);

-- Relaciones de MOVIMIENTO_INVENTARIO
ALTER TABLE MOVIMIENTO_INVENTARIO
    ADD CONSTRAINT MOVIMIENTO_INVENTARIO_id_item_fk
    FOREIGN KEY (id_item) REFERENCES ITEM (id_item);
ALTER TABLE MOVIMIENTO_INVENTARIO
    ADD CONSTRAINT MOVIMIENTO_INVENTARIO_id_item_vendido_fk
    FOREIGN KEY (id_item_vendido) REFERENCES ITEM_VENDIDO (id_item_vendido);
ALTER TABLE MOVIMIENTO_INVENTARIO
    ADD CONSTRAINT MOVIMIENTO_INVENTARIO_id_empleado_fk
    FOREIGN KEY (id_empleado) REFERENCES EMPLEADO (id_persona);
ALTER TABLE MOVIMIENTO_INVENTARIO
    ADD CONSTRAINT MOV_item_vendido_fk
    FOREIGN KEY (id_item_vendido, id_item) REFERENCES ITEM_VENDIDO (id_item_vendido, id_item);

-- Relaciones de RESERVA_INVENTARIO
ALTER TABLE RESERVA_INVENTARIO
    ADD CONSTRAINT RESERVA_INVENTARIO_id_item_vendido_fk
    FOREIGN KEY (id_item_vendido) REFERENCES ITEM_VENDIDO (id_item_vendido);
ALTER TABLE RESERVA_INVENTARIO
    ADD CONSTRAINT RESERVA_INVENTARIO_id_item_fk
    FOREIGN KEY (id_item) REFERENCES ITEM (id_item);
ALTER TABLE RESERVA_INVENTARIO
    ADD CONSTRAINT RES_item_vendido_fk
    FOREIGN KEY (id_item_vendido, id_item) REFERENCES ITEM_VENDIDO (id_item_vendido, id_item);

-- Relaciones de ORDEN_TRABAJO
ALTER TABLE ORDEN_TRABAJO
    ADD CONSTRAINT ORDEN_TRABAJO_id_lente_fk
    FOREIGN KEY (id_lente) REFERENCES LENTE (id_lente);
ALTER TABLE ORDEN_TRABAJO
    ADD CONSTRAINT ORDEN_TRABAJO_id_empleado_fk
    FOREIGN KEY (id_empleado) REFERENCES EMPLEADO (id_persona);
ALTER TABLE ORDEN_TRABAJO
    ADD CONSTRAINT ORDEN_TRABAJO_id_estado_orden_trabajo_fk
    FOREIGN KEY (id_estado_orden_trabajo) REFERENCES ESTADO_ORDEN_TRABAJO (id_estado_orden_trabajo);

-- Relaciones de ENVIO_LABORATORIO
ALTER TABLE ENVIO_LABORATORIO
    ADD CONSTRAINT ENVIO_LABORATORIO_id_orden_trabajo_fk
    FOREIGN KEY (id_orden_trabajo) REFERENCES ORDEN_TRABAJO (id_orden_trabajo);
ALTER TABLE ENVIO_LABORATORIO
    ADD CONSTRAINT ENVIO_LABORATORIO_id_laboratorio_fk
    FOREIGN KEY (id_laboratorio) REFERENCES LABORATORIO (id_laboratorio);

-- Relaciones de CONTROL_CALIDAD
ALTER TABLE CONTROL_CALIDAD
    ADD CONSTRAINT CONTROL_CALIDAD_id_envio_laboratorio_fk
    FOREIGN KEY (id_envio_laboratorio) REFERENCES ENVIO_LABORATORIO (id_envio_laboratorio);
ALTER TABLE CONTROL_CALIDAD
    ADD CONSTRAINT CONTROL_CALIDAD_id_empleado_fk
    FOREIGN KEY (id_empleado) REFERENCES EMPLEADO (id_persona);

-- Relaciones de PARAMETRO_CALIDAD
ALTER TABLE PARAMETRO_CALIDAD
    ADD CONSTRAINT PARAMETRO_CALIDAD_id_control_calidad_fk
    FOREIGN KEY (id_control_calidad) REFERENCES CONTROL_CALIDAD (id_control_calidad);
ALTER TABLE PARAMETRO_CALIDAD
    ADD CONSTRAINT PARAMETRO_CALIDAD_id_tipo_parametro_calidad_fk
    FOREIGN KEY (id_tipo_parametro_calidad) REFERENCES TIPO_PARAMETRO_CALIDAD (id_tipo_parametro_calidad);
ALTER TABLE PARAMETRO_CALIDAD
    ADD CONSTRAINT PARAMETRO_CALIDAD_id_medida_ojo_fk
    FOREIGN KEY (id_medida_ojo) REFERENCES MEDIDA_OJO (id_medida_ojo);

-- Relaciones de AUDITORIA
ALTER TABLE AUDITORIA
    ADD CONSTRAINT AUDITORIA_id_usuario_fk
    FOREIGN KEY (id_usuario) REFERENCES USUARIO (id_usuario);

-- Relaciones de PARAMETRO_SISTEMA
ALTER TABLE PARAMETRO_SISTEMA
    ADD CONSTRAINT PARAMETRO_SISTEMA_id_tipo_parametro_sistema_fk
    FOREIGN KEY (id_tipo_parametro_sistema) REFERENCES TIPO_PARAMETRO_SISTEMA (id_tipo_parametro_sistema);

-- Relaciones de PACIENTE_LOG
ALTER TABLE PACIENTE_LOG
    ADD CONSTRAINT PACIENTE_LOG_id_usuario_fk
    FOREIGN KEY (id_usuario) REFERENCES USUARIO (id_usuario);
ALTER TABLE PACIENTE_LOG
    ADD CONSTRAINT PACIENTE_LOG_id_persona_fk
    FOREIGN KEY (id_persona) REFERENCES PACIENTE (id_persona);

-- Relaciones de EMPLEADO_LOG
ALTER TABLE EMPLEADO_LOG
    ADD CONSTRAINT EMPLEADO_LOG_id_usuario_fk
    FOREIGN KEY (id_usuario) REFERENCES USUARIO (id_usuario);
ALTER TABLE EMPLEADO_LOG
    ADD CONSTRAINT EMPLEADO_LOG_id_persona_fk
    FOREIGN KEY (id_persona) REFERENCES EMPLEADO (id_persona);

/* ==================================================
   FASE 3: CATÁLOGOS INICIALES
   ================================================== */

-- MARCA
INSERT INTO MARCA (nombre) VALUES ('GENERICO');
INSERT INTO MARCA (nombre) VALUES ('RAYBAN');
INSERT INTO MARCA (nombre) VALUES ('OAKLEY');
INSERT INTO MARCA (nombre) VALUES ('VOGUE');
INSERT INTO MARCA (nombre) VALUES ('TREISY');

-- MATERIAL_CRISTAL
INSERT INTO MATERIAL_CRISTAL (nombre) VALUES ('RESINA');
INSERT INTO MATERIAL_CRISTAL (nombre) VALUES ('POLICARBONATO');
INSERT INTO MATERIAL_CRISTAL (nombre) VALUES ('CRISTAL_MINERAL');
INSERT INTO MATERIAL_CRISTAL (nombre) VALUES ('ALTO_INDICE');
INSERT INTO MATERIAL_CRISTAL (nombre) VALUES ('TRIVEX');
INSERT INTO MATERIAL_CRISTAL (nombre) VALUES ('DURAQUARTZ');

-- TRATAMIENTO_CRISTAL
INSERT INTO TRATAMIENTO_CRISTAL (nombre) VALUES ('FILTRO_UV');
INSERT INTO TRATAMIENTO_CRISTAL (nombre) VALUES ('ANTIREFLEJO');
INSERT INTO TRATAMIENTO_CRISTAL (nombre) VALUES ('BLUEPROTEC');
INSERT INTO TRATAMIENTO_CRISTAL (nombre) VALUES ('PHOTOBROWN');
INSERT INTO TRATAMIENTO_CRISTAL (nombre) VALUES ('PHOTOMATIC');

-- MODELO_MONTURA
INSERT INTO MODELO_MONTURA (nombre) VALUES ('ARO_COMPLETO');
INSERT INTO MODELO_MONTURA (nombre) VALUES ('SEMI_AL_AIRE');
INSERT INTO MODELO_MONTURA (nombre) VALUES ('AL_AIRE');

-- MATERIAL_MONTURA
INSERT INTO MATERIAL_MONTURA (nombre) VALUES ('ACETATO');
INSERT INTO MATERIAL_MONTURA (nombre) VALUES ('METAL');
INSERT INTO MATERIAL_MONTURA (nombre) VALUES ('CAREY');
INSERT INTO MATERIAL_MONTURA (nombre) VALUES ('TITANIO');
INSERT INTO MATERIAL_MONTURA (nombre) VALUES ('TR90');

-- DISENO_LENTE
INSERT INTO DISENO_LENTE (nombre) VALUES ('MONOFOCAL');
INSERT INTO DISENO_LENTE (nombre) VALUES ('BIFOCAL');
INSERT INTO DISENO_LENTE (nombre) VALUES ('MULTIFOCAL');
INSERT INTO DISENO_LENTE (nombre) VALUES ('PROGRESIVO');

-- TIPO_DOCUMENTO
INSERT INTO TIPO_DOCUMENTO (nombre) VALUES ('DNI');
INSERT INTO TIPO_DOCUMENTO (nombre) VALUES ('PASAPORTE');
INSERT INTO TIPO_DOCUMENTO (nombre) VALUES ('RUC');
INSERT INTO TIPO_DOCUMENTO (nombre) VALUES ('CARNET_EXTRANJERIA');

-- CARGO
INSERT INTO CARGO (nombre) VALUES ('RECEPCIONISTA');
INSERT INTO CARGO (nombre) VALUES ('VENDEDOR');
INSERT INTO CARGO (nombre) VALUES ('OPTOMETRA');
INSERT INTO CARGO (nombre) VALUES ('TECNICO');
INSERT INTO CARGO (nombre) VALUES ('ADMINISTRADOR');

-- TIPO_MEDIDA
INSERT INTO TIPO_MEDIDA (nombre) VALUES ('LEJOS');
INSERT INTO TIPO_MEDIDA (nombre) VALUES ('CERCA');
INSERT INTO TIPO_MEDIDA (nombre) VALUES ('CENTRADO');

-- TIPO_OJO
INSERT INTO TIPO_OJO (nombre) VALUES ('DERECHO');
INSERT INTO TIPO_OJO (nombre) VALUES ('IZQUIERDO');

-- ESTADO_VENTA
INSERT INTO ESTADO_VENTA (nombre) VALUES ('COTIZACION');
INSERT INTO ESTADO_VENTA (nombre) VALUES ('CONFIRMADA');
INSERT INTO ESTADO_VENTA (nombre) VALUES ('EN_PROCESO');
INSERT INTO ESTADO_VENTA (nombre) VALUES ('ENTREGADA');
INSERT INTO ESTADO_VENTA (nombre) VALUES ('ANULADA');

-- TIPO_COMPROBANTE
INSERT INTO TIPO_COMPROBANTE (nombre) VALUES ('BOLETA');
INSERT INTO TIPO_COMPROBANTE (nombre) VALUES ('FACTURA');
INSERT INTO TIPO_COMPROBANTE (nombre) VALUES ('NOTA_VENTA');

-- METODO_PAGO
INSERT INTO METODO_PAGO (nombre) VALUES ('YAPE');
INSERT INTO METODO_PAGO (nombre) VALUES ('PLIN');
INSERT INTO METODO_PAGO (nombre) VALUES ('TARJETA');
INSERT INTO METODO_PAGO (nombre) VALUES ('EFECTIVO');
INSERT INTO METODO_PAGO (nombre) VALUES ('TRANSFERENCIA');

-- ESTADO_ORDEN_TRABAJO
INSERT INTO ESTADO_ORDEN_TRABAJO (nombre) VALUES ('PENDIENTE');
INSERT INTO ESTADO_ORDEN_TRABAJO (nombre) VALUES ('ENVIADO');
INSERT INTO ESTADO_ORDEN_TRABAJO (nombre) VALUES ('OBSERVADO');
INSERT INTO ESTADO_ORDEN_TRABAJO (nombre) VALUES ('APROBADO');
INSERT INTO ESTADO_ORDEN_TRABAJO (nombre) VALUES ('NOTIFICADO');
INSERT INTO ESTADO_ORDEN_TRABAJO (nombre) VALUES ('ENTREGADO');
INSERT INTO ESTADO_ORDEN_TRABAJO (nombre) VALUES ('ANULADO');

-- TIPO_PARAMETRO_SISTEMA
INSERT INTO TIPO_PARAMETRO_SISTEMA (nombre) VALUES ('STRING');
INSERT INTO TIPO_PARAMETRO_SISTEMA (nombre) VALUES ('INTEGER');
INSERT INTO TIPO_PARAMETRO_SISTEMA (nombre) VALUES ('REAL');
INSERT INTO TIPO_PARAMETRO_SISTEMA (nombre) VALUES ('BOOLEAN');

-- ESTADO_COMPROBANTE
INSERT INTO ESTADO_COMPROBANTE (nombre) VALUES ('PENDIENTE');
INSERT INTO ESTADO_COMPROBANTE (nombre) VALUES ('CON_ADELANTO');
INSERT INTO ESTADO_COMPROBANTE (nombre) VALUES ('CANCELADO');
INSERT INTO ESTADO_COMPROBANTE (nombre) VALUES ('ANULADO');

COMMIT;

-- REGLAS DE NEGOCIO EN EL BACKEND (no impuestas por todas las restricciones SQL):
-- 1. USUARIO y EMPLEADO deben crearse en la misma transacción, usando FK diferida.
-- 2. Las contraseñas en USUARIO.password son hashes; jamás texto plano.
-- 3. Sincronizar ITEM.stock con MOVIMIENTO_INVENTARIO en una sola transacción.
-- 4. Conciliar VENTA.total y LENTE.sub_total con los detalles; no duplicar subtotales.
-- 5. Verificar pagos, devoluciones y estados de comprobante atómicamente.
-- 6. Sincronizar RECETA.es_externo con EXAMEN.es_externo.
-- 7. Validar pertenencia de medidas usadas en fabricación y control de calidad.
-- 8. Guardar auditorías desde la aplicación; el DDL no crea triggers de auditoría.
-- 9. Agudeza visual: utilizar escala decimal en la aplicación; rango pendiente de especificación clínica.
-- 10. No asumir que las FK por sí solas implementan transiciones de estado.
-- 11. La devolución monetaria y física son operaciones diferentes.
-- 12. La columna SERVICIO_VENDIDO.sub_total es virtual: no insertarla ni actualizarla.

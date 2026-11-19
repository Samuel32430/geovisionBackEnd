-- ============================================================================
-- SISTEMA GEOVISION - BASE DE DATOS ORACLE 19c / 23c
-- SCRIPT DE DATOS DE PRUEBA INTEGRALES (SEED DATA)
-- Cubre el 100% de tablas del sistema:
--  1. CONFIGURACION_EMPRESA
--  2. CATEGORIA
--  3. MATERIAL_CRISTAL
--  4. TRATAMIENTO_CRISTAL
--  5. CARGO
--  6. ROL
--  7. PERMISO
--  8. ROL_PERMISO
--  9. PERSONA
-- 10. USUARIO
-- 11. USUARIO_ROL
-- 12. EMPLEADO
-- 13. EMPLEADO_CARGO
-- 14. PACIENTE
-- 15. PRODUCTO
-- 16. MONTURA
-- 17. SERVICIO
-- 18. LABORATORIO
-- 19. MOVIMIENTO_STOCK_LOG
-- 20. MEDIDA_OJO
-- 21. RECETA
-- 22. MEDIDA
-- 23. LENTE
-- 24. LENTE_MEDIDA
-- 25. LENTE_TRATAMIENTO_CRISTAL
-- 26. ORDEN_TRABAJO
-- 27. ENVIO_LABORATORIO
-- 28. VENTA
-- 29. PRODUCTO_VENDIDO
-- 30. SERVICIO_VENDIDO
-- 31. PAGO
-- 32. USUARIO_LOG
-- 33. PACIENTE_LOG
-- 34. EMPLEADO_LOG
-- ============================================================================

SET SERVEROUTPUT ON SIZE UNLIMITED;
SET DEFINE OFF;

PROMPT >>> INICIANDO CARGA DE DATOS DE PRUEBA GEOVISION...

-- ----------------------------------------------------------------------------
-- 1. CONFIGURACION_EMPRESA
-- ----------------------------------------------------------------------------
PROMPT [1/15] Insertando Configuracion de la Empresa...
INSERT INTO CONFIGURACION_EMPRESA (nombre_legal, nombre_comercial, ruc, direccion, telefono, correo, logo)
SELECT 'GEOVISION OPTICA INTEGRAL S.A.C.', 'GeoVision Óptica & Laboratorio', '20608945123', 
       'Av. Javier Prado Este 2450, San Borja, Lima', '+51 1 445-8920', 'contacto@geovision.pe', '/assets/images/logo_geovision.png'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM CONFIGURACION_EMPRESA WHERE ruc = '20608945123');

-- ----------------------------------------------------------------------------
-- 2. CATALOGOS BASE: CATEGORIA, MATERIAL_CRISTAL, TRATAMIENTO_CRISTAL, CARGO
-- ----------------------------------------------------------------------------
PROMPT [2/15] Insertando Catalogos Base...

-- Categorias
INSERT INTO CATEGORIA (nombre, descripcion, estado, fecha_registro)
SELECT 'MONTURAS OFTALMICAS', 'Monturas para lentes de corrección y medida oftálmica', 1, SYSDATE FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM CATEGORIA WHERE nombre = 'MONTURAS OFTALMICAS');

INSERT INTO CATEGORIA (nombre, descripcion, estado, fecha_registro)
SELECT 'GAFAS DE SOL', 'Lentes de sol con protección UV400 y lentes polarizadas', 1, SYSDATE FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM CATEGORIA WHERE nombre = 'GAFAS DE SOL');

INSERT INTO CATEGORIA (nombre, descripcion, estado, fecha_registro)
SELECT 'LENTES DE CONTACTO', 'Lentes de contacto blandos, tóricos, cosméticos y gas permeables', 1, SYSDATE FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM CATEGORIA WHERE nombre = 'LENTES DE CONTACTO');

INSERT INTO CATEGORIA (nombre, descripcion, estado, fecha_registro)
SELECT 'ACCESORIOS Y REPUESTOS', 'Estuches, paños de microfibra, cordones, plaquetas y tornillería', 1, SYSDATE FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM CATEGORIA WHERE nombre = 'ACCESORIOS Y REPUESTOS');

INSERT INTO CATEGORIA (nombre, descripcion, estado, fecha_registro)
SELECT 'SOLUCIONES Y GOTAS', 'Líquidos limpiadores con nanotecnología y lágrimas artificiales para lubricación', 1, SYSDATE FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM CATEGORIA WHERE nombre = 'SOLUCIONES Y GOTAS');

-- Material Cristal
INSERT INTO MATERIAL_CRISTAL (nombre)
SELECT 'POLICARBONATO' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM MATERIAL_CRISTAL WHERE nombre = 'POLICARBONATO');

INSERT INTO MATERIAL_CRISTAL (nombre)
SELECT 'RESINA' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM MATERIAL_CRISTAL WHERE nombre = 'RESINA');

INSERT INTO MATERIAL_CRISTAL (nombre)
SELECT 'CRISTAL MINERAL' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM MATERIAL_CRISTAL WHERE nombre = 'CRISTAL MINERAL');

INSERT INTO MATERIAL_CRISTAL (nombre)
SELECT 'ALTO INDICE' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM MATERIAL_CRISTAL WHERE nombre = 'ALTO INDICE');

-- Tratamiento Cristal
INSERT INTO TRATAMIENTO_CRISTAL (nombre)
SELECT 'FILTRO UV' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM TRATAMIENTO_CRISTAL WHERE nombre = 'FILTRO UV');

INSERT INTO TRATAMIENTO_CRISTAL (nombre)
SELECT 'ANTIRREFLEJO' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM TRATAMIENTO_CRISTAL WHERE nombre = 'ANTIRREFLEJO');

INSERT INTO TRATAMIENTO_CRISTAL (nombre)
SELECT 'BLUE DEFENSE' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM TRATAMIENTO_CRISTAL WHERE nombre = 'BLUE DEFENSE');

INSERT INTO TRATAMIENTO_CRISTAL (nombre)
SELECT 'FOTOCROMATICO' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM TRATAMIENTO_CRISTAL WHERE nombre = 'FOTOCROMATICO');

-- Cargo
INSERT INTO CARGO (nombre)
SELECT 'ADMINISTRADOR' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CARGO WHERE nombre = 'ADMINISTRADOR');

INSERT INTO CARGO (nombre)
SELECT 'VENDEDOR' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CARGO WHERE nombre = 'VENDEDOR');

INSERT INTO CARGO (nombre)
SELECT 'OPTOMETRA' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CARGO WHERE nombre = 'OPTOMETRA');

INSERT INTO CARGO (nombre)
SELECT 'TECNICO' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CARGO WHERE nombre = 'TECNICO');

INSERT INTO CARGO (nombre)
SELECT 'RECEPCIONISTA' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CARGO WHERE nombre = 'RECEPCIONISTA');

INSERT INTO CARGO (nombre)
SELECT 'GERENTE GENERAL' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM CARGO WHERE nombre = 'GERENTE GENERAL');

-- ----------------------------------------------------------------------------
-- 3. SEGURIDAD: ROLES Y PERMISOS
-- ----------------------------------------------------------------------------
PROMPT [3/15] Verificando Roles y Permisos de Seguridad...

-- Roles
INSERT INTO ROL (nombre)
SELECT 'ADMINISTRADOR' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM ROL WHERE nombre = 'ADMINISTRADOR');

INSERT INTO ROL (nombre)
SELECT 'VENDEDOR' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM ROL WHERE nombre = 'VENDEDOR');

INSERT INTO ROL (nombre)
SELECT 'OPTOMETRA' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM ROL WHERE nombre = 'OPTOMETRA');

INSERT INTO ROL (nombre)
SELECT 'TECNICO' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM ROL WHERE nombre = 'TECNICO');

INSERT INTO ROL (nombre)
SELECT 'RECEPCIONISTA' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM ROL WHERE nombre = 'RECEPCIONISTA');

-- Permisos (modulo: 1=Seguridad, 2=Pacientes, 3=Ventas, 4=OT, 5=Inventario, 6=Mantenimiento, 7=Reportes)
INSERT INTO PERMISO (nombre, modulo) SELECT 'SEGURIDAD_ADMINISTRAR', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'SEGURIDAD_ADMINISTRAR');
INSERT INTO PERMISO (nombre, modulo) SELECT 'SEGURIDAD_LEER', 1 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'SEGURIDAD_LEER');
INSERT INTO PERMISO (nombre, modulo) SELECT 'PACIENTES_LEER', 2 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'PACIENTES_LEER');
INSERT INTO PERMISO (nombre, modulo) SELECT 'PACIENTES_REGISTRAR', 2 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'PACIENTES_REGISTRAR');
INSERT INTO PERMISO (nombre, modulo) SELECT 'PACIENTES_GESTIONAR', 2 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'PACIENTES_GESTIONAR');
INSERT INTO PERMISO (nombre, modulo) SELECT 'EXAMENES_REGISTRAR', 2 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'EXAMENES_REGISTRAR');
INSERT INTO PERMISO (nombre, modulo) SELECT 'RECETAS_REGISTRAR', 2 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'RECETAS_REGISTRAR');
INSERT INTO PERMISO (nombre, modulo) SELECT 'VENTAS_LEER', 3 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'VENTAS_LEER');
INSERT INTO PERMISO (nombre, modulo) SELECT 'VENTAS_REGISTRAR', 3 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'VENTAS_REGISTRAR');
INSERT INTO PERMISO (nombre, modulo) SELECT 'PAGOS_GESTIONAR', 3 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'PAGOS_GESTIONAR');
INSERT INTO PERMISO (nombre, modulo) SELECT 'ORDENES_TRABAJO_LEER', 4 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'ORDENES_TRABAJO_LEER');
INSERT INTO PERMISO (nombre, modulo) SELECT 'ORDENES_TRABAJO_GESTIONAR', 4 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'ORDENES_TRABAJO_GESTIONAR');
INSERT INTO PERMISO (nombre, modulo) SELECT 'ORDENES_TRABAJO_CALIDAD', 4 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'ORDENES_TRABAJO_CALIDAD');
INSERT INTO PERMISO (nombre, modulo) SELECT 'INVENTARIO_LEER', 5 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'INVENTARIO_LEER');
INSERT INTO PERMISO (nombre, modulo) SELECT 'PRODUCTOS_LEER', 5 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'PRODUCTOS_LEER');
INSERT INTO PERMISO (nombre, modulo) SELECT 'PRODUCTOS_REGISTRAR', 5 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'PRODUCTOS_REGISTRAR');
INSERT INTO PERMISO (nombre, modulo) SELECT 'PRODUCTOS_GESTIONAR', 5 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'PRODUCTOS_GESTIONAR');
INSERT INTO PERMISO (nombre, modulo) SELECT 'INVENTARIO_REGISTRAR', 5 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'INVENTARIO_REGISTRAR');
INSERT INTO PERMISO (nombre, modulo) SELECT 'INVENTARIO_AJUSTAR', 5 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'INVENTARIO_AJUSTAR');
INSERT INTO PERMISO (nombre, modulo) SELECT 'INVENTARIO_GESTIONAR', 5 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'INVENTARIO_GESTIONAR');
INSERT INTO PERMISO (nombre, modulo) SELECT 'MANTENIMIENTO_GESTIONAR', 6 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'MANTENIMIENTO_GESTIONAR');
INSERT INTO PERMISO (nombre, modulo) SELECT 'REPORTES_VER', 7 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM PERMISO WHERE nombre = 'REPORTES_VER');

-- Asignar permisos a roles (Matriz RBAC)
INSERT INTO ROL_PERMISO (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso
FROM ROL r CROSS JOIN PERMISO p
WHERE r.nombre = 'ADMINISTRADOR'
  AND NOT EXISTS (SELECT 1 FROM ROL_PERMISO rp WHERE rp.id_rol = r.id_rol AND rp.id_permiso = p.id_permiso);

INSERT INTO ROL_PERMISO (id_rol, id_permiso)
SELECT (SELECT id_rol FROM ROL WHERE nombre = 'VENDEDOR'), p.id_permiso
FROM PERMISO p
WHERE p.nombre IN ('VENTAS_LEER', 'VENTAS_REGISTRAR', 'PAGOS_GESTIONAR', 'PACIENTES_LEER', 'PACIENTES_REGISTRAR', 'PRODUCTOS_LEER', 'INVENTARIO_LEER')
  AND NOT EXISTS (SELECT 1 FROM ROL_PERMISO rp WHERE rp.id_rol = (SELECT id_rol FROM ROL WHERE nombre = 'VENDEDOR') AND rp.id_permiso = p.id_permiso);

INSERT INTO ROL_PERMISO (id_rol, id_permiso)
SELECT (SELECT id_rol FROM ROL WHERE nombre = 'OPTOMETRA'), p.id_permiso
FROM PERMISO p
WHERE p.nombre IN ('PACIENTES_LEER', 'PACIENTES_REGISTRAR', 'PACIENTES_GESTIONAR', 'EXAMENES_REGISTRAR', 'RECETAS_REGISTRAR', 'ORDENES_TRABAJO_LEER')
  AND NOT EXISTS (SELECT 1 FROM ROL_PERMISO rp WHERE rp.id_rol = (SELECT id_rol FROM ROL WHERE nombre = 'OPTOMETRA') AND rp.id_permiso = p.id_permiso);

INSERT INTO ROL_PERMISO (id_rol, id_permiso)
SELECT (SELECT id_rol FROM ROL WHERE nombre = 'TECNICO'), p.id_permiso
FROM PERMISO p
WHERE p.nombre IN ('ORDENES_TRABAJO_LEER', 'ORDENES_TRABAJO_GESTIONAR', 'ORDENES_TRABAJO_CALIDAD', 'INVENTARIO_LEER', 'INVENTARIO_AJUSTAR')
  AND NOT EXISTS (SELECT 1 FROM ROL_PERMISO rp WHERE rp.id_rol = (SELECT id_rol FROM ROL WHERE nombre = 'TECNICO') AND rp.id_permiso = p.id_permiso);

INSERT INTO ROL_PERMISO (id_rol, id_permiso)
SELECT (SELECT id_rol FROM ROL WHERE nombre = 'RECEPCIONISTA'), p.id_permiso
FROM PERMISO p
WHERE p.nombre IN ('PACIENTES_LEER', 'PACIENTES_REGISTRAR', 'VENTAS_LEER', 'PRODUCTOS_LEER')
  AND NOT EXISTS (SELECT 1 FROM ROL_PERMISO rp WHERE rp.id_rol = (SELECT id_rol FROM ROL WHERE nombre = 'RECEPCIONISTA') AND rp.id_permiso = p.id_permiso);

-- ----------------------------------------------------------------------------
-- 4. PERSONAS, USUARIOS Y ASIGNACION DE ROLES
-- Password BCrypt: '$2a$10$czbjY67pnY/9VpHMabujT.Oc4vD1/2lnseB0Jh4xZif7D6A0JJruK' = 'admin123'
-- ----------------------------------------------------------------------------
PROMPT [4/15] Insertando Personas y Usuarios...

-- Personas del Personal
INSERT INTO PERSONA (nombres, apellidos, telefono, fecha_nacimiento, sexo, direccion, numero_documento, tipo_documento)
SELECT 'Administrador', 'General GeoVision', '999888777', TO_DATE('1985-01-01', 'YYYY-MM-DD'), 1, 'Av. Javier Prado Este 2500, San Borja, Lima', '00000001', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PERSONA WHERE numero_documento = '00000001');

INSERT INTO PERSONA (nombres, apellidos, telefono, fecha_nacimiento, sexo, direccion, numero_documento, tipo_documento)
SELECT 'Samuel David', 'Vilca Salinas', '987654321', TO_DATE('1994-05-12', 'YYYY-MM-DD'), 1, 'Av. Brasil 1250, Jesús María, Lima', '70961428', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PERSONA WHERE numero_documento = '70961428');

INSERT INTO PERSONA (nombres, apellidos, telefono, fecha_nacimiento, sexo, direccion, numero_documento, tipo_documento)
SELECT 'Elena Patricia', 'Ramos Quispe', '991234567', TO_DATE('1989-08-23', 'YYYY-MM-DD'), 2, 'Calle Los Pinos 450, Miraflores, Lima', '45892134', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PERSONA WHERE numero_documento = '45892134');

INSERT INTO PERSONA (nombres, apellidos, telefono, fecha_nacimiento, sexo, direccion, numero_documento, tipo_documento)
SELECT 'Marcos Antonio', 'Huamán Castillo', '984512367', TO_DATE('1992-03-15', 'YYYY-MM-DD'), 1, 'Jr. Huancavelica 780, Cercado de Lima', '48123091', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PERSONA WHERE numero_documento = '48123091');

INSERT INTO PERSONA (nombres, apellidos, telefono, fecha_nacimiento, sexo, direccion, numero_documento, tipo_documento)
SELECT 'Lucía Beatriz', 'Salazar Benítez', '976543218', TO_DATE('1996-09-30', 'YYYY-MM-DD'), 2, 'Av. Arequipa 2140, Lince, Lima', '72341092', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PERSONA WHERE numero_documento = '72341092');

-- Personas Pacientes
INSERT INTO PERSONA (nombres, apellidos, telefono, fecha_nacimiento, sexo, direccion, numero_documento, tipo_documento)
SELECT 'Jorge Luis', 'Valdivia Peña', '945678123', TO_DATE('1983-05-18', 'YYYY-MM-DD'), 1, 'Calle Las Begonias 320, San Isidro, Lima', '41258963', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PERSONA WHERE numero_documento = '41258963');

INSERT INTO PERSONA (nombres, apellidos, telefono, fecha_nacimiento, sexo, direccion, numero_documento, tipo_documento)
SELECT 'María Fernanda', 'Gómez Flores', '952341876', TO_DATE('1995-12-04', 'YYYY-MM-DD'), 2, 'Av. Universitaria 1450, San Miguel, Lima', '73849201', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PERSONA WHERE numero_documento = '73849201');

INSERT INTO PERSONA (nombres, apellidos, telefono, fecha_nacimiento, sexo, direccion, numero_documento, tipo_documento)
SELECT 'Andrés Alberto', 'Cornejo Rivas', '963258741', TO_DATE('1975-02-27', 'YYYY-MM-DD'), 1, 'Jr. Junín 410, Magdalena del Mar, Lima', '10472938', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PERSONA WHERE numero_documento = '10472938');

INSERT INTO PERSONA (nombres, apellidos, telefono, fecha_nacimiento, sexo, direccion, numero_documento, tipo_documento)
SELECT 'Patricia Isabel', 'Morales Chávez', '981247856', TO_DATE('1998-07-14', 'YYYY-MM-DD'), 2, 'Calle Los Álamos 112, Surco, Lima', '76543210', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PERSONA WHERE numero_documento = '76543210');

-- Usuarios
INSERT INTO USUARIO (nombre, password, correo, estado, id_persona)
SELECT 'optometra', '$2a$10$czbjY67pnY/9VpHMabujT.Oc4vD1/2lnseB0Jh4xZif7D6A0JJruK', 'optometra@geovision.pe', 1, 
       (SELECT id_persona FROM PERSONA WHERE numero_documento = '45892134') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM USUARIO WHERE nombre = 'optometra');

INSERT INTO USUARIO (nombre, password, correo, estado, id_persona)
SELECT 'vendedor', '$2a$10$czbjY67pnY/9VpHMabujT.Oc4vD1/2lnseB0Jh4xZif7D6A0JJruK', 'vendedor@geovision.pe', 1, 
       (SELECT id_persona FROM PERSONA WHERE numero_documento = '70961428') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM USUARIO WHERE nombre = 'vendedor');

INSERT INTO USUARIO (nombre, password, correo, estado, id_persona)
SELECT 'tecnico', '$2a$10$czbjY67pnY/9VpHMabujT.Oc4vD1/2lnseB0Jh4xZif7D6A0JJruK', 'tecnico@geovision.pe', 1, 
       (SELECT id_persona FROM PERSONA WHERE numero_documento = '48123091') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM USUARIO WHERE nombre = 'tecnico');

INSERT INTO USUARIO (nombre, password, correo, estado, id_persona)
SELECT 'recepcion', '$2a$10$czbjY67pnY/9VpHMabujT.Oc4vD1/2lnseB0Jh4xZif7D6A0JJruK', 'recepcion@geovision.pe', 1, 
       (SELECT id_persona FROM PERSONA WHERE numero_documento = '72341092') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM USUARIO WHERE nombre = 'recepcion');

-- Asignar Roles a Usuarios
INSERT INTO USUARIO_ROL (id_usuario, id_rol)
SELECT (SELECT id_usuario FROM USUARIO WHERE nombre = 'admin'), (SELECT id_rol FROM ROL WHERE nombre = 'ADMINISTRADOR') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM USUARIO_ROL WHERE id_usuario = (SELECT id_usuario FROM USUARIO WHERE nombre = 'admin') AND id_rol = (SELECT id_rol FROM ROL WHERE nombre = 'ADMINISTRADOR'));

INSERT INTO USUARIO_ROL (id_usuario, id_rol)
SELECT (SELECT id_usuario FROM USUARIO WHERE nombre = 'optometra'), (SELECT id_rol FROM ROL WHERE nombre = 'OPTOMETRA') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM USUARIO_ROL WHERE id_usuario = (SELECT id_usuario FROM USUARIO WHERE nombre = 'optometra') AND id_rol = (SELECT id_rol FROM ROL WHERE nombre = 'OPTOMETRA'));

INSERT INTO USUARIO_ROL (id_usuario, id_rol)
SELECT (SELECT id_usuario FROM USUARIO WHERE nombre = 'vendedor'), (SELECT id_rol FROM ROL WHERE nombre = 'VENDEDOR') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM USUARIO_ROL WHERE id_usuario = (SELECT id_usuario FROM USUARIO WHERE nombre = 'vendedor') AND id_rol = (SELECT id_rol FROM ROL WHERE nombre = 'VENDEDOR'));

INSERT INTO USUARIO_ROL (id_usuario, id_rol)
SELECT (SELECT id_usuario FROM USUARIO WHERE nombre = 'tecnico'), (SELECT id_rol FROM ROL WHERE nombre = 'TECNICO') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM USUARIO_ROL WHERE id_usuario = (SELECT id_usuario FROM USUARIO WHERE nombre = 'tecnico') AND id_rol = (SELECT id_rol FROM ROL WHERE nombre = 'TECNICO'));

INSERT INTO USUARIO_ROL (id_usuario, id_rol)
SELECT (SELECT id_usuario FROM USUARIO WHERE nombre = 'recepcion'), (SELECT id_rol FROM ROL WHERE nombre = 'RECEPCIONISTA') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM USUARIO_ROL WHERE id_usuario = (SELECT id_usuario FROM USUARIO WHERE nombre = 'recepcion') AND id_rol = (SELECT id_rol FROM ROL WHERE nombre = 'RECEPCIONISTA'));

-- ----------------------------------------------------------------------------
-- 5. EMPLEADOS, EMPLEADO_CARGO Y PACIENTES
-- ----------------------------------------------------------------------------
PROMPT [5/15] Insertando Empleados, Cargos y Pacientes...

-- Empleados
INSERT INTO EMPLEADO (id_persona, estado, fecha_contrato, id_usuario)
SELECT (SELECT id_persona FROM PERSONA WHERE numero_documento = '45892134'), 1, TO_DATE('2023-01-15', 'YYYY-MM-DD'), 
       (SELECT id_usuario FROM USUARIO WHERE nombre = 'optometra') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM EMPLEADO WHERE id_persona = (SELECT id_persona FROM PERSONA WHERE numero_documento = '45892134'));

INSERT INTO EMPLEADO (id_persona, estado, fecha_contrato, id_usuario)
SELECT (SELECT id_persona FROM PERSONA WHERE numero_documento = '70961428'), 1, TO_DATE('2023-03-01', 'YYYY-MM-DD'), 
       (SELECT id_usuario FROM USUARIO WHERE nombre = 'vendedor') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM EMPLEADO WHERE id_persona = (SELECT id_persona FROM PERSONA WHERE numero_documento = '70961428'));

INSERT INTO EMPLEADO (id_persona, estado, fecha_contrato, id_usuario)
SELECT (SELECT id_persona FROM PERSONA WHERE numero_documento = '48123091'), 1, TO_DATE('2023-06-01', 'YYYY-MM-DD'), 
       (SELECT id_usuario FROM USUARIO WHERE nombre = 'tecnico') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM EMPLEADO WHERE id_persona = (SELECT id_persona FROM PERSONA WHERE numero_documento = '48123091'));

INSERT INTO EMPLEADO (id_persona, estado, fecha_contrato, id_usuario)
SELECT (SELECT id_persona FROM PERSONA WHERE numero_documento = '72341092'), 1, TO_DATE('2023-08-15', 'YYYY-MM-DD'), 
       (SELECT id_usuario FROM USUARIO WHERE nombre = 'recepcion') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM EMPLEADO WHERE id_persona = (SELECT id_persona FROM PERSONA WHERE numero_documento = '72341092'));

-- Empleado Cargo
INSERT INTO EMPLEADO_CARGO (id_persona, id_cargo)
SELECT (SELECT id_persona FROM PERSONA WHERE numero_documento = '45892134'), (SELECT id_cargo FROM CARGO WHERE nombre = 'OPTOMETRA') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM EMPLEADO_CARGO WHERE id_persona = (SELECT id_persona FROM PERSONA WHERE numero_documento = '45892134'));

INSERT INTO EMPLEADO_CARGO (id_persona, id_cargo)
SELECT (SELECT id_persona FROM PERSONA WHERE numero_documento = '70961428'), (SELECT id_cargo FROM CARGO WHERE nombre = 'VENDEDOR') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM EMPLEADO_CARGO WHERE id_persona = (SELECT id_persona FROM PERSONA WHERE numero_documento = '70961428'));

INSERT INTO EMPLEADO_CARGO (id_persona, id_cargo)
SELECT (SELECT id_persona FROM PERSONA WHERE numero_documento = '48123091'), (SELECT id_cargo FROM CARGO WHERE nombre = 'TECNICO') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM EMPLEADO_CARGO WHERE id_persona = (SELECT id_persona FROM PERSONA WHERE numero_documento = '48123091'));

INSERT INTO EMPLEADO_CARGO (id_persona, id_cargo)
SELECT (SELECT id_persona FROM PERSONA WHERE numero_documento = '72341092'), (SELECT id_cargo FROM CARGO WHERE nombre = 'RECEPCIONISTA') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM EMPLEADO_CARGO WHERE id_persona = (SELECT id_persona FROM PERSONA WHERE numero_documento = '72341092'));

-- Pacientes
INSERT INTO PACIENTE (id_persona, estado, fecha_registro, id_usuario)
SELECT (SELECT id_persona FROM PERSONA WHERE numero_documento = '41258963'), 1, TO_DATE('2024-01-10', 'YYYY-MM-DD'), NULL FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PACIENTE WHERE id_persona = (SELECT id_persona FROM PERSONA WHERE numero_documento = '41258963'));

INSERT INTO PACIENTE (id_persona, estado, fecha_registro, id_usuario)
SELECT (SELECT id_persona FROM PERSONA WHERE numero_documento = '73849201'), 1, TO_DATE('2024-02-14', 'YYYY-MM-DD'), NULL FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PACIENTE WHERE id_persona = (SELECT id_persona FROM PERSONA WHERE numero_documento = '73849201'));

INSERT INTO PACIENTE (id_persona, estado, fecha_registro, id_usuario)
SELECT (SELECT id_persona FROM PERSONA WHERE numero_documento = '10472938'), 1, TO_DATE('2024-03-20', 'YYYY-MM-DD'), NULL FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PACIENTE WHERE id_persona = (SELECT id_persona FROM PERSONA WHERE numero_documento = '10472938'));

INSERT INTO PACIENTE (id_persona, estado, fecha_registro, id_usuario)
SELECT (SELECT id_persona FROM PERSONA WHERE numero_documento = '76543210'), 1, TO_DATE('2024-04-05', 'YYYY-MM-DD'), NULL FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PACIENTE WHERE id_persona = (SELECT id_persona FROM PERSONA WHERE numero_documento = '76543210'));

-- ----------------------------------------------------------------------------
-- 6. PRODUCTOS, MONTURAS Y MOVIMIENTOS KARDEX
-- ----------------------------------------------------------------------------
PROMPT [6/15] Insertando Catalogo de Productos y Monturas...

-- Monturas Oftálmicas
INSERT INTO PRODUCTO (codigo, nombre, marca, descripcion, precio, stock, estado, fecha_registro, id_categoria)
SELECT 'MON-RAY-001', 'Montura Oftálmica Ray-Ban Aviator RX6489', 'Ray-Ban', 'Montura clásica metálica dorada de alta resistencia y almohadillas ajustables', 480.00, 25, 1, SYSDATE - 60, (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'MONTURAS OFTALMICAS') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PRODUCTO WHERE codigo = 'MON-RAY-001');

INSERT INTO MONTURA (id_producto, color, modelo, material)
SELECT (SELECT id_producto FROM PRODUCTO WHERE codigo = 'MON-RAY-001'), 'Dorado Brillante', 1, 2 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM MONTURA WHERE id_producto = (SELECT id_producto FROM PRODUCTO WHERE codigo = 'MON-RAY-001'));

INSERT INTO PRODUCTO (codigo, nombre, marca, descripcion, precio, stock, estado, fecha_registro, id_categoria)
SELECT 'MON-OAK-002', 'Montura Oftálmica Oakley Holbrook RX', 'Oakley', 'Montura deportiva ligera de material O-Matter / TR90 resistente a impactos', 420.00, 20, 1, SYSDATE - 50, (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'MONTURAS OFTALMICAS') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PRODUCTO WHERE codigo = 'MON-OAK-002');

INSERT INTO MONTURA (id_producto, color, modelo, material)
SELECT (SELECT id_producto FROM PRODUCTO WHERE codigo = 'MON-OAK-002'), 'Negro Satinado', 1, 4 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM MONTURA WHERE id_producto = (SELECT id_producto FROM PRODUCTO WHERE codigo = 'MON-OAK-002'));

INSERT INTO PRODUCTO (codigo, nombre, marca, descripcion, precio, stock, estado, fecha_registro, id_categoria)
SELECT 'MON-SIL-003', 'Montura Silhouette Titan Minimal Art', 'Silhouette', 'Montura ultra ligera al aire de titanio puro sin bisagras, fabricación austríaca', 650.00, 15, 1, SYSDATE - 45, (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'MONTURAS OFTALMICAS') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PRODUCTO WHERE codigo = 'MON-SIL-003');

INSERT INTO MONTURA (id_producto, color, modelo, material)
SELECT (SELECT id_producto FROM PRODUCTO WHERE codigo = 'MON-SIL-003'), 'Titanio Plateado', 3, 3 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM MONTURA WHERE id_producto = (SELECT id_producto FROM PRODUCTO WHERE codigo = 'MON-SIL-003'));

INSERT INTO PRODUCTO (codigo, nombre, marca, descripcion, precio, stock, estado, fecha_registro, id_categoria)
SELECT 'MON-VOG-004', 'Montura Vogue Eyewear Acetato Retro', 'Vogue', 'Montura femenina en acetato pulido diseño cat-eye vintage', 320.00, 30, 1, SYSDATE - 40, (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'MONTURAS OFTALMICAS') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PRODUCTO WHERE codigo = 'MON-VOG-004');

INSERT INTO MONTURA (id_producto, color, modelo, material)
SELECT (SELECT id_producto FROM PRODUCTO WHERE codigo = 'MON-VOG-004'), 'Carey Habana', 1, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM MONTURA WHERE id_producto = (SELECT id_producto FROM PRODUCTO WHERE codigo = 'MON-VOG-004'));

INSERT INTO PRODUCTO (codigo, nombre, marca, descripcion, precio, stock, estado, fecha_registro, id_categoria)
SELECT 'MON-TOM-005', 'Montura Tommy Hilfiger Semi al Aire TH1789', 'Tommy Hilfiger', 'Montura ejecutiva semi al aire mixta metal con terminales de acetato azul', 380.00, 18, 1, SYSDATE - 35, (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'MONTURAS OFTALMICAS') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PRODUCTO WHERE codigo = 'MON-TOM-005');

INSERT INTO MONTURA (id_producto, color, modelo, material)
SELECT (SELECT id_producto FROM PRODUCTO WHERE codigo = 'MON-TOM-005'), 'Azul Marino / Plata', 2, 5 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM MONTURA WHERE id_producto = (SELECT id_producto FROM PRODUCTO WHERE codigo = 'MON-TOM-005'));

-- Gafas de Sol
INSERT INTO PRODUCTO (codigo, nombre, marca, descripcion, precio, stock, estado, fecha_registro, id_categoria)
SELECT 'SOL-RAY-006', 'Gafas de Sol Ray-Ban Wayfarer Classic Polarized', 'Ray-Ban', 'Lentes de sol icónicos con cristales verdes G-15 polarizados y protección total UV400', 520.00, 20, 1, SYSDATE - 30, (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'GAFAS DE SOL') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PRODUCTO WHERE codigo = 'SOL-RAY-006');

INSERT INTO PRODUCTO (codigo, nombre, marca, descripcion, precio, stock, estado, fecha_registro, id_categoria)
SELECT 'SOL-OAK-007', 'Gafas Deportivas Oakley Radar EV Path Prizm Road', 'Oakley', 'Gafas envolventes de alto rendimiento para ciclistas y corredores con óptica Prizm', 590.00, 12, 1, SYSDATE - 30, (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'GAFAS DE SOL') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PRODUCTO WHERE codigo = 'SOL-OAK-007');

-- Accesorios y Soluciones
INSERT INTO PRODUCTO (codigo, nombre, marca, descripcion, precio, stock, estado, fecha_registro, id_categoria)
SELECT 'ACC-EST-008', 'Estuche Rígido GeoVision con Cierre Magnético', 'GeoVision', 'Estuche protector térmico antigolpes forrado en gamuza suave', 35.00, 80, 1, SYSDATE - 25, (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'ACCESORIOS Y REPUESTOS') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PRODUCTO WHERE codigo = 'ACC-EST-008');

INSERT INTO PRODUCTO (codigo, nombre, marca, descripcion, precio, stock, estado, fecha_registro, id_categoria)
SELECT 'ACC-PAN-009', 'Paño Microfibra Antiestático de Alta Densidad', 'GeoVision', 'Microfibra lavable para cristales con tratamiento antirreflejo y pantallas', 12.00, 150, 1, SYSDATE - 25, (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'ACCESORIOS Y REPUESTOS') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PRODUCTO WHERE codigo = 'ACC-PAN-009');

INSERT INTO PRODUCTO (codigo, nombre, marca, descripcion, precio, stock, estado, fecha_registro, id_categoria)
SELECT 'SOL-LMP-010', 'Solución Limpiadora Antirreflejo Spray 60ml', 'CleanVision', 'Spray desengrasante antiempañante con fórmula biodegradable', 25.00, 90, 1, SYSDATE - 20, (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'SOLUCIONES Y GOTAS') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PRODUCTO WHERE codigo = 'SOL-LMP-010');

INSERT INTO PRODUCTO (codigo, nombre, marca, descripcion, precio, stock, estado, fecha_registro, id_categoria)
SELECT 'SOL-SYS-011', 'Lágrimas Artificiales Systane Ultra Gotas 10ml', 'Alcon', 'Gotas lubricantes de acción rápida para resequedad e irritación ocular', 45.00, 45, 1, SYSDATE - 20, (SELECT id_categoria FROM CATEGORIA WHERE nombre = 'SOLUCIONES Y GOTAS') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PRODUCTO WHERE codigo = 'SOL-SYS-011');

-- Movimientos Iniciales de Kardex (Motivo 1: Entrada por compra)
INSERT INTO MOVIMIENTO_STOCK_LOG (fecha_registro, cantidad, stock_previo, stock_posterior, motivo, observaciones, id_producto, id_usuario)
SELECT SYSDATE - 65, p.stock, 0, p.stock, 1, 'Apertura e ingreso de inventario físico inicial', p.id_producto, 
       (SELECT id_usuario FROM USUARIO WHERE nombre = 'admin')
FROM PRODUCTO p
WHERE NOT EXISTS (SELECT 1 FROM MOVIMIENTO_STOCK_LOG m WHERE m.id_producto = p.id_producto AND m.motivo = 1);

-- ----------------------------------------------------------------------------
-- 7. SERVICIOS CLINICOS Y MANTENIMIENTO
-- ----------------------------------------------------------------------------
PROMPT [7/15] Insertando Servicios Clinicos y Optometricos...
INSERT INTO SERVICIO (nombre, descripcion, precio, estado)
SELECT 'EXAMEN DE REFRACCION COMPUTARIZADA', 'Evaluación refractiva con autorrefractómetro computarizado y retinoscopía estática', 35.00, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM SERVICIO WHERE nombre = 'EXAMEN DE REFRACCION COMPUTARIZADA');

INSERT INTO SERVICIO (nombre, descripcion, precio, estado)
SELECT 'MEDIDA DE VISTA Y FONDO DE OJO', 'Evaluación integral clínica de fondo de ojo, biomicroscopía y descarte de patologías', 50.00, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM SERVICIO WHERE nombre = 'MEDIDA DE VISTA Y FONDO DE OJO');

INSERT INTO SERVICIO (nombre, descripcion, precio, estado)
SELECT 'MANTENIMIENTO Y CALIBRACION DE MONTURAS', 'Limpieza profunda ultrasónica, cambio de plaquetas de silicona y calibrado anatómico', 25.00, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM SERVICIO WHERE nombre = 'MANTENIMIENTO Y CALIBRACION DE MONTURAS');

INSERT INTO SERVICIO (nombre, descripcion, precio, estado)
SELECT 'ADAPTACION DE LENTES DE CONTACTO', 'Prueba clínica de adaptación lagrimal, parámetros corneales y sesión de adiestramiento', 60.00, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM SERVICIO WHERE nombre = 'ADAPTACION DE LENTES DE CONTACTO');

INSERT INTO SERVICIO (nombre, descripcion, precio, estado)
SELECT 'CONFECCION Y MONTAJE DE LENTES', 'Confección, biselado y montaje de lentes oftálmicos bajo receta médica', 200.00, 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM SERVICIO WHERE nombre = 'CONFECCION Y MONTAJE DE LENTES');

-- ----------------------------------------------------------------------------
-- 8. LABORATORIOS EXTERNOS Y TALLER
-- ----------------------------------------------------------------------------
PROMPT [8/15] Insertando Laboratorios Oftalmicos...
INSERT INTO LABORATORIO (nombre, ruc, telefono, direccion, correo, estado)
SELECT 'ESSILOR PERU S.A.', '20100234567', '+51 1 614-2000', 'Av. República de Panamá 3505, San Isidro, Lima', 'pedidos@essilor.com.pe', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM LABORATORIO WHERE ruc = '20100234567');

INSERT INTO LABORATORIO (nombre, ruc, telefono, direccion, correo, estado)
SELECT 'LABORATORIO OPTICO RODENSTOCK PERU', '20509876543', '+51 1 221-4560', 'Av. Aramburú 850, Surquillo, Lima', 'laboratorio@rodenstock.pe', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM LABORATORIO WHERE ruc = '20509876543');

INSERT INTO LABORATORIO (nombre, ruc, telefono, direccion, correo, estado)
SELECT 'HOYA LENS DEL PERU S.A.C.', '20601239874', '+51 1 719-8800', 'Av. Javier Prado Este 4200, Surco, Lima', 'contacto@hoyalens.pe', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM LABORATORIO WHERE ruc = '20601239874');

INSERT INTO LABORATORIO (nombre, ruc, telefono, direccion, correo, estado)
SELECT 'TALLER CENTRAL BISELADO GEOVISION', '20608945123', '+51 1 445-8921', 'Av. Javier Prado Este 2450 Int. 2, San Borja, Lima', 'taller@geovision.pe', 1 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM LABORATORIO WHERE ruc = '20608945123');

COMMIT;

-- ----------------------------------------------------------------------------
-- 9. FLUJO CLINICO COMPLETO: MEDIDAS, RECETAS, LENTES Y ORDENES DE TRABAJO
-- ----------------------------------------------------------------------------
PROMPT [9/15] Insertando Flujos Optometricos y de Fabricacion...

DECLARE
    v_pac_jorge      NUMBER;
    v_pac_maria      NUMBER;
    v_pac_andres     NUMBER;
    v_pac_patricia   NUMBER;
    v_emp_elena      NUMBER;

    v_mat_policarb   NUMBER;
    v_mat_resina     NUMBER;
    v_mat_alto_ind   NUMBER;

    v_trat_uv        NUMBER;
    v_trat_ar        NUMBER;
    v_trat_blue      NUMBER;
    v_trat_foto      NUMBER;

    v_prod_ray       NUMBER;
    v_prod_oak       NUMBER;
    v_prod_sil       NUMBER;
    v_prod_vog       NUMBER;

    v_lab_taller     NUMBER;
    v_lab_essilor    NUMBER;
    v_lab_rodenstock NUMBER;

    -- Variables temporales para enlaces
    v_od1 NUMBER; v_oi1 NUMBER; v_rec1 NUMBER; v_med1 NUMBER; v_len1 NUMBER; v_ot1 NUMBER; v_env1 NUMBER;
    v_od2 NUMBER; v_oi2 NUMBER; v_rec2 NUMBER; v_med2 NUMBER; v_len2 NUMBER; v_ot2 NUMBER; v_env2 NUMBER;
    v_od3 NUMBER; v_oi3 NUMBER; v_rec3 NUMBER; v_med3 NUMBER; v_len3 NUMBER; v_ot3 NUMBER; v_env3 NUMBER;
    v_od4 NUMBER; v_oi4 NUMBER; v_rec4 NUMBER; v_med4 NUMBER; v_len4 NUMBER; v_ot4 NUMBER;

BEGIN
    -- Obtener IDs base
    SELECT id_persona INTO v_pac_jorge FROM PERSONA WHERE numero_documento = '41258963';
    SELECT id_persona INTO v_pac_maria FROM PERSONA WHERE numero_documento = '73849201';
    SELECT id_persona INTO v_pac_andres FROM PERSONA WHERE numero_documento = '10472938';
    SELECT id_persona INTO v_pac_patricia FROM PERSONA WHERE numero_documento = '76543210';
    SELECT id_persona INTO v_emp_elena FROM PERSONA WHERE numero_documento = '45892134';

    SELECT id_material_cristal INTO v_mat_policarb FROM MATERIAL_CRISTAL WHERE nombre = 'POLICARBONATO';
    SELECT id_material_cristal INTO v_mat_resina FROM MATERIAL_CRISTAL WHERE nombre = 'RESINA';
    SELECT id_material_cristal INTO v_mat_alto_ind FROM MATERIAL_CRISTAL WHERE nombre = 'ALTO INDICE';

    SELECT id_tratamiento_cristal INTO v_trat_uv FROM TRATAMIENTO_CRISTAL WHERE nombre = 'FILTRO UV';
    SELECT id_tratamiento_cristal INTO v_trat_ar FROM TRATAMIENTO_CRISTAL WHERE nombre = 'ANTIRREFLEJO';
    SELECT id_tratamiento_cristal INTO v_trat_blue FROM TRATAMIENTO_CRISTAL WHERE nombre = 'BLUE DEFENSE';
    SELECT id_tratamiento_cristal INTO v_trat_foto FROM TRATAMIENTO_CRISTAL WHERE nombre = 'FOTOCROMATICO';

    SELECT id_producto INTO v_prod_ray FROM PRODUCTO WHERE codigo = 'MON-RAY-001';
    SELECT id_producto INTO v_prod_oak FROM PRODUCTO WHERE codigo = 'MON-OAK-002';
    SELECT id_producto INTO v_prod_sil FROM PRODUCTO WHERE codigo = 'MON-SIL-003';
    SELECT id_producto INTO v_prod_vog FROM PRODUCTO WHERE codigo = 'MON-VOG-004';

    SELECT id_laboratorio INTO v_lab_taller FROM LABORATORIO WHERE ruc = '20608945123';
    SELECT id_laboratorio INTO v_lab_essilor FROM LABORATORIO WHERE ruc = '20100234567';
    SELECT id_laboratorio INTO v_lab_rodenstock FROM LABORATORIO WHERE ruc = '20509876543';

    -- ========================================================================
    -- CASO 1: Paciente Jorge Valdivia (Miopia Leve - Policarbonato AR - Listo para Entrega)
    -- ========================================================================
    INSERT INTO MEDIDA_OJO (agudeza_visual, esfera, cilindro, eje, adicion)
    VALUES ('20/20', -1.75, -0.50, 15, 0.00) RETURNING id_medida_ojo INTO v_od1;

    INSERT INTO MEDIDA_OJO (agudeza_visual, esfera, cilindro, eje, adicion)
    VALUES ('20/25', -1.50, -0.75, 165, 0.00) RETURNING id_medida_ojo INTO v_oi1;

    INSERT INTO RECETA (fecha_registro, es_externo, id_paciente, id_empleado)
    VALUES (SYSDATE - 12, 0, v_pac_jorge, v_emp_elena) RETURNING id_receta INTO v_rec1;

    INSERT INTO MEDIDA (tipo_medida, distancia_pupilar, prisma, observaciones, id_receta, id_medida_ojo_derecha, id_medida_ojo_izquierda)
    VALUES (1, 63.00, 0.00, 'Uso permanente para trabajo y conduccion', v_rec1, v_od1, v_oi1)
    RETURNING id_medida INTO v_med1;

    INSERT INTO LENTE (sub_total, id_receta, id_material_cristal, id_producto)
    VALUES (620.00, v_rec1, v_mat_policarb, v_prod_ray)
    RETURNING id_lente INTO v_len1;

    INSERT INTO LENTE_MEDIDA (id_lente, id_medida) VALUES (v_len1, v_med1);
    INSERT INTO LENTE_TRATAMIENTO_CRISTAL (id_lente, id_tratamiento_cristal) VALUES (v_len1, v_trat_uv);
    INSERT INTO LENTE_TRATAMIENTO_CRISTAL (id_lente, id_tratamiento_cristal) VALUES (v_len1, v_trat_ar);

    -- Orden de trabajo: 6 = LISTO_ENTREGA
    INSERT INTO ORDEN_TRABAJO (fecha_registro, fecha_entrega, fecha_notificacion, estado, descripcion, id_lente)
    VALUES (SYSDATE - 10, SYSDATE - 1, SYSDATE - 1, 6, 'Biselado y montaje finalizado conforme en taller interno', v_len1)
    RETURNING id_orden_trabajo INTO v_ot1;

    INSERT INTO ENVIO_LABORATORIO (fecha_envio, fecha_recepcion_estimada, fecha_recepcion, fecha_revision, tolerancia, aprobado, observaciones, id_orden_trabajo, id_laboratorio)
    VALUES (SYSDATE - 10, SYSDATE - 8, SYSDATE - 8, SYSDATE - 8, 0.25, 1, 'Control de calidad aprobado sin observaciones tecnicas', v_ot1, v_lab_taller)
    RETURNING id_envio_laboratorio INTO v_env1;

    -- ========================================================================
    -- CASO 2: Paciente Maria Gomez (Miopia Moderada - Resina Blue Defense - Recibido de Lab)
    -- ========================================================================
    INSERT INTO MEDIDA_OJO (agudeza_visual, esfera, cilindro, eje, adicion)
    VALUES ('20/20', -2.50, -0.75, 90, 0.00) RETURNING id_medida_ojo INTO v_od2;

    INSERT INTO MEDIDA_OJO (agudeza_visual, esfera, cilindro, eje, adicion)
    VALUES ('20/20', -2.25, -0.50, 85, 0.00) RETURNING id_medida_ojo INTO v_oi2;

    INSERT INTO RECETA (fecha_registro, es_externo, id_paciente, id_empleado)
    VALUES (SYSDATE - 7, 0, v_pac_maria, v_emp_elena) RETURNING id_receta INTO v_rec2;

    INSERT INTO MEDIDA (tipo_medida, distancia_pupilar, prisma, observaciones, id_receta, id_medida_ojo_derecha, id_medida_ojo_izquierda)
    VALUES (1, 61.50, 0.00, 'Proteccion especial para programacion y pantallas LED', v_rec2, v_od2, v_oi2)
    RETURNING id_medida INTO v_med2;

    INSERT INTO LENTE (sub_total, id_receta, id_material_cristal, id_producto)
    VALUES (580.00, v_rec2, v_mat_resina, v_prod_oak)
    RETURNING id_lente INTO v_len2;

    INSERT INTO LENTE_MEDIDA (id_lente, id_medida) VALUES (v_len2, v_med2);
    INSERT INTO LENTE_TRATAMIENTO_CRISTAL (id_lente, id_tratamiento_cristal) VALUES (v_len2, v_trat_uv);
    INSERT INTO LENTE_TRATAMIENTO_CRISTAL (id_lente, id_tratamiento_cristal) VALUES (v_len2, v_trat_ar);
    INSERT INTO LENTE_TRATAMIENTO_CRISTAL (id_lente, id_tratamiento_cristal) VALUES (v_len2, v_trat_blue);

    -- Orden de trabajo: 3 = RECIBIDO_LABORATORIO
    INSERT INTO ORDEN_TRABAJO (fecha_registro, fecha_entrega, fecha_notificacion, estado, descripcion, id_lente)
    VALUES (SYSDATE - 5, SYSDATE + 2, NULL, 3, 'Recibido de Essilor Peru. En espera de lensometria de calidad', v_len2)
    RETURNING id_orden_trabajo INTO v_ot2;

    INSERT INTO ENVIO_LABORATORIO (fecha_envio, fecha_recepcion_estimada, fecha_recepcion, fecha_revision, tolerancia, aprobado, observaciones, id_orden_trabajo, id_laboratorio)
    VALUES (SYSDATE - 5, SYSDATE - 1, SYSDATE, NULL, 0.25, NULL, 'Recepcion de paquete sellado de laboratorio', v_ot2, v_lab_essilor)
    RETURNING id_envio_laboratorio INTO v_env2;

    -- ========================================================================
    -- CASO 3: Paciente Andres Cornejo (Presbicia Multifocal - Alto Indice - Enviado a Lab)
    -- ========================================================================
    INSERT INTO MEDIDA_OJO (agudeza_visual, esfera, cilindro, eje, adicion)
    VALUES ('20/25', 1.25, -0.50, 180, 2.00) RETURNING id_medida_ojo INTO v_od3;

    INSERT INTO MEDIDA_OJO (agudeza_visual, esfera, cilindro, eje, adicion)
    VALUES ('20/25', 1.50, -0.50, 10, 2.00) RETURNING id_medida_ojo INTO v_oi3;

    INSERT INTO RECETA (fecha_registro, es_externo, id_paciente, id_empleado)
    VALUES (SYSDATE - 3, 0, v_pac_andres, v_emp_elena) RETURNING id_receta INTO v_rec3;

    INSERT INTO MEDIDA (tipo_medida, distancia_pupilar, prisma, observaciones, id_receta, id_medida_ojo_derecha, id_medida_ojo_izquierda)
    VALUES (1, 64.00, 0.00, 'Lentes progresivos ergonomicos campo visual amplio', v_rec3, v_od3, v_oi3)
    RETURNING id_medida INTO v_med3;

    INSERT INTO LENTE (sub_total, id_receta, id_material_cristal, id_producto)
    VALUES (950.00, v_rec3, v_mat_alto_ind, v_prod_sil)
    RETURNING id_lente INTO v_len3;

    INSERT INTO LENTE_MEDIDA (id_lente, id_medida) VALUES (v_len3, v_med3);
    INSERT INTO LENTE_TRATAMIENTO_CRISTAL (id_lente, id_tratamiento_cristal) VALUES (v_len3, v_trat_uv);
    INSERT INTO LENTE_TRATAMIENTO_CRISTAL (id_lente, id_tratamiento_cristal) VALUES (v_len3, v_trat_ar);
    INSERT INTO LENTE_TRATAMIENTO_CRISTAL (id_lente, id_tratamiento_cristal) VALUES (v_len3, v_trat_foto);

    -- Orden de trabajo: 2 = ENVIADO_LABORATORIO
    INSERT INTO ORDEN_TRABAJO (fecha_registro, fecha_entrega, fecha_notificacion, estado, descripcion, id_lente)
    VALUES (SYSDATE - 2, SYSDATE + 5, NULL, 2, 'Despachado a Rodenstock para tallado FreeForm digital', v_len3)
    RETURNING id_orden_trabajo INTO v_ot3;

    INSERT INTO ENVIO_LABORATORIO (fecha_envio, fecha_recepcion_estimada, fecha_recepcion, fecha_revision, tolerancia, aprobado, observaciones, id_orden_trabajo, id_laboratorio)
    VALUES (SYSDATE - 2, SYSDATE + 3, NULL, NULL, 0.20, NULL, 'Guia de remision No 001-9842', v_ot3, v_lab_rodenstock)
    RETURNING id_envio_laboratorio INTO v_env3;

    -- ========================================================================
    -- CASO 4: Paciente Patricia Morales (Astigmatismo - Pendiente de Procesar)
    -- ========================================================================
    INSERT INTO MEDIDA_OJO (agudeza_visual, esfera, cilindro, eje, adicion)
    VALUES ('20/30', -0.75, -1.25, 45, 0.00) RETURNING id_medida_ojo INTO v_od4;

    INSERT INTO MEDIDA_OJO (agudeza_visual, esfera, cilindro, eje, adicion)
    VALUES ('20/25', -0.50, -1.00, 135, 0.00) RETURNING id_medida_ojo INTO v_oi4;

    INSERT INTO RECETA (fecha_registro, es_externo, id_paciente, id_empleado)
    VALUES (SYSDATE - 1, 0, v_pac_patricia, v_emp_elena) RETURNING id_receta INTO v_rec4;

    INSERT INTO MEDIDA (tipo_medida, distancia_pupilar, prisma, observaciones, id_receta, id_medida_ojo_derecha, id_medida_ojo_izquierda)
    VALUES (1, 62.00, 0.00, 'Filtro Blue para descanso ocular', v_rec4, v_od4, v_oi4)
    RETURNING id_medida INTO v_med4;

    INSERT INTO LENTE (sub_total, id_receta, id_material_cristal, id_producto)
    VALUES (460.00, v_rec4, v_mat_policarb, v_prod_vog)
    RETURNING id_lente INTO v_len4;

    INSERT INTO LENTE_MEDIDA (id_lente, id_medida) VALUES (v_len4, v_med4);
    INSERT INTO LENTE_TRATAMIENTO_CRISTAL (id_lente, id_tratamiento_cristal) VALUES (v_len4, v_trat_uv);
    INSERT INTO LENTE_TRATAMIENTO_CRISTAL (id_lente, id_tratamiento_cristal) VALUES (v_len4, v_trat_blue);

    -- Orden de trabajo: 1 = PENDIENTE
    INSERT INTO ORDEN_TRABAJO (fecha_registro, fecha_entrega, fecha_notificacion, estado, descripcion, id_lente)
    VALUES (SYSDATE - 1, SYSDATE + 4, NULL, 1, 'Orden de trabajo recién registrada en sucursal', v_len4);

END;
/
COMMIT;

-- ----------------------------------------------------------------------------
-- 10. VENTAS, PRODUCTO_VENDIDO, SERVICIO_VENDIDO Y PAGOS
-- ----------------------------------------------------------------------------
PROMPT [10/15] Insertando Ventas, Facturacion y Pagos...

DECLARE
    v_pac_jorge    NUMBER;
    v_pac_maria    NUMBER;
    v_pac_andres   NUMBER;
    v_pac_patricia NUMBER;
    v_emp_vendedor NUMBER;

    v_rec_jorge    NUMBER;
    v_rec_maria    NUMBER;

    v_srv_refrac   NUMBER;
    v_srv_fondo    NUMBER;

    v_prod_ray     NUMBER;
    v_prod_oak     NUMBER;
    v_prod_sil     NUMBER;
    v_prod_sol_ray NUMBER;
    v_prod_estuche NUMBER;
    v_prod_pano    NUMBER;
    v_prod_soluc   NUMBER;
    v_prod_gotas   NUMBER;

    v_vta1 NUMBER;
    v_vta2 NUMBER;
    v_vta3 NUMBER;
    v_vta4 NUMBER;

BEGIN
    -- Clientes y Vendedor
    SELECT id_persona INTO v_pac_jorge FROM PERSONA WHERE numero_documento = '41258963';
    SELECT id_persona INTO v_pac_maria FROM PERSONA WHERE numero_documento = '73849201';
    SELECT id_persona INTO v_pac_andres FROM PERSONA WHERE numero_documento = '10472938';
    SELECT id_persona INTO v_pac_patricia FROM PERSONA WHERE numero_documento = '76543210';
    SELECT id_persona INTO v_emp_vendedor FROM PERSONA WHERE numero_documento = '70961428';

    SELECT MAX(id_receta) INTO v_rec_jorge FROM RECETA WHERE id_paciente = v_pac_jorge;
    SELECT MAX(id_receta) INTO v_rec_maria FROM RECETA WHERE id_paciente = v_pac_maria;

    SELECT id_servicio INTO v_srv_refrac FROM SERVICIO WHERE nombre = 'EXAMEN DE REFRACCION COMPUTARIZADA';
    SELECT id_servicio INTO v_srv_fondo FROM SERVICIO WHERE nombre = 'MEDIDA DE VISTA Y FONDO DE OJO';

    SELECT id_producto INTO v_prod_ray FROM PRODUCTO WHERE codigo = 'MON-RAY-001';
    SELECT id_producto INTO v_prod_oak FROM PRODUCTO WHERE codigo = 'MON-OAK-002';
    SELECT id_producto INTO v_prod_sil FROM PRODUCTO WHERE codigo = 'MON-SIL-003';
    SELECT id_producto INTO v_prod_sol_ray FROM PRODUCTO WHERE codigo = 'SOL-RAY-006';
    SELECT id_producto INTO v_prod_estuche FROM PRODUCTO WHERE codigo = 'ACC-EST-008';
    SELECT id_producto INTO v_prod_pano FROM PRODUCTO WHERE codigo = 'ACC-PAN-009';
    SELECT id_producto INTO v_prod_soluc FROM PRODUCTO WHERE codigo = 'SOL-LMP-010';
    SELECT id_producto INTO v_prod_gotas FROM PRODUCTO WHERE codigo = 'SOL-SYS-011';

    -- ========================================================================
    -- VENTA 1: BOLETA PAGADA TOTALMENTE (S/ 550.00)
    -- Montura Ray-Ban (480) + Estuche (35) + Examen Refraccion (35)
    -- Pagos: Yape 300.00 + Tarjeta Debito 250.00 = 550.00 (Total pagado)
    -- ========================================================================
    INSERT INTO VENTA (fecha_registro, total, estado, numero_comprobante, tipo_comprobante, id_empleado, id_paciente, id_receta)
    VALUES (SYSDATE - 10, 550.00, 2, 'B001-0001001', 1, v_emp_vendedor, v_pac_jorge, v_rec_jorge)
    RETURNING id_venta INTO v_vta1;

    INSERT INTO PRODUCTO_VENDIDO (cantidad, precio_unit, sub_total, id_venta, id_producto)
    VALUES (1, 480.00, 480.00, v_vta1, v_prod_ray);

    INSERT INTO PRODUCTO_VENDIDO (cantidad, precio_unit, sub_total, id_venta, id_producto)
    VALUES (1, 35.00, 35.00, v_vta1, v_prod_estuche);

    INSERT INTO SERVICIO_VENDIDO (precio, id_venta, id_servicio)
    VALUES (35.00, v_vta1, v_srv_refrac);

    INSERT INTO PAGO (fecha_registro, numero_operacion, monto, metodo_pago, id_venta)
    VALUES (SYSDATE - 10, 'YAPE-8912301', 300.00, 5, v_vta1);

    INSERT INTO PAGO (fecha_registro, numero_operacion, monto, metodo_pago, id_venta)
    VALUES (SYSDATE - 10, 'POS-048912', 250.00, 2, v_vta1);

    -- ========================================================================
    -- VENTA 2: BOLETA PENDIENTE CON ADELANTO (S/ 480.00, Saldo S/ 180.00)
    -- Montura Oakley (420) + Solucion Limpiadora (25) + Examen (35) = 480.00
    -- Pagos: Efectivo 300.00 (Adelanto del 62.5%) -> Estado: 1 (PENDIENTE)
    -- ========================================================================
    INSERT INTO VENTA (fecha_registro, total, estado, numero_comprobante, tipo_comprobante, id_empleado, id_paciente, id_receta)
    VALUES (SYSDATE - 5, 480.00, 1, 'B001-0001002', 1, v_emp_vendedor, v_pac_maria, v_rec_maria)
    RETURNING id_venta INTO v_vta2;

    INSERT INTO PRODUCTO_VENDIDO (cantidad, precio_unit, sub_total, id_venta, id_producto)
    VALUES (1, 420.00, 420.00, v_vta2, v_prod_oak);

    INSERT INTO PRODUCTO_VENDIDO (cantidad, precio_unit, sub_total, id_venta, id_producto)
    VALUES (1, 25.00, 25.00, v_vta2, v_prod_soluc);

    INSERT INTO SERVICIO_VENDIDO (precio, id_venta, id_servicio)
    VALUES (35.00, v_vta2, v_srv_refrac);

    INSERT INTO PAGO (fecha_registro, numero_operacion, monto, metodo_pago, id_venta)
    VALUES (SYSDATE - 5, 'EFECTIVO-REC-01', 300.00, 1, v_vta2);

    -- ========================================================================
    -- VENTA 3: BOLETA PAGADA AL CONTADO (S/ 557.00)
    -- Gafas de Sol Ray-Ban (520) + Paño Microfibra (12) + Solucion (25)
    -- Pago: Tarjeta Credito 557.00 -> Estado: 2 (PAGADA)
    -- ========================================================================
    INSERT INTO VENTA (fecha_registro, total, estado, numero_comprobante, tipo_comprobante, id_empleado, id_paciente, id_receta)
    VALUES (SYSDATE - 2, 557.00, 2, 'B001-0001003', 1, v_emp_vendedor, v_pac_andres, NULL)
    RETURNING id_venta INTO v_vta3;

    INSERT INTO PRODUCTO_VENDIDO (cantidad, precio_unit, sub_total, id_venta, id_producto)
    VALUES (1, 520.00, 520.00, v_vta3, v_prod_sol_ray);

    INSERT INTO PRODUCTO_VENDIDO (cantidad, precio_unit, sub_total, id_venta, id_producto)
    VALUES (1, 12.00, 12.00, v_vta3, v_prod_pano);

    INSERT INTO PRODUCTO_VENDIDO (cantidad, precio_unit, sub_total, id_venta, id_producto)
    VALUES (1, 25.00, 25.00, v_vta3, v_prod_soluc);

    INSERT INTO PAGO (fecha_registro, numero_operacion, monto, metodo_pago, id_venta)
    VALUES (SYSDATE - 2, 'VISA-339012', 557.00, 3, v_vta3);

    -- ========================================================================
    -- VENTA 4: FACTURA PENDIENTE SIN PAGO (S/ 790.00)
    -- Montura Silhouette (650) + Gotas Systane (90 = 2x45) + Fondo Ojo (50)
    -- Pagos: Ninguno registrado todavia -> Estado: 1 (PENDIENTE)
    -- ========================================================================
    INSERT INTO VENTA (fecha_registro, total, estado, numero_comprobante, tipo_comprobante, id_empleado, id_paciente, id_receta)
    VALUES (SYSDATE - 1, 790.00, 1, 'F001-0000501', 2, v_emp_vendedor, v_pac_patricia, NULL)
    RETURNING id_venta INTO v_vta4;

    INSERT INTO PRODUCTO_VENDIDO (cantidad, precio_unit, sub_total, id_venta, id_producto)
    VALUES (1, 650.00, 650.00, v_vta4, v_prod_sil);

    INSERT INTO PRODUCTO_VENDIDO (cantidad, precio_unit, sub_total, id_venta, id_producto)
    VALUES (2, 45.00, 90.00, v_vta4, v_prod_gotas);

    INSERT INTO SERVICIO_VENDIDO (precio, id_venta, id_servicio)
    VALUES (50.00, v_vta4, v_srv_fondo);

END;
/
COMMIT;

-- ----------------------------------------------------------------------------
-- 11. AUDITORIA Y LOGS DEL SISTEMA (USUARIO_LOG, PACIENTE_LOG, EMPLEADO_LOG)
-- ----------------------------------------------------------------------------
PROMPT [11/15] Insertando Registros de Auditoria y Logs...

INSERT INTO USUARIO_LOG (fecha_log, tipo_log, id_usuario)
SELECT SYSDATE - 10, 1, (SELECT id_usuario FROM USUARIO WHERE nombre = 'admin') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM USUARIO_LOG WHERE id_usuario = (SELECT id_usuario FROM USUARIO WHERE nombre = 'admin') AND tipo_log = 1 AND fecha_log < SYSDATE - 9);

INSERT INTO USUARIO_LOG (fecha_log, tipo_log, id_usuario)
SELECT SYSDATE - 7, 1, (SELECT id_usuario FROM USUARIO WHERE nombre = 'optometra') FROM DUAL;

INSERT INTO USUARIO_LOG (fecha_log, tipo_log, id_usuario)
SELECT SYSDATE - 5, 1, (SELECT id_usuario FROM USUARIO WHERE nombre = 'vendedor') FROM DUAL;

INSERT INTO USUARIO_LOG (fecha_log, tipo_log, id_usuario)
SELECT SYSDATE - 2, 1, (SELECT id_usuario FROM USUARIO WHERE nombre = 'tecnico') FROM DUAL;

INSERT INTO USUARIO_LOG (fecha_log, tipo_log, id_usuario)
SELECT SYSDATE - 1, 1, (SELECT id_usuario FROM USUARIO WHERE nombre = 'recepcion') FROM DUAL;

-- Paciente Logs
INSERT INTO PACIENTE_LOG (fecha_log, id_usuario, id_persona)
SELECT SYSDATE - 12, (SELECT id_usuario FROM USUARIO WHERE nombre = 'recepcion'), (SELECT id_persona FROM PERSONA WHERE numero_documento = '41258963') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PACIENTE_LOG WHERE id_persona = (SELECT id_persona FROM PERSONA WHERE numero_documento = '41258963'));

INSERT INTO PACIENTE_LOG (fecha_log, id_usuario, id_persona)
SELECT SYSDATE - 8, (SELECT id_usuario FROM USUARIO WHERE nombre = 'recepcion'), (SELECT id_persona FROM PERSONA WHERE numero_documento = '73849201') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PACIENTE_LOG WHERE id_persona = (SELECT id_persona FROM PERSONA WHERE numero_documento = '73849201'));

INSERT INTO PACIENTE_LOG (fecha_log, id_usuario, id_persona)
SELECT SYSDATE - 4, (SELECT id_usuario FROM USUARIO WHERE nombre = 'recepcion'), (SELECT id_persona FROM PERSONA WHERE numero_documento = '10472938') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PACIENTE_LOG WHERE id_persona = (SELECT id_persona FROM PERSONA WHERE numero_documento = '10472938'));

INSERT INTO PACIENTE_LOG (fecha_log, id_usuario, id_persona)
SELECT SYSDATE - 2, (SELECT id_usuario FROM USUARIO WHERE nombre = 'recepcion'), (SELECT id_persona FROM PERSONA WHERE numero_documento = '76543210') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM PACIENTE_LOG WHERE id_persona = (SELECT id_persona FROM PERSONA WHERE numero_documento = '76543210'));

-- Empleado Logs
INSERT INTO EMPLEADO_LOG (fecha_log, id_usuario, id_persona)
SELECT TO_DATE('2023-01-15', 'YYYY-MM-DD'), (SELECT id_usuario FROM USUARIO WHERE nombre = 'admin'), (SELECT id_persona FROM PERSONA WHERE numero_documento = '45892134') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM EMPLEADO_LOG WHERE id_persona = (SELECT id_persona FROM PERSONA WHERE numero_documento = '45892134'));

INSERT INTO EMPLEADO_LOG (fecha_log, id_usuario, id_persona)
SELECT TO_DATE('2023-03-01', 'YYYY-MM-DD'), (SELECT id_usuario FROM USUARIO WHERE nombre = 'admin'), (SELECT id_persona FROM PERSONA WHERE numero_documento = '70961428') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM EMPLEADO_LOG WHERE id_persona = (SELECT id_persona FROM PERSONA WHERE numero_documento = '70961428'));

INSERT INTO EMPLEADO_LOG (fecha_log, id_usuario, id_persona)
SELECT TO_DATE('2023-06-01', 'YYYY-MM-DD'), (SELECT id_usuario FROM USUARIO WHERE nombre = 'admin'), (SELECT id_persona FROM PERSONA WHERE numero_documento = '48123091') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM EMPLEADO_LOG WHERE id_persona = (SELECT id_persona FROM PERSONA WHERE numero_documento = '48123091'));

INSERT INTO EMPLEADO_LOG (fecha_log, id_usuario, id_persona)
SELECT TO_DATE('2023-08-15', 'YYYY-MM-DD'), (SELECT id_usuario FROM USUARIO WHERE nombre = 'admin'), (SELECT id_persona FROM PERSONA WHERE numero_documento = '72341092') FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM EMPLEADO_LOG WHERE id_persona = (SELECT id_persona FROM PERSONA WHERE numero_documento = '72341092'));

COMMIT;

PROMPT >>> CARGA DE DATOS DE PRUEBA COMPLETADA CON EXITO.

# Elaboración de Orden de Trabajo

Caleb David Asmat Briceño · GeoVision · Administración de Base de Datos II · Oracle Database 19c

## Procedimientos almacenados

| Objeto | Descripción | Referencia |
| --- | --- | --- |
| `SP_AP_COMPARAR_MEDIDA` | Procedimiento original. Compara una medida de referencia con los valores de lensometría recibidos en JSON y devuelve un indicador de conformidad y el detalle por campo. | CUS09 · RF-61 y RF-62 |
| `SP_LISTAR_VENTAS_PENDIENTES_OT` | Abre un cursor con las ventas elegibles que tienen lentes sin OT. Permite filtrar por nombre del paciente, documento o comprobante. Un filtro nulo devuelve todas las coincidencias elegibles. | CUS08 · RF-55 |
| `SP_REGISTRAR_RECEPCION_OT` | Recibe el identificador del último envío pendiente, registra su fecha de recepción y cambia la OT de «Enviado a Laboratorio» (2) a «Recibido de Laboratorio» (3). Ambos cambios quedan en la misma transacción. | CUS08 · RF-59 |

El primer procedimiento calcula el resultado. El servicio de control de calidad del backend registra la lensometría, la revisión y el estado resultante. La recepción implementa el cambio de estado y fecha para la práctica SQL. La validación del operador, de la venta vinculada y el registro de constancia siguen en el flujo Java.

## Funciones y triggers

| Objeto | Descripción | Referencia |
| --- | --- | --- |
| `FN_DIFERENCIA_ANGULAR` | Calcula la menor distancia entre dos ejes válidos. Entre 5° y 175°, con período de 180°, devuelve 10°. Devuelve `NULL` si el período o los valores son inválidos. | CUS09 · RF-61 y RF-62 |
| `FN_ES_MEDIDA_APROBADA` | Invoca el comparador y devuelve `APROBADO`, `RECHAZADO` o un mensaje de error. | CUS09 · RF-61 y RF-62 |
| `TRG_VALIDAR_RANGO_EJE` | Permite ejes entre 0 y 180 grados inclusive y conserva la posibilidad de guardar un eje nulo. | CUS09 · RF-60 |
| `TRG_PROTEGER_MEDIDA_LABORATORIO` | Impide editar o eliminar una fila de `MEDIDA` ya vinculada al envío como resultado de lensometría. La regla actúa sobre la fila de `MEDIDA`. | CUS09 · RF-60 |

## Consultas y vistas

| Pieza | Descripción | Referencia |
| --- | --- | --- |
| Consulta 01 · Carga de trabajo | Agrupa por día de venta y cuenta ventas y lentes pendientes por separado. | CUS08 · RF-55 |
| Consulta 02 · Resumen logístico | Usa `RIGHT JOIN`, `GROUP BY` y `DECODE` para mostrar todos los laboratorios y sus envíos recibidos y pendientes. | CUS08 · RF-57 a RF-59 |
| Consulta 03 · Estado antes de entregar | Muestra estado, control, notificación, saldo y cumplimiento de las condiciones de entrega. | CUS09/CUS10/CUS11 · RF-60 a RF-63, RF-65 y RF-66 |
| `V_S_VENTAS_PENDIENTES_OT` | Identifica los lentes sin OT con venta vigente, pago positivo, receta con venta única y medidas vinculadas. «Pendiente de OT» se deriva de estos datos. | CUS08 · RF-55 |
| `V_S_DETALLE_ORDEN_TRABAJO` | Reúne el paciente, la receta, el material, la montura, los tratamientos y las medidas de referencia de una OT. Apoya la consulta de la ficha generada. | CUS08 · RF-56 |
| `V_S_SEGUIMIENTO_ORDEN_TRABAJO` | Muestra el estado de cada OT y su envío más reciente. Conserva las OT pendientes de envío. | CUS08/CUS09/CUS10/CUS11 · RF-57 a RF-63, RF-65 y RF-66 |

La constancia de notificación corresponde al RF-65. El envío del mensaje de WhatsApp (RF-64) corresponde a la aplicación. La numeración de esta ficha sigue la matriz compartida para la entrega.

## Administración de base de datos

| Pieza | Descripción | Referencia |
| --- | --- | --- |
| Particionamiento de `ENVIO_LABORATORIO_HIST` | Crea una copia de práctica para reportes. Agrupa los envíos anteriores a 2026 y crea particiones mensuales desde enero de 2026 según los datos cargados. Requiere soporte de Oracle Partitioning en el esquema de práctica. | RF-57 a RF-60 |
| Enmascaramiento de `PERSONA.numero_documento` | La vista `V_S_PENDIENTES_OT_MASCARADOS` muestra solo los últimos cuatro caracteres de documentos de más de cuatro caracteres, y oculta por completo los más cortos. La cuenta del reporte debe tener acceso a la vista enmascarada y carecer de acceso a las fuentes con el documento completo. | RF-55 |
| Auditoría de `ORDEN_TRABAJO` y `ENVIO_LABORATORIO` | Una política de auditoría unificada registra inserciones, actualizaciones y borrados. El registro permite consultar usuario de base de datos, fecha, objeto y código de resultado. | RF-56 a RF-63 y RF-65, más el registro de entrega |

- [Particionamiento por intervalo en Oracle 19c](https://docs.oracle.com/en/database/oracle/oracle-database/19/vldbg/partition-create-tables-indexes.html)
- [Políticas de auditoría unificada en Oracle 19c](https://docs.oracle.com/en/database/oracle/oracle-database/19/dbseg/syntax-creating-custom-unified-audit-policy.html)

## Demostración en el esquema de desarrollo

Ejecuta `01_objetos_ot.sql` después del DDL base de GeoVision. Consulta `USER_ERRORS` para comprobar la compilación y ejecuta `02_consultas_ot.sql` para ver los reportes. Ejecuta los bloques de `03_administracion_ot.sql` por separado según la edición y los privilegios disponibles. El bloque de enmascaramiento usa la primera vista como origen y la auditoría requiere los permisos indicados en el script.

```sql
-- SQL Developer en modo script, SQLcl o SQL*Plus.
VARIABLE ventas REFCURSOR
EXEC SP_LISTAR_VENTAS_PENDIENTES_OT(NULL, :ventas);
PRINT ventas

-- Resultado esperado: 10 y NULL, respectivamente.
SELECT FN_DIFERENCIA_ANGULAR(5, 175) AS diferencia,
       FN_DIFERENCIA_ANGULAR(5, 200) AS dato_invalido
FROM dual;

-- Localiza un envio de prueba por recibir. El seed incluye una OT en estado 2.
SELECT id_envio_laboratorio, id_orden_trabajo
FROM V_S_SEGUIMIENTO_ORDEN_TRABAJO
WHERE estado = 2 AND fecha_recepcion IS NULL;

-- Tras elegir el ID de la consulta anterior, el estado esperado es 3.
EXEC SP_REGISTRAR_RECEPCION_OT(&id_envio_prueba);
SELECT estado, fecha_recepcion, fecha_revision
FROM V_S_SEGUIMIENTO_ORDEN_TRABAJO
WHERE id_envio_laboratorio = &id_envio_prueba;
ROLLBACK;
```

Para probar el comparador, el JSON debe llevar los campos de `MEDIDA` en la raíz y las claves `OJO_DERECHO` y `OJO_IZQUIERDO` con los atributos de cada ojo. Copia los valores de una medida de estudio para probar conformidad y luego cambia un campo para probar rechazo. Usa los nombres de los atributos en mayúsculas y suministra `null` explícito para los valores ausentes de columnas opcionales.

Los scripts se revisaron contra el DDL y el código del backend. Queda pendiente compilarlos y ejecutarlos en Oracle 19c.

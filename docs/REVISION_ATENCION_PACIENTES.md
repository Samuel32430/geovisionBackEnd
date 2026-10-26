# Revision de pruebas y riesgos de produccion

Fecha: 2026-10-26. Alcance: atencion de pacientes y revision puntual de dependencias.
Sin commits, sin cambios de esquema Oracle y sin modificar implementaciones de otros modulos.

## Resultado ejecutado

Comando:

```powershell
.\mvnw.cmd -o '-Dtest=*Test,*Tests,!GeovisionBackEndApplicationTests' test
```

Resultado final: **178 pruebas, 0 fallos, 0 errores, 0 omitidas dentro de la seleccion**.
El test GeovisionBackEndApplicationTests se excluyo expresamente: no se dispone de una
conexion Oracle de prueba configurada en este entorno. No se certifica el arranque completo
con la base de datos de despliegue.

- 107 casos de contrato HTTP nuevos, para todos los controladores de pacientes.
- 11 pruebas nuevas con servicios Spring reales, validacion por proxy, JPA y transacciones.
- 60 pruebas anteriores, incluidas las suites de los otros modulos.

La base de integracion es H2 2.4.240 en memoria, en modo de compatibilidad Oracle.
Su datasource se define exclusivamente en la configuracion de pruebas. No utiliza
DB_URL ni ejecuta el DDL oficial en una base externa. H2 es una dependencia solo de test.
Los controles de autoria/permisos se prueban por separado; el actor se simula en las
pruebas de persistencia. MockMvc standalone no ejecuta la cadena de filtros JWT.

## Errores corregidos y regresiones

1. **Conversion silenciosa de IDs y banderas fraccionarios.**
   Jackson aceptaba un idPaciente 1.5 como 1 y devolvia 201. Esto podia seleccionar
   una persona distinta de la indicada. Se incorporaron deserializadores limitados a
   los DTOs de este modulo: requieren numeros JSON enteros, rechazan fracciones,
   cadenas, booleanos y desbordamientos. Las restricciones Positive/Min/Max siguen
   validando el rango. No se modifico el ObjectMapper global ni los DTOs de otros modulos.

2. **Precision temporal incompatible con Oracle DATE.**
   Las fechas creadas por la aplicacion contenian nanosegundos que Oracle DATE no
   conserva. Se alinearon fechas de registro/log y fechas de entrega con precision
   de segundos para evitar respuestas diferentes al releer y comparaciones inconsistentes.

3. **Validacion tardia de parametros HTTP.**
   IDs no positivos, paginas negativas, tamanios fuera de 1..100 y estados invalidos
   dependian de que la llamada llegara al servicio. Ahora se validan tambien en MVC,
   antes de invocar el servicio. Se conserva la validacion del servicio para llamadas internas.

4. **Errores HTTP heterogeneos y conflictos de bloqueo.**
   JSON incorrecto, parametros ausentes y errores de validacion MVC reciben un error
   sanitizado. Se agrego tratamiento de conflictos de bloqueo como 409, sin exponer SQL
   ni valores clinicos. Los metodos y tipos de contenido no soportados conservan 405/415.

## Comportamientos verificados con persistencia real

- Alta de persona, paciente y documentos, normalizacion y recuperacion de DTOs fuera de la transaccion.
- Busqueda sin filtros, por nombre, documento y telefono; comodines literales y estado.
- Examen, medidas decimales, precision de agudeza visual, receta, historial y entrega.
- Rollback de persona, paciente y log cuando un documento esta duplicado o el catalogo no existe.
- Validacion de solicitudes e IDs mediante los proxies Spring reales.
- Prohibicion de transferir un documento a otra persona.
- Congelacion de medidas despues de emitir una receta.
- Dos emisiones simultaneas: solo una receta confirma.
- Dos altas de paciente sobre la misma persona: solo una confirma.
- Edicion concurrente con emision: espera al bloqueo del examen y se rechaza tras confirmar la receta.

Estas pruebas verifican la estrategia en H2. El comportamiento exacto del driver Oracle,
los bloqueos bajo carga y las constraints del esquema existente siguen requiriendo Oracle.

## Hallazgos pendientes antes de produccion

### P1 - Integracion de autenticacion y permisos

La rama contiene entidades/repositorios de acceso, pero no una cadena JWT de produccion.
ActorPacienteActual requiere que Authentication.getName() corresponda a USUARIO.nombre,
un usuario/empleado activo y nombres de permisos existentes configurados mediante:

- app.atencion.permiso-lectura
- app.atencion.permiso-escritura

Sin esos permisos se deniega el acceso por defecto. No sustituir esto por permitAll.
Validar con el responsable de seguridad: token real, identidad, cuenta inactiva y
401/403 desde la cadena completa. No se inventaron roles ni nombres de permisos.

### P1 - Validacion contra Oracle y arranque completo no ejecutados

Verificar schema-validation sobre un esquema de prueba con el DDL oficial, constraints
externas, precision numerica, tipos DATE, semantica de VARCHAR2 y transacciones concurrentes.
No usar los tests H2 como evidencia de equivalencia completa con Oracle.

### P1 - Riesgo de pagos simultaneos en ventas (codigo del otro modulo)

PagoPendienteServiceImpl.registrarPagoPendiente obtiene el comprobante con findById
(linea 82), suma los pagos existentes (linea 114), valida el saldo e inserta el pago
(linea 138). ComprobanteRepository no solicita bloqueo y la entidad no tiene Version.
Por inspeccion, dos transacciones pueden leer el mismo saldo y aprobar pagos cuya suma
exceda la deuda. La anotacion Transactional por si sola no serializa esas lecturas.

Accion para el responsable: serializar por comprobante o incorporar otro mecanismo
de concurrencia e idempotencia, con una prueba concurrente sobre Oracle.
Hallazgo de revision estatica, no reproducido con un test de ventas en esta tarea.
No se modifico el modulo de ventas.

### P2 - Autoria de pagos suministrada por el cliente (otro modulo)

El mismo servicio obtiene el empleado desde request.getEmpleadoId() (linea 98).
La comprobacion de empleado activo no demuestra que sea el usuario autenticado.
Al integrar seguridad, derivar la autoria de la identidad autenticada, o validar
expresamente cualquier delegacion autorizada. No se modifico ese contrato.

### Pendiente funcional confirmado - material recomendado

RF-8/RF-17 de atencion requieren material recomendado, pero EXAMEN/RECETA no tienen
un campo para almacenarlo. Por decision del usuario se conserva el esquema.
No se considera implementado ni se codifica un ID oculto dentro de observaciones.

## Conclusion de la revision

Las pruebas cubren mas rutas y comportamientos reales que la suite anterior y los
errores anteriores tienen regresiones automatizadas. No se declara el backend listo
para produccion mientras falten la integracion de seguridad y la verificacion Oracle.

# BUC 7 — CUS22: mantenimiento de catálogos

## Alcance y trazabilidad

Se usa CUS22 de la captura suministrada: categorías, materiales de cristal,
tratamientos, servicios y laboratorios. Corresponde a las filas 116–120
(RF-91 a RF-95) de «2. REQ FUNCIONALES» del contexto, donde figura como CU24.
Se incorporan las reglas compartidas de dependencias y duplicados de las filas
122–123 (RF-97 y RF-98). No identificar requisitos solamente por su número RF.

DDL sin cambios. CUS23 (catálogo de criterios, unidades y tolerancias por defecto)
permanece excluido porque no tiene estructura en el DDL. No se implementan
parámetros del sistema, eliminación física ni cambios en ventas/stock/envíos.

## Autorización

Todas las rutas requieren autenticación y el permiso existente
`MANTENIMIENTO_GESTIONAR`. El inicializador no productivo ya lo asigna al
administrador. En producción se debe comprobar su asignación a través de la
gestión de permisos existente; no se crean roles ni permisos nuevos.
Las rutas de consulta de atención `/api/v1/catalogos-opticos` conservan sus
contratos y sus permisos originales.

## API

Base: `/api/v1/mantenimiento`.

| Recurso | Identificador | Campos editables |
|---|---|---|
| categorias | idCategoria | nombre (100), descripcion (200), estado |
| materiales-cristal | idMaterialCristal | nombre (100) |
| tratamientos-cristal | idTratamientoCristal | nombre (100) |
| servicios | idServicio | nombre (200), descripcion (200), precio, estado |
| laboratorios | idLaboratorio | nombre (200), ruc (20), telefono (50), direccion (200), correo (100), estado |

Cada recurso ofrece:

- `GET /{recurso}`: página de resultados.
- `GET /{recurso}/{id}`: detalle, 404 si no existe.
- `POST /{recurso}`: creación, 201 con Location del detalle.
- `PUT /{recurso}/{id}`: reemplazo de campos editables, 200.

PUT no modifica el identificador ni la fecha de registro de categorías.
Los IDs/fechas recibidos en el cuerpo no controlan la persistencia.
No hay DELETE, ni estado para materiales/tratamientos, ni precios para estos.

### Consultas

`termino`: búsqueda parcial por nombre, sin distinguir mayúsculas/minúsculas,
máximo 200 caracteres. Los caracteres %, _ y ! se tratan como literales.
`estado`: filtro opcional 0/1 únicamente para categorías, servicios y laboratorios.
`page`: desde 0; `size`: 1–100, por defecto 10. Se valida el límite de offset.
Orden fijo por nombre e identificador ascendente.

Respuesta de lista: `PaginaResponseDTO` existente, con content, number, size,
totalElements, totalPages, first, last y empty. Una consulta sin coincidencias
retorna 200 y una página vacía.

### Validación y edición

- Nombre obligatorio; longitudes según el DDL.
- Nombres guardados sin espacios exteriores. Duplicados comprobados usando
  nombre sin espacios exteriores y sin distinguir mayúsculas/minúsculas;
  no se eliminan tildes ni se colapsan espacios interiores.
- Estado omitido: activo (1). Estado explícitamente nulo o distinto de 0/1: 400.
- Precio obligatorio, mayor o igual a cero, hasta 10 dígitos enteros y 2 decimales,
  compatible con NUMBER(12,2). No se redondean entradas inválidas.
- Correo opcional con formato email. El RUC del laboratorio conserva el máximo
  de 20 caracteres del DDL; la regla de 11 dígitos del CUS20 es de empresa.
- Campos opcionales omitidos/nulos/blancos se limpian a null en PUT.
- Cambiar precio de un servicio no altera SERVICIO_VENDIDO.precio.
- No hay validación externa de RUC, correo, teléfono o dirección.

## Reglas de desactivación confirmadas

Las comprobaciones se ejecutan al solicitar estado 0:

1. Categoría: bloqueada si existen productos de esa categoría en estado ACTIVO,
   incluso con stock cero.
2. Servicio: bloqueado si aparece en SERVICIO_VENDIDO de una VENTA PENDIENTE.
   Ventas PAGADAS o ANULADAS no bloquean. El estado se toma del enum existente.
3. Laboratorio: bloqueado si tiene un envío sin fecha_recepcion o sin
   fecha_revision. Se requieren ambas fechas para dejar de bloquear.

No se alteran ni borran referencias históricas. La reactivación está permitida.
La validez de crear futuras dependencias de un catálogo inactivo corresponde
a los módulos que crean productos, ventas y envíos.

## Concurrencia Oracle sin migraciones

Las escrituras bloquean la tabla del catálogo en modo EXCLUSIVE, incluso
estando vacía; luego verifican duplicados y guardan dentro de la misma transacción.
Esto serializa los escritores de mantenimiento, incluyendo los nombres que
no tienen constraint UNIQUE en SERVICIO y LABORATORIO.

Antes de desactivar se bloquean en modo SHARE las tablas con las dependencias:
PRODUCTO para categorías; VENTA y SERVICIO_VENDIDO para servicios;
ENVIO_LABORATORIO para laboratorios. Así no cambian durante la comprobación.
Todos los bloqueos usan WAIT 10, se liberan al commit/rollback y requieren el
aislamiento Oracle READ COMMITTED. JdbcTemplate usa el mismo datasource que JPA.
Los nombres de tablas/modos provienen de un enum y constantes, nunca del cliente.
No se ejecutan cambios de esquema.

La contención/deadlock traducida a PessimisticLockingFailureException retorna
409 con un mensaje para reintentar, sin exponer SQL. Los bloqueos de tabla
pueden bloquear temporalmente operaciones de otros módulos: se prefieren aquí
para cubrir tabla vacía, nombres sin constraint y dependencias sin cambiar DDL.
Requieren que la cuenta Oracle sea propietaria o tenga permisos apropiados.
La unicidad normalizada se garantiza entre escritores de esta aplicación;
SQL externo puede crear nombres duplicados donde el DDL no los restringe.

## Errores

400: validación/JSON/parámetros inválidos.
401: falta autenticación. 403: falta permiso.
404: ID no encontrado. 409: duplicado, dependencia activa o bloqueo ocupado.
Errores de negocio reutilizan ErrorResponseDTO y GlobalExceptionHandler.

## Verificación

MantenimientoControllerTest usa Spring, Security, mapper, servicio y repositorios
reales sobre H2. El bloqueo Oracle se sustituye por mock: no constituye prueba de
concurrencia Oracle. Casos: ciclo por catálogo, identidad/fecha, duplicados y
renombrado, filtros combinados/paginación/literales, estado/precio/contactos,
dependencias reales de producto/venta/envío, precio histórico, validación del
servicio, permisos y contención simulada.

Se ejecutan también ConfiguracionLenteControllerTest, VentaServiceTest y
RepositoryStructureTest para verificar los contratos relacionados.

Resultado de la ejecución limpia: 63 pruebas, 0 fallos, 0 errores, 0 omitidas
(36 de mantenimiento, 15 de configuración de lentes, 7 de ventas y 5 de
estructura de repositorios). Registro local: target/mantenimiento-verificacion.log.

Pendiente de entorno Oracle de prueba: dos creaciones simultáneas con el mismo
nombre normalizado (una debe retornar 201 y otra 409); rollback y liberación de
bloqueos; desactivación concurrente con cambios en dependencias; timeout.
No declarar estas comprobaciones como realizadas con H2.

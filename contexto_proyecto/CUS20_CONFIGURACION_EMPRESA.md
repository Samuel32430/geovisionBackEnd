# CUS20 — Configurar datos de la empresa

## Alcance

Registro inicial, consulta y actualización de una única empresa. DDL Oracle sin
cambios. CUS21 (parámetros del sistema) está completamente excluido.

## Contrato HTTP

Todas las operaciones requieren autenticación y `MANTENIMIENTO_GESTIONAR`, permiso
existente asignado inicialmente al administrador en perfiles distintos de prod.
En producción debe asignarse mediante la gestión de permisos existente.

| Método | Ruta | Resultado |
|---|---|---|
| GET | `/api/v1/configuracion/empresa` | 200 con los datos; 404 si no se configuró |
| POST | `/api/v1/configuracion/empresa` | 201 y Location; 409 si ya existe |
| PUT | `/api/v1/configuracion/empresa` | 200; 404 si no existe |

POST/PUT reciben el mismo objeto. PUT reemplaza todos los campos editables;
opcionales omitidos, nulos o blancos quedan nulos. El identificador recibido se
ignora: lo genera Oracle y la actualización conserva el existente.

```json
{
  "nombreLegal": "Óptica Geo Visión",
  "nombreComercial": "Geo Visión",
  "ruc": "20123456789",
  "direccion": "Lima",
  "telefono": "999123456",
  "correo": "contacto@example.com",
  "logo": "/imagenes/logo.png"
}
```

El RUC es texto de exactamente 11 dígitos ASCII; no se valida existencia ante
SUNAT ni dígito verificador. Nombre legal obligatorio, máximo 200 caracteres.
Nombre comercial/dirección/logo: 200; teléfono: 50; correo: 100 y formato email.
Se eliminan espacios exteriores de los campos descriptivos tras validarlos.
Logo es una referencia textual; no se suben, descargan ni eliminan archivos.
Los errores de validación retornan 400 mediante el formato de errores existente.
No se implementa eliminación ni listado de múltiples empresas.

## Integridad y concurrencia

Antes de consultar existencia y escribir, POST/PUT ejecutan
`LOCK TABLE CONFIGURACION_EMPRESA IN EXCLUSIVE MODE WAIT 10` dentro de su
transacción. JdbcTemplate y JPA participan en la misma transacción/datasource.
El bloqueo incluye la tabla vacía y se libera al commit/rollback. No se realiza
DDL ni se agrega ningún constraint. Una espera agotada produce una excepción de
acceso a datos; no se reintenta automáticamente.

La cuenta Oracle requiere ser propietaria de la tabla o contar con los permisos
correspondientes para bloquearla. El mecanismo presupone el aislamiento Oracle
READ COMMITTED usado por defecto. Si existen múltiples registros heredados,
GET/PUT retornan 409; no seleccionan ni eliminan una empresa arbitrariamente.
El esquema permite inserciones externas: esta implementación garantiza el flujo
de la aplicación, no incorpora una restricción singleton en la base de datos.

## Verificación

`ConfiguracionEmpresaValidationTest`: RUC válido/inválido, ceros iniciales,
longitudes, espacios, letras y dígitos no ASCII.

`ConfiguracionEmpresaControllerTest`: API con Spring Security, validación,
servicio transaccional, mapper y repositorio real H2; ciclo completo,
identificador preservado, segundo registro, datos heredados múltiples, opcionales,
401/403/404/409, JSON inválido y ausencia de eliminación.

El bloqueo Oracle se sustituye por un mock en H2, por incompatibilidad SQL.
Estos tests no demuestran concurrencia Oracle. En un esquema Oracle de prueba,
verificar dos POST simultáneos con tabla inicialmente vacía: uno debe retornar
201, el otro 409, y debe quedar una sola fila. Repetir con rollback de la primera
transacción y verificar que la segunda pueda registrar; comprobar timeout y
liberación de bloqueos. No ejecutar estas pruebas sobre datos de producción.

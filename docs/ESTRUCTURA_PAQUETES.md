# Estructura de paquetes

Organización: `pe.edu.upeu.geovision_backend/<capa>/<modulo>`.

Capas: `controller`, `entity`, `mapper`, `dto`, `security`, `service`, `repository`, `config`, `exception`. `enums` y `util` conservan sus responsabilidades y siguen la misma división por módulo. Las implementaciones están en `service/<modulo>/impl`.

Módulos: `atencion`, `ventas`, `orden_trabajo`, `inventario`, `gestion_configuracion`, `gestion_mantenimiento`, `security`, `reports`. Los tipos transversales de producción están en la raíz de su capa; los errores JSON están en `exception/dto`. La infraestructura JWT está en `security` y sus servicios en `security/service`. Los directorios sin implementación contienen `.gitkeep`.

Se conservan todas las clases presentes, incluidos `EmpleadoCargo` y su repositorio. La reparación de paquetes respeta las ubicaciones actuales y el contenido de los métodos. Se actualizan imports y referencias de clase en JPQL. Las pruebas mantienen sus carpetas actuales, con paquetes e imports acordes.

Mantenimiento es propietario de catálogos maestros (categorías, cargos, materiales, tratamientos, servicios y laboratorios). Órdenes conserva envíos, recepción, control de calidad y entrega. Inventario conserva productos, monturas y stock. Atención conserva pacientes, datos personales, recetas, medidas y configuración clínica de lentes. Seguridad conserva empleados, cuentas, roles, permisos y autenticación. Ventas conserva pagos y detalles vendidos. Configuración conserva los datos de empresa.

`OpticaValidator` pertenece a `util/atencion`; `PoliticaCalidad` carga tolerancias en `config/gestion_mantenimiento`. `PersonaUsuarioResumenDTO` sirve a la selección de empleados para cuentas y pertenece a seguridad. Los historiales operativos permanecen en su módulo; `reports` se reserva para futuras clases dedicadas.

## Verificación de la reorganización

- 237 clases de producción y 29 clases de pruebas conservadas y reubicadas.
- Los 72 directorios requeridos existen; las 266 declaraciones de paquete
  coinciden con sus rutas físicas.
- Se verificó que los cuerpos de código permanecen idénticos salvo las
  referencias de clase completamente calificadas; no se cambiaron reglas.
- `./mvnw.cmd -o clean test`: producción y pruebas compilan correctamente.
  225 pruebas ejecutadas: 204 correctas, 19 fallos de aserción y 2 errores.
- La ejecución anterior a la reorganización produjo exactamente los mismos
  resultados, comparados prueba por prueba: ninguna regresión detectada.
- `git diff --check`: correcto. Detalles de pruebas en
  `target/surefire-reports/` y `target/reorganization-verification.log`.

## Corrección posterior del manejo de excepciones

Se conserva `handleAnyException` como respuesta JSON para errores inesperados.
Los handlers específicos atienden validación de parámetros, métodos HTTP no
permitidos y `ResponseStatusException`, preservando sus estados y headers. Las
respuestas de error declaran `application/json`, incluso ante solicitudes de PDF.

Después de esta corrección, `./mvnw.cmd -o test` ejecutó 233 pruebas: 230 correctas,
2 fallos y 1 error. Las 17 pruebas HTTP antes fallidas y la prueba PDF pasan.
Los tres casos restantes pertenecen a `VentaServiceTest`, cuyos mocks de receta
no corresponden a `findByIdForUpdate`. Ocho nuevas pruebas comprueban el formato
JSON, la cabecera `Allow`, los estados explícitos y el catch-all de errores 500.

## Reparación de paquetes tras movimientos manuales

Se alinearon 37 declaraciones de paquete con sus carpetas y se actualizaron las
referencias en producción, pruebas y consultas JPQL. Se añadieron imports
explícitos donde JWT y el test del handler dependían de compartir paquete.
Las 267 clases Java tienen rutas y paquetes coherentes; no quedan imports de
aplicación sin resolver. No se movieron archivos ni se cambiaron cuerpos de métodos.

Producción y pruebas compilan. La suite ejecutó 233 pruebas: 229 correctas,
3 fallos de aserción y 1 error. Permanecen los tres casos de mocks de ventas;
la otra aserción detecta que `handleMethodNotSupported` no conserva la cabecera
`Allow` en su implementación actual. Estos comportamientos se dejaron intactos.
Los resultados están en `target/surefire-reports/` y `target/package-repair-tests.log`.

## Resolución de los casos restantes

Se restauró la cabecera `Allow` del error 405 a partir de los headers de
`HttpRequestMethodNotSupportedException`, conservando el JSON personalizado.
En los tres casos de receta de `VentaServiceTest`, los mocks ahora usan
`findByIdForUpdate`, igual que el servicio real. No se modificaron las reglas
de ventas ni `handleAnyException`.

Verificación final: `./mvnw.cmd -o test` terminó con **BUILD SUCCESS**:
233 pruebas correctas, 0 fallos, 0 errores y 0 omitidas. Registro de esta
ejecución: `target/remaining-fixes-tests.log`.

## Indicador Boolean de actividad

`Usuario`, `Empleado`, `Paciente`, `Producto`, `Categoria`, `Servicio` y
`Laboratorio` usan `Boolean activo = true`. Se eliminaron el enum de estado
general, sus siete converters y los métodos `getEstadoCodigo`/`setEstadoCodigo`
asociados. Lombok proporciona `getActivo`/`setActivo`; las consultas JPQL y los
métodos derivados de repositorios usan la propiedad `activo`.

Se conservan las columnas `estado` y su representación numérica `0/1`, mediante
`@JdbcTypeCode(SqlTypes.INTEGER)`, sin converters propios. También se conservan
los campos numéricos de los DTO y parámetros públicos, con validación `0/1` en
la entrada y mapeo al Boolean. El ordenamiento público `sortBy=estado` se dirige
a la propiedad `activo`. Las comprobaciones de permisos, autenticación y
disponibilidad utilizan `Boolean.TRUE.equals(...)`.

Los estados múltiples de ventas y órdenes, así como los demás enums, conservan
sus converters y métodos propios. Se añadió una prueba de persistencia que
comprueba `true → 1`, `false → 0`, lectura desde la columna numérica y valor
predeterminado activo cuando se inserta un indicador nulo.

Verificación final de este cambio: `./mvnw.cmd -o clean test`, **234 pruebas
correctas, 0 fallos, 0 errores**. El clean elimina también los binarios antiguos
del enum y sus converters. Log: `target/boolean-activo-verification.log`.

## Inventario completo

| Origen | Destino |
|---|---|
| `src/main/java/pe/edu/upeu/geovisionBackEnd/config/CorsConfig.java` | `src/main/java/pe/edu/upeu/geovision_backend/config/security/CorsConfig.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/config/JacksonConfig.java` | `src/main/java/pe/edu/upeu/geovision_backend/config/JacksonConfig.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/config/SecurityConfig.java` | `src/main/java/pe/edu/upeu/geovision_backend/config/security/SecurityConfig.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/config/SecurityDataInitializer.java` | `src/main/java/pe/edu/upeu/geovision_backend/config/security/SecurityDataInitializer.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/controller/AuthController.java` | `src/main/java/pe/edu/upeu/geovision_backend/controller/security/AuthController.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/controller/CatalogoOpticoController.java` | `src/main/java/pe/edu/upeu/geovision_backend/controller/atencion/CatalogoOpticoController.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/controller/ConfiguracionEmpresaController.java` | `src/main/java/pe/edu/upeu/geovision_backend/controller/gestion_configuracion/ConfiguracionEmpresaController.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/controller/ControlCalidadController.java` | `src/main/java/pe/edu/upeu/geovision_backend/controller/orden_trabajo/ControlCalidadController.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/controller/InventarioStockController.java` | `src/main/java/pe/edu/upeu/geovision_backend/controller/inventario/InventarioStockController.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/controller/MantenimientoController.java` | `src/main/java/pe/edu/upeu/geovision_backend/controller/gestion_mantenimiento/MantenimientoController.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/controller/NotificacionOrdenController.java` | `src/main/java/pe/edu/upeu/geovision_backend/controller/orden_trabajo/NotificacionOrdenController.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/controller/OrdenTrabajoController.java` | `src/main/java/pe/edu/upeu/geovision_backend/controller/orden_trabajo/OrdenTrabajoController.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/controller/PacienteController.java` | `src/main/java/pe/edu/upeu/geovision_backend/controller/atencion/PacienteController.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/controller/PagoPendienteController.java` | `src/main/java/pe/edu/upeu/geovision_backend/controller/ventas/PagoPendienteController.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/controller/PermisoController.java` | `src/main/java/pe/edu/upeu/geovision_backend/controller/security/PermisoController.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/controller/ProductoCatalogoController.java` | `src/main/java/pe/edu/upeu/geovision_backend/controller/inventario/ProductoCatalogoController.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/controller/RecetaController.java` | `src/main/java/pe/edu/upeu/geovision_backend/controller/atencion/RecetaController.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/controller/RolController.java` | `src/main/java/pe/edu/upeu/geovision_backend/controller/security/RolController.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/controller/UsuarioController.java` | `src/main/java/pe/edu/upeu/geovision_backend/controller/security/UsuarioController.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/controller/VentaController.java` | `src/main/java/pe/edu/upeu/geovision_backend/controller/ventas/VentaController.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/controller/VentaHistorialController.java` | `src/main/java/pe/edu/upeu/geovision_backend/controller/ventas/VentaHistorialController.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/AjusteStockRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/inventario/AjusteStockRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/CambioPasswordRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/security/CambioPasswordRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/CargoDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/gestion_mantenimiento/CargoDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/CategoriaDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/gestion_mantenimiento/CategoriaDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/ConfiguracionEmpresaDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/gestion_configuracion/ConfiguracionEmpresaDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/ControlCalidadRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/orden_trabajo/ControlCalidadRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/CuentaUsuarioRegistroDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/security/CuentaUsuarioRegistroDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/EmpleadoRegistroDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/security/EmpleadoRegistroDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/EntradaStockRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/inventario/EntradaStockRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/EntregaPedidoRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/orden_trabajo/EntregaPedidoRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/EnvioLaboratorioRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/orden_trabajo/EnvioLaboratorioRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/EnvioLaboratorioResponseDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/orden_trabajo/EnvioLaboratorioResponseDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/EstadoNotificacionOrdenDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/orden_trabajo/EstadoNotificacionOrdenDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/LaboratorioDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/gestion_mantenimiento/LaboratorioDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/LenteRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/atencion/LenteRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/LenteResponseDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/atencion/LenteResponseDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/LoginRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/security/LoginRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/LoginResponseDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/security/LoginResponseDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/LogoutResponseDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/security/LogoutResponseDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/MaterialCristalDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/gestion_mantenimiento/MaterialCristalDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/MedidaOjoDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/atencion/MedidaOjoDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/MedidaRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/atencion/MedidaRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/MedidaResponseDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/atencion/MedidaResponseDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/MonturaDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/inventario/MonturaDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/MovimientoStockResponseDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/inventario/MovimientoStockResponseDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/NotificacionOrdenRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/orden_trabajo/NotificacionOrdenRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/NotificacionOrdenResponseDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/orden_trabajo/NotificacionOrdenResponseDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/NotificacionPacienteDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/orden_trabajo/NotificacionPacienteDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/OrdenTrabajoRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/orden_trabajo/OrdenTrabajoRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/OrdenTrabajoResponseDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/orden_trabajo/OrdenTrabajoResponseDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/PacienteRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/atencion/PacienteRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/PacienteResponseDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/atencion/PacienteResponseDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/PaginaResponseDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/PaginaResponseDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/PagoPendienteResponseDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/ventas/PagoPendienteResponseDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/PagoRegistroRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/ventas/PagoRegistroRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/PagoResponseDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/ventas/PagoResponseDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/PendienteOrdenTrabajoDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/orden_trabajo/PendienteOrdenTrabajoDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/PermisoDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/security/PermisoDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/PersonaRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/atencion/PersonaRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/PersonaResponseDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/atencion/PersonaResponseDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/PersonaUsuarioResumenDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/security/PersonaUsuarioResumenDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/PoliticaCalidadDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/orden_trabajo/PoliticaCalidadDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/ProductoRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/inventario/ProductoRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/ProductoResponseDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/inventario/ProductoResponseDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/ProductoStockResponseDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/inventario/ProductoStockResponseDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/ProductoVendidoDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/ventas/ProductoVendidoDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/RecepcionLaboratorioRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/orden_trabajo/RecepcionLaboratorioRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/RecetaRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/atencion/RecetaRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/RecetaResponseDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/atencion/RecetaResponseDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/RecuperarPasswordRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/security/RecuperarPasswordRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/RegistroPersonaUsuarioRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/security/RegistroPersonaUsuarioRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/ResetPasswordRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/security/ResetPasswordRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/RolDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/security/RolDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/ServicioDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/gestion_mantenimiento/ServicioDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/ServicioVendidoDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/ventas/ServicioVendidoDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/TratamientoCristalDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/gestion_mantenimiento/TratamientoCristalDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/UsuarioRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/security/UsuarioRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/UsuarioResponseDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/security/UsuarioResponseDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/VentaHistorialResponseDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/ventas/VentaHistorialResponseDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/VentaRegistroRequestDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/ventas/VentaRegistroRequestDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/dto/VentaResponseDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/dto/ventas/VentaResponseDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/Cargo.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/gestion_mantenimiento/Cargo.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/Categoria.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/gestion_mantenimiento/Categoria.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/ConfiguracionEmpresa.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/gestion_configuracion/ConfiguracionEmpresa.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/Empleado.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/security/Empleado.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/EmpleadoCargo.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/security/EmpleadoCargo.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/EmpleadoLog.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/security/EmpleadoLog.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/EnvioLaboratorio.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/orden_trabajo/EnvioLaboratorio.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/Laboratorio.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/gestion_mantenimiento/Laboratorio.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/Lente.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Lente.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/LenteMedida.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/LenteMedida.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/LenteTratamientoCristal.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/LenteTratamientoCristal.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/MaterialCristal.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/gestion_mantenimiento/MaterialCristal.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/Medida.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Medida.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/MedidaOjo.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/MedidaOjo.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/Montura.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/inventario/Montura.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/MovimientoStockLog.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/inventario/MovimientoStockLog.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/OrdenTrabajo.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/orden_trabajo/OrdenTrabajo.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/Paciente.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Paciente.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/PacienteLog.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/PacienteLog.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/Pago.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/ventas/Pago.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/Permiso.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/security/Permiso.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/Persona.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Persona.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/Producto.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/inventario/Producto.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/ProductoVendido.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/ventas/ProductoVendido.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/Receta.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/atencion/Receta.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/Rol.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/security/Rol.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/RolPermiso.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/security/RolPermiso.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/Servicio.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/gestion_mantenimiento/Servicio.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/ServicioVendido.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/ventas/ServicioVendido.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/TratamientoCristal.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/gestion_mantenimiento/TratamientoCristal.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/Usuario.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/security/Usuario.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/UsuarioLog.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/security/UsuarioLog.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/UsuarioRol.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/security/UsuarioRol.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/entity/Venta.java` | `src/main/java/pe/edu/upeu/geovision_backend/entity/ventas/Venta.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/enums/EstadoOrdenTrabajoEnum.java` | `src/main/java/pe/edu/upeu/geovision_backend/enums/orden_trabajo/EstadoOrdenTrabajoEnum.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/enums/EstadoVentaEnum.java` | `src/main/java/pe/edu/upeu/geovision_backend/enums/ventas/EstadoVentaEnum.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/enums/MaterialMonturaEnum.java` | `src/main/java/pe/edu/upeu/geovision_backend/enums/inventario/MaterialMonturaEnum.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/enums/MetodoPagoEnum.java` | `src/main/java/pe/edu/upeu/geovision_backend/enums/ventas/MetodoPagoEnum.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/enums/ModeloMonturaEnum.java` | `src/main/java/pe/edu/upeu/geovision_backend/enums/inventario/ModeloMonturaEnum.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/enums/ModuloEnum.java` | `src/main/java/pe/edu/upeu/geovision_backend/enums/security/ModuloEnum.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/enums/MotivoMovimientoStockEnum.java` | `src/main/java/pe/edu/upeu/geovision_backend/enums/inventario/MotivoMovimientoStockEnum.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/enums/SexoEnum.java` | `src/main/java/pe/edu/upeu/geovision_backend/enums/atencion/SexoEnum.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/enums/TipoComprobanteEnum.java` | `src/main/java/pe/edu/upeu/geovision_backend/enums/ventas/TipoComprobanteEnum.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/enums/TipoDocumentoEnum.java` | `src/main/java/pe/edu/upeu/geovision_backend/enums/atencion/TipoDocumentoEnum.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/enums/TipoLogEnum.java` | `src/main/java/pe/edu/upeu/geovision_backend/enums/TipoLogEnum.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/enums/TipoMedidaEnum.java` | `src/main/java/pe/edu/upeu/geovision_backend/enums/atencion/TipoMedidaEnum.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/exception/dto/ErrorResponseDTO.java` | `src/main/java/pe/edu/upeu/geovision_backend/exception/dto/ErrorResponseDTO.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/exception/GlobalExceptionHandler.java` | `src/main/java/pe/edu/upeu/geovision_backend/exception/GlobalExceptionHandler.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/exception/RecursosNoEncontradosException.java` | `src/main/java/pe/edu/upeu/geovision_backend/exception/RecursosNoEncontradosException.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/exception/ReglaNegocioException.java` | `src/main/java/pe/edu/upeu/geovision_backend/exception/ReglaNegocioException.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/GeovisionBackEndApplication.java` | `src/main/java/pe/edu/upeu/geovision_backend/GeovisionBackEndApplication.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/mapper/CatalogoOpticoMapper.java` | `src/main/java/pe/edu/upeu/geovision_backend/mapper/atencion/CatalogoOpticoMapper.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/mapper/ConfiguracionEmpresaMapper.java` | `src/main/java/pe/edu/upeu/geovision_backend/mapper/gestion_configuracion/ConfiguracionEmpresaMapper.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/mapper/MantenimientoMapper.java` | `src/main/java/pe/edu/upeu/geovision_backend/mapper/gestion_mantenimiento/MantenimientoMapper.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/mapper/MovimientoStockMapper.java` | `src/main/java/pe/edu/upeu/geovision_backend/mapper/inventario/MovimientoStockMapper.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/mapper/OrdenTrabajoMapper.java` | `src/main/java/pe/edu/upeu/geovision_backend/mapper/orden_trabajo/OrdenTrabajoMapper.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/mapper/PacienteMapper.java` | `src/main/java/pe/edu/upeu/geovision_backend/mapper/atencion/PacienteMapper.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/mapper/PagoMapper.java` | `src/main/java/pe/edu/upeu/geovision_backend/mapper/ventas/PagoMapper.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/mapper/PermisoMapper.java` | `src/main/java/pe/edu/upeu/geovision_backend/mapper/security/PermisoMapper.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/mapper/ProductoMapper.java` | `src/main/java/pe/edu/upeu/geovision_backend/mapper/inventario/ProductoMapper.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/mapper/RecetaMapper.java` | `src/main/java/pe/edu/upeu/geovision_backend/mapper/atencion/RecetaMapper.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/mapper/RolMapper.java` | `src/main/java/pe/edu/upeu/geovision_backend/mapper/security/RolMapper.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/mapper/UsuarioMapper.java` | `src/main/java/pe/edu/upeu/geovision_backend/mapper/security/UsuarioMapper.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/mapper/VentaMapper.java` | `src/main/java/pe/edu/upeu/geovision_backend/mapper/ventas/VentaMapper.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/CargoRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/gestion_mantenimiento/CargoRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/CategoriaRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/gestion_mantenimiento/CategoriaRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/ConfiguracionEmpresaLock.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/gestion_configuracion/ConfiguracionEmpresaLock.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/ConfiguracionEmpresaRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/gestion_configuracion/ConfiguracionEmpresaRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/EmpleadoCargoRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/security/EmpleadoCargoRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/EmpleadoLogRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/security/EmpleadoLogRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/EmpleadoRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/security/EmpleadoRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/EnvioLaboratorioRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/orden_trabajo/EnvioLaboratorioRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/LaboratorioRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/gestion_mantenimiento/LaboratorioRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/LenteMedidaRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/atencion/LenteMedidaRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/LenteRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/atencion/LenteRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/LenteTratamientoCristalRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/atencion/LenteTratamientoCristalRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/MantenimientoLock.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/gestion_mantenimiento/MantenimientoLock.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/MaterialCristalRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/gestion_mantenimiento/MaterialCristalRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/MedidaOjoRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/atencion/MedidaOjoRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/MedidaRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/atencion/MedidaRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/MonturaRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/inventario/MonturaRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/MovimientoStockLogRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/inventario/MovimientoStockLogRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/OrdenTrabajoRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/orden_trabajo/OrdenTrabajoRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/PacienteLogRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/atencion/PacienteLogRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/PacienteRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/atencion/PacienteRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/PagoRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/ventas/PagoRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/PermisoRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/security/PermisoRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/PersonaRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/atencion/PersonaRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/ProductoRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/inventario/ProductoRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/ProductoVendidoRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/ventas/ProductoVendidoRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/RecetaRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/atencion/RecetaRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/RolPermisoRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/security/RolPermisoRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/RolRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/security/RolRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/ServicioRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/gestion_mantenimiento/ServicioRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/ServicioVendidoRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/ventas/ServicioVendidoRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/TratamientoCristalRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/gestion_mantenimiento/TratamientoCristalRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/UsuarioLogRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/security/UsuarioLogRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/UsuarioRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/security/UsuarioRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/UsuarioRolRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/security/UsuarioRolRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/repository/VentaRepository.java` | `src/main/java/pe/edu/upeu/geovision_backend/repository/ventas/VentaRepository.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/security/JwtAuthenticationFilter.java` | `src/main/java/pe/edu/upeu/geovision_backend/security/JwtAuthenticationFilter.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/security/JwtService.java` | `src/main/java/pe/edu/upeu/geovision_backend/security/service/JwtService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/security/RespuestaErrorSeguridad.java` | `src/main/java/pe/edu/upeu/geovision_backend/security/RespuestaErrorSeguridad.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/security/RestAccessDeniedHandler.java` | `src/main/java/pe/edu/upeu/geovision_backend/security/RestAccessDeniedHandler.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/security/RestAuthenticationEntryPoint.java` | `src/main/java/pe/edu/upeu/geovision_backend/security/RestAuthenticationEntryPoint.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/security/UsuarioDetailsService.java` | `src/main/java/pe/edu/upeu/geovision_backend/security/service/UsuarioDetailsService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/impl/AuthServiceImpl.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/security/impl/AuthServiceImpl.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/impl/CatalogoOpticoServiceImpl.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/atencion/impl/CatalogoOpticoServiceImpl.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/impl/ConfiguracionEmpresaServiceImpl.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/gestion_configuracion/impl/ConfiguracionEmpresaServiceImpl.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/impl/ControlCalidadServiceImpl.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/orden_trabajo/impl/ControlCalidadServiceImpl.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/impl/EmailServiceImpl.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/security/impl/EmailServiceImpl.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/impl/InventarioStockServiceImpl.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/inventario/impl/InventarioStockServiceImpl.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/impl/MantenimientoServiceImpl.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/gestion_mantenimiento/impl/MantenimientoServiceImpl.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/impl/NotificacionOrdenServiceImpl.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/orden_trabajo/impl/NotificacionOrdenServiceImpl.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/impl/OrdenTrabajoServiceImpl.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/orden_trabajo/impl/OrdenTrabajoServiceImpl.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/impl/PacienteServiceImpl.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/atencion/impl/PacienteServiceImpl.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/impl/PagoPendienteServiceImpl.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/ventas/impl/PagoPendienteServiceImpl.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/impl/PermisoServiceImpl.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/security/impl/PermisoServiceImpl.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/impl/ProductoCatalogoServiceImpl.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/inventario/impl/ProductoCatalogoServiceImpl.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/impl/RecetaPdfServiceImpl.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/atencion/impl/RecetaPdfServiceImpl.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/impl/RecetaServiceImpl.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/atencion/impl/RecetaServiceImpl.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/impl/RolServiceImpl.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/security/impl/RolServiceImpl.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/impl/TextbeeClient.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/orden_trabajo/impl/TextbeeClient.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/impl/UsuarioLogServiceImpl.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/security/impl/UsuarioLogServiceImpl.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/impl/UsuarioServiceImpl.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/security/impl/UsuarioServiceImpl.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/impl/VentaHistorialServiceImpl.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/ventas/impl/VentaHistorialServiceImpl.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/impl/VentaServiceImpl.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/ventas/impl/VentaServiceImpl.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/service/AuthService.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/security/AuthService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/service/CatalogoOpticoService.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/atencion/CatalogoOpticoService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/service/ConfiguracionEmpresaService.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/gestion_configuracion/ConfiguracionEmpresaService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/service/ControlCalidadService.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/orden_trabajo/ControlCalidadService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/service/EmailService.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/security/EmailService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/service/InventarioStockService.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/inventario/InventarioStockService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/service/MantenimientoService.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/gestion_mantenimiento/MantenimientoService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/service/NotificacionOrdenService.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/orden_trabajo/NotificacionOrdenService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/service/OrdenTrabajoService.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/orden_trabajo/OrdenTrabajoService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/service/PacienteService.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/atencion/PacienteService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/service/PagoPendienteService.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/ventas/PagoPendienteService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/service/PermisoService.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/security/PermisoService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/service/ProductoCatalogoService.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/inventario/ProductoCatalogoService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/service/RecetaPdfService.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/atencion/RecetaPdfService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/service/RecetaService.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/atencion/RecetaService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/service/RolService.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/security/RolService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/service/UsuarioLogService.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/security/UsuarioLogService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/service/UsuarioService.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/security/UsuarioService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/service/VentaHistorialService.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/ventas/VentaHistorialService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/service/service/VentaService.java` | `src/main/java/pe/edu/upeu/geovision_backend/service/ventas/VentaService.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/util/ActorOrdenTrabajo.java` | `src/main/java/pe/edu/upeu/geovision_backend/util/orden_trabajo/ActorOrdenTrabajo.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/util/ComparadorLensometria.java` | `src/main/java/pe/edu/upeu/geovision_backend/util/orden_trabajo/ComparadorLensometria.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/util/ConstanciaOrdenTrabajo.java` | `src/main/java/pe/edu/upeu/geovision_backend/util/orden_trabajo/ConstanciaOrdenTrabajo.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/util/FlujoOrdenTrabajo.java` | `src/main/java/pe/edu/upeu/geovision_backend/util/orden_trabajo/FlujoOrdenTrabajo.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/util/OpticaValidator.java` | `src/main/java/pe/edu/upeu/geovision_backend/util/atencion/OpticaValidator.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/util/PoliticaCalidad.java` | `src/main/java/pe/edu/upeu/geovision_backend/config/gestion_mantenimiento/PoliticaCalidad.java` |
| `src/main/java/pe/edu/upeu/geovisionBackEnd/util/VentaOrdenTrabajo.java` | `src/main/java/pe/edu/upeu/geovision_backend/util/orden_trabajo/VentaOrdenTrabajo.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/controller/AtencionPacienteIntegrationTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/controller/atencion/AtencionPacienteIntegrationTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/controller/ConfiguracionEmpresaControllerTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/controller/gestion_configuracion/ConfiguracionEmpresaControllerTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/controller/ConfiguracionLenteControllerTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/controller/atencion/ConfiguracionLenteControllerTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/controller/HistorialClinicoControllerTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/controller/atencion/HistorialClinicoControllerTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/controller/InventarioStockControllerTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/controller/inventario/InventarioStockControllerTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/controller/MantenimientoControllerTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/controller/gestion_mantenimiento/MantenimientoControllerTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/controller/PacienteControllerTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/controller/atencion/PacienteControllerTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/controller/PagoPendienteControllerTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/controller/ventas/PagoPendienteControllerTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/controller/ProductoCatalogoControllerTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/controller/inventario/ProductoCatalogoControllerTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/controller/RecetaControllerTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/controller/atencion/RecetaControllerTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/controller/RecetaPdfControllerTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/controller/atencion/RecetaPdfControllerTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/controller/VentaControllerTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/controller/ventas/VentaControllerTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/controller/VentaHistorialControllerTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/controller/ventas/VentaHistorialControllerTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/dto/ConfiguracionEmpresaValidationTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/dto/gestion_configuracion/ConfiguracionEmpresaValidationTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/dto/PatientFoundationValidationTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/dto/atencion/PatientFoundationValidationTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/enums/TipoMedidaEnumTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/enums/atencion/TipoMedidaEnumTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/GeovisionBackEndApplicationTests.java` | `src/test/java/pe/edu/upeu/geovision_backend/GeovisionBackEndApplicationTests.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/repository/PatientFoundationRepositoryTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/repository/atencion/PatientFoundationRepositoryTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/repository/RepositoryStructureTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/repository/comun/RepositoryStructureTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/security/JwtServiceTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/security/seguridad/JwtServiceTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/service/AuthServiceTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/service/security/AuthServiceTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/service/InventarioStockServiceTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/service/inventario/InventarioStockServiceTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/service/PagoPendienteServiceTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/service/ventas/PagoPendienteServiceTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/service/ProductoCatalogoServiceTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/service/inventario/ProductoCatalogoServiceTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/service/RecetaPdfServiceTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/service/atencion/RecetaPdfServiceTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/service/RolServiceTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/service/security/RolServiceTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/service/UsuarioServiceTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/service/security/UsuarioServiceTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/service/VentaHistorialServiceTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/service/ventas/VentaHistorialServiceTest.java` |
| `src/test/java/pe/edu/upeu/geovisionBackEnd/service/VentaServiceTest.java` | `src/test/java/pe/edu/upeu/geovision_backend/service/ventas/VentaServiceTest.java` |
| `—` | `src/test/java/pe/edu/upeu/geovision_backend/exception/comun/GlobalExceptionHandlerTest.java` |

# CONTEXTO INTEGRAL — SISTEMA DE VENTAS ÓPTICA GEO VISIÓN


**Fecha de compilación y revisión:** 20 de octubre de 2026. **Alcance:** documentación para orientar agentes de desarrollo. **No es una certificación de que el código o DDL esté validado.**


## Control de revisión (20-10-2026)

**Límite de fuentes:** la única plantilla disponible aquí es `3. Plantilla de requerimientos V3 (3).xlsx`, también utilizada en el documento anterior. **No se ha recibido una segunda versión distinguible** para efectuar un diff de cambios recientes. Esta revisión mejora la exactitud del análisis existente; no afirma detectar ediciones recientes de la plantilla.

- Huella SHA-256 de la plantilla disponible: `09b8f368e01b018db3a0f366b3406c7b989d5b1903dd27d8b4d6dfb30432fe2a`.
- **114 definiciones RF distintas** en la hoja `2. REQ FUNCIONALES`; **37 apariciones adicionales** en `Hoja1` que replican exactamente código y descripción de la hoja principal. El total de **151 apariciones** no significa 151 funcionalidades.
- **13 identificadores RF con colisiones** (`RF-83` a `RF-95`), que designan distintas funcionalidades entre inventario/seguridad/configuración/mantenimiento. Para las tareas usar **nombre de hoja + fila + descripción**, no solamente código RF.
- **15 casos de uso nombrados** y **9 entradas sin nombre** (`CU-16` a `CU-24`) en la hoja de priorización; no inventar el significado de las últimas.
- Hay conflictos entre nomenclaturas: `CUS05` designa tanto receta oftálmica como venta directa; `CU-6` en priorización es venta directa mientras `CUS06` en requerimientos se refiere a venta de servicios. Se requiere validación de un catálogo canónico.
- En priorización constan `CU-2 Agendar cita`, `CU-5 Generar constancia de atención`, `CU-14 Mandar Requerimiento de Compra` y `CU-15 Recepcionar Productos`. **No añadir estos flujos al backend solo por su presencia en priorización**; confirmar su descripción funcional y compatibilidad con DDL.
- Ausencia de numeración: `RF-1`, `RF-11`, `RF-21` a `RF-24`. No se pueden etiquetar automáticamente como funcionalidades faltantes.
- La estrategia JWT + roles/permisos y la relación usuario–empleado 1:1 siguen siendo acuerdos de arquitectura; los permisos exactos por rol, caducidad y revocación de JWT continúan pendientes de definición.

Revisar [AUDITORIA_REQUERIMIENTOS.md](AUDITORIA_REQUERIMIENTOS.md) antes de implementar nuevos RF/CUS. Allí se detallan las prioridades actuales, conflictos y protocolo para comparar una próxima plantilla.

## 1. Fuentes y autoridad
- Matriz íntegra aportada: `3. Plantilla de requerimientos V3 (3).xlsx` (10 hojas); las transcripciones conservan las referencias de fila.
- Contexto de conversaciones del proyecto, incluyendo plan de backend, revisiones DDL, arquitectura y decisiones del equipo.
- El DDL oficial final y el código real NO están adjuntos aquí para cotejo exhaustivo. El agente debe recibirlos adicionalmente.
- Jerarquía: DDL final/código real para persistencia, decisiones aprobadas para reglas, matriz para cobertura funcional; contradicciones deben resolverse antes de programar.


## 2. Objetivo y dominios
Sistema para pacientes e historia, exámenes, recetas internas y externas, ventas de productos/servicios/con receta, pagos, órdenes de trabajo, envíos a laboratorio, controles de calidad, entrega, inventario, usuarios y permisos, configuración, mantenimiento de catálogos y reportes.


## 3. Estado del proyecto y decisiones técnicas confirmadas
- Equipo: 4 desarrolladores que deben entender y revisar el código generado. Cada uno trabaja en ramas funcionales; integración a `develop` mediante PR.
- Repositorio Git inicialmente vacío; proyectos backend y frontend creados. No se dispone de su estado actual.
- Backend: **Java + Spring Boot + Maven**, arquitectura **en capas y responsabilidades desacopladas** con `controller`, DTO, `models`, `repository`, `service`, `serviceimpl`, `security`. API REST con JSON y HTTPS en despliegue.
- Frontend: **Angular SPA**, separado del backend; comunicación HTTP mediante API REST.
- Base de datos: **Oracle Database**; acceso mediante driver **JDBC** (el uso de JPA/Hibernate vs JDBC directo debe comprobarse en `pom.xml` y código).
- DDL definitivo reportado como ejecutado en esquema de prueba. Backend compilaba pero aún no conectaba a Oracle al momento del último reporte.
- Seguridad acordada: usuarios, hash de contraseñas, autenticación **JWT**, roles, permisos y protección de endpoints; detalles de biblioteca, algoritmos de firma, expiración, refresh y revocación no confirmados.
- Entidades: usuario siempre pertenece a empleado; relación empleado–usuario 1:1 según DDL revisado. Estado `PENDIENTE` añadido al catálogo. Agudeza visual de escala decimal.
- También discutidos: normalización de comprobantes, medios/pagos, auditoría/triggers, precio histórico en tratamientos, trazabilidad de empleado y receta interna/externa; contrastar con DDL actual antes de generar mappings.


## 4. Stack: confirmado frente a verificación pendiente
| Componente | Selección / estado |
|---|---|
| Frontend | Angular SPA, proyecto independiente |
| Backend | Java, Spring Boot, Maven |
| API | REST / JSON / HTTPS en despliegue |
| Persistencia | Oracle Database, Oracle JDBC; mecanismo JPA o JDBC directo **por confirmar** |
| Estructura | Controller, DTO, models, repository, service, serviceimpl, security |
| Seguridad | Spring Security **propuesto para implementación**, JWT definido, hash, RBAC por roles/permisos |
| Testing | JUnit 5, Mockito, Spring Boot Test: **dependencias a comprobar**, no afirmar instaladas |
| Docs API | OpenAPI/Swagger: recomendado, no confirmado |
| Migraciones | Flyway/Liquibase: no confirmados; evitar ejecutar sobre DDL existente sin acuerdo |
| CI/CD y hosting | Sin tecnología/versiones confirmadas |
| Colaboración | Git + ramas de funcionalidad + integración en `develop` |


## 5. Modelo de seguridad y diseño de controles
### 5.1 Decisiones
- Usuario autenticado mediante credenciales; autorización basada en roles y permisos (RBAC).
- Gestión de usuarios: alta, actualización, desactivación; asignación de **un rol por usuario** según RF-89; permisos asociados a roles.
- Inicio de sesión, cierre de sesión, recuperación de contraseña, validación del código y cambio.
- Toda cuenta está ligada a un empleado (vínculo 1:1), con estado activo verificable.
- Autenticación JWT expresamente indicada en plan de backend. Su diseño detallado y el esquema de claves no están confirmados.
### 5.2 Propuesta técnica para implementación, no confundir con política aprobada
- Spring Security con PasswordEncoder (BCrypt o Argon2 según dependencias). Evitar guardar contraseñas o tokens en logs.
- `SecurityFilterChain`, acceso denegado por defecto, reglas de endpoints + comprobaciones en la capa service (`@PreAuthorize` si se adopta), 401/403 correctamente diferenciados.
- JWT firmado, expiración corta, validación de issuer/audience/firma/exp, estrategia documentada de revocación/logout y refresh (si se aprueba).
- Recuperación mediante token/código de un solo uso, caducidad, rate limit y respuesta que evite enumerar cuentas.
- CORS solo para origen frontend aprobado, HTTPS, secretos desde variables de entorno, autorización de datos por ámbito y auditoría de cambios críticos.
### 5.3 Actores
De la matriz: Cliente/Paciente, Recepcionista, Optómetra, Vendedor, Administrador, Dueño/Técnico, Sistema. **Actores ≠ roles de base de datos automáticamente.** El técnico aparece como responsable, mientras varias filas indican Vendedor como actor: pendiente definir la matriz de autorizaciones por endpoint.
### 5.4 Rutas como contratos ilustrativos (NO existentes confirmadas)
`POST /api/auth/login`, `POST /api/auth/logout`, `POST /api/auth/forgot-password`, `POST /api/auth/reset-password`, `/api/usuarios`, `/api/roles`, `/api/permisos`. Verificar convenciones reales antes de crearlas.


## 6. Inventario completo de procesos y actividades de la matriz
Se conserva cada actividad no vacía con fuente y responsable. La secuencia de procesos proviene de las filas CUN. Las filas en blanco no representan requisitos adicionales.


- **Sin proceso** — Actividad del Negocio _(responsable: Responsable del  Negocio; 2. REQ FUNCIONALES fila 5)_

- **CUN 1 Atender paciente en consulta** — Solicitud _(responsable: sin consignar; 2. REQ FUNCIONALES fila 6)_

- **CUN 1 Atender paciente en consulta** — Solicita examen visual _(responsable: Cliente; 2. REQ FUNCIONALES fila 7)_

- **CUN 1 Atender paciente en consulta** — Revisa si el paciente ya está registrado _(responsable: Recepcionista; 2. REQ FUNCIONALES fila 8)_

- **CUN 1 Atender paciente en consulta** — Registrar los datos personales del paciente en un excel (nuevo examen visual) _(responsable: Optómetra / Recepcionista; 2. REQ FUNCIONALES fila 9)_

- **CUN 1 Atender paciente en consulta** — Examen Visual _(responsable: sin consignar; 2. REQ FUNCIONALES fila 16)_

- **CUN 1 Atender paciente en consulta** — Registra la fecha del examen visual _(responsable: Optómetra; 2. REQ FUNCIONALES fila 17)_

- **CUN 1 Atender paciente en consulta** — Evalua y registra medidas del ojo derecho _(responsable: Optómetra; 2. REQ FUNCIONALES fila 18)_

- **CUN 1 Atender paciente en consulta** — Evalua y registra medidas del ojo izquierdo _(responsable: Optómetra; 2. REQ FUNCIONALES fila 19)_

- **CUN 1 Atender paciente en consulta** — Evalua y registra distancia interpupilar _(responsable: Optómetra; 2. REQ FUNCIONALES fila 20)_

- **CUN 1 Atender paciente en consulta** — Evalua y registra adición, si corresponde _(responsable: Optómetra; 2. REQ FUNCIONALES fila 21)_

- **CUN 1 Atender paciente en consulta** — Registra material recomendado para los lentes _(responsable: Optómetra; 2. REQ FUNCIONALES fila 22)_

- **CUN 1 Atender paciente en consulta** — Redacta la receta oftálmica del paciente _(responsable: Optómetra; 2. REQ FUNCIONALES fila 23)_

- **CUN 1 Atender paciente en consulta** — Guarda una copia de la receta _(responsable: Optómetra; 2. REQ FUNCIONALES fila 24)_

- **CUN 1 Atender paciente en consulta** — Entrega la receta al paciente _(responsable: Optómetra / Recepcionista; 2. REQ FUNCIONALES fila 25)_

- **CUN 2_Vender productos y servicios** — Registrar venta directa _(responsable: sin consignar; 2. REQ FUNCIONALES fila 28)_

- **CUN 2_Vender productos y servicios** — Consultar productos del catálogo _(responsable: Vendedor; 2. REQ FUNCIONALES fila 29)_

- **CUN 2_Vender productos y servicios** — Seleccionar productos para la venta directa _(responsable: Vendedor; 2. REQ FUNCIONALES fila 30)_

- **CUN 2_Vender productos y servicios** — Registrar pago completo y seleccionar metodo de pago _(responsable: Vendedor; 2. REQ FUNCIONALES fila 31)_

- **CUN 2_Vender productos y servicios** — Confirmar venta directa _(responsable: Vendedor; 2. REQ FUNCIONALES fila 32)_

- **CUN 2_Vender productos y servicios** — Registrar venta de servicios _(responsable: Vendedor; 2. REQ FUNCIONALES fila 34)_

- **CUN 2_Vender productos y servicios** — Buscar o registrar cliente para servicio _(responsable: Vendedor; 2. REQ FUNCIONALES fila 35)_

- **CUN 2_Vender productos y servicios** — Seleccionar servicio brindado _(responsable: Vendedor; 2. REQ FUNCIONALES fila 36)_

- **CUN 2_Vender productos y servicios** — Registrar pago completo y seleccionar metodo de pago _(responsable: Vendedor; 2. REQ FUNCIONALES fila 37)_

- **CUN 2_Vender productos y servicios** — Confirmar venta de servicio _(responsable: sin consignar; 2. REQ FUNCIONALES fila 38)_

- **CUN 2_Vender productos y servicios** — Registrar venta con receta _(responsable: Vendedor; 2. REQ FUNCIONALES fila 40)_

- **CUN 2_Vender productos y servicios** — Buscar o seleccionar paciente _(responsable: Vendedor; 2. REQ FUNCIONALES fila 41)_

- **CUN 2_Vender productos y servicios** — Identificar tipo de receta _(responsable: Vendedor; 2. REQ FUNCIONALES fila 42)_

- **CUN 2_Vender productos y servicios** — Seleccionar receta interna o registrar receta externa _(responsable: Vendedor; 2. REQ FUNCIONALES fila 43)_

- **CUN 2_Vender productos y servicios** — Seleccionar montura por código _(responsable: Vendedor; 2. REQ FUNCIONALES fila 44)_

- **CUN 2_Vender productos y servicios** — Seleccionar tipo de cristal _(responsable: Vendedor; 2. REQ FUNCIONALES fila 45)_

- **CUN 2_Vender productos y servicios** — Seleccionar tratamiento del cristal _(responsable: Vendedor; 2. REQ FUNCIONALES fila 46)_

- **CUN 2_Vender productos y servicios** — Agregar productos extra, si corresponde _(responsable: Vendedor; 2. REQ FUNCIONALES fila 47)_

- **CUN 2_Vender productos y servicios** — Registrar adelanto o pago total _(responsable: Vendedor; 2. REQ FUNCIONALES fila 48)_

- **CUN 2_Vender productos y servicios** — Registrar fecha de entrega estimada _(responsable: Vendedor; 2. REQ FUNCIONALES fila 49)_

- **CUN 2_Vender productos y servicios** — Confirmar venta con receta _(responsable: Vendedor; 2. REQ FUNCIONALES fila 50)_

- **CUN 2_Vender productos y servicios** — Buscar venta con receta pendiente _(responsable: Vendedor; 2. REQ FUNCIONALES fila 52)_

- **CUN 2_Vender productos y servicios** — Revisar saldo pendiente de la venta con receta _(responsable: Vendedor; 2. REQ FUNCIONALES fila 53)_

- **CUN 2_Vender productos y servicios** — Registrar pago final y seleccionar metodo de pago _(responsable: Vendedor; 2. REQ FUNCIONALES fila 54)_

- **CUN 2_Vender productos y servicios** — Confirmar cancelación del saldo pendiente _(responsable: Vendedor; 2. REQ FUNCIONALES fila 55)_

- **CUN 2_Vender productos y servicios** — Historial de ventas _(responsable: Historial de ventas; 2. REQ FUNCIONALES fila 56)_

- **CUN 2_Vender productos y servicios** — Consultar historial de ventas _(responsable: Vendedor; 2. REQ FUNCIONALES fila 57)_

- **CUN 2_Vender productos y servicios** — Listar ventas registradas _(responsable: Vendedor; 2. REQ FUNCIONALES fila 58)_

- **CUN 2_Vender productos y servicios** — Buscar ventas _(responsable: Vendedor; 2. REQ FUNCIONALES fila 59)_

- **CUN 2_Vender productos y servicios** — Filtrar ventas por fecha, tipo o estado _(responsable: Vendedor; 2. REQ FUNCIONALES fila 60)_

- **CUN 2_Vender productos y servicios** — Revisar detalle de venta registrada _(responsable: Vendedor; 2. REQ FUNCIONALES fila 61)_

- **CUN 3 Hacer orden de trabajo** — Revisar pedidos pendientes de OT _(responsable: Dueño (Técnico); 2. REQ FUNCIONALES fila 63)_

- **CUN 3 Hacer orden de trabajo** — Generar orden de trabajo _(responsable: Dueño (Técnico); 2. REQ FUNCIONALES fila 64)_

- **CUN 3 Hacer orden de trabajo** — Registrar envío al laboratorio _(responsable: Dueño (Técnico) / Vendedor; 2. REQ FUNCIONALES fila 65)_

- **CUN 3 Hacer orden de trabajo** — Confirmar envío del pedido _(responsable: Dueño (Técnico) / Vendedor; 2. REQ FUNCIONALES fila 66)_

- **CUN 3 Hacer orden de trabajo** — Registrar recepción del producto terminado _(responsable: Dueño (Técnico) / Vendedor; 2. REQ FUNCIONALES fila 67)_

- **CUN 3 Hacer orden de trabajo** — Realizar control de calidad _(responsable: Dueño (Técnico); 2. REQ FUNCIONALES fila 68)_

- **CUN 3 Hacer orden de trabajo** — Registrar rechazo del lente cuando no cumple _(responsable: Dueño (Técnico); 2. REQ FUNCIONALES fila 69)_

- **CUN 3 Hacer orden de trabajo** — Registrar aprobación del lente cuando cumple _(responsable: Dueño (Técnico); 2. REQ FUNCIONALES fila 70)_

- **CUN 3 Hacer orden de trabajo** — Preparar pedido para entrega _(responsable: Vendedor; 2. REQ FUNCIONALES fila 71)_

- **CUN 3 Hacer orden de trabajo** — Notificar disponibilidad del pedido al paciente _(responsable: Vendedor; 2. REQ FUNCIONALES fila 72)_

- **CUN 3 Hacer orden de trabajo** — Entregar pedido al cliente _(responsable: Vendedor; 2. REQ FUNCIONALES fila 73)_

- **CUN 4 Controlar inventario** — Revisar catálogo de productos _(responsable: Dueño; 2. REQ FUNCIONALES fila 75)_

- **CUN 4 Controlar inventario** — Filtrar productos del catálogo _(responsable: Sistema; 2. REQ FUNCIONALES fila 76)_

- **CUN 4 Controlar inventario** — Registrar producto nuevo si no existe _(responsable: Dueño; 2. REQ FUNCIONALES fila 77)_

- **CUN 4 Controlar inventario** — Validar duplicidad del producto _(responsable: Sistema; 2. REQ FUNCIONALES fila 78)_

- **CUN 4 Controlar inventario** — Actualizar datos básicos del producto _(responsable: Dueño; 2. REQ FUNCIONALES fila 79)_

- **CUN 4 Controlar inventario** — Habilitar o deshabilitar producto para venta _(responsable: Dueño; 2. REQ FUNCIONALES fila 80)_

- **CUN 4 Controlar inventario** — Revisar stock físico disponible _(responsable: Administrador; 2. REQ FUNCIONALES fila 81)_

- **CUN 4 Controlar inventario** — Revisar datos de stock y disponibilidad _(responsable: Administrador; 2. REQ FUNCIONALES fila 82)_

- **CUN 4 Controlar inventario** — Registrar entrada de productos _(responsable: Administrador; 2. REQ FUNCIONALES fila 83)_

- **CUN 4 Controlar inventario** — Registrar ajuste por diferencia, perdida o dano _(responsable: Administrador; 2. REQ FUNCIONALES fila 84)_

- **CUN 4 Controlar inventario** — Validar cantidad ingresada _(responsable: Dueño; 2. REQ FUNCIONALES fila 85)_

- **CUN 4 Controlar inventario** — Validar que no quede stock negativo _(responsable: Sistema; 2. REQ FUNCIONALES fila 86)_

- **CUN 4 Controlar inventario** — Confirmar cantidad disponible _(responsable: Dueño; 2. REQ FUNCIONALES fila 87)_

- **CUN 4 Controlar inventario** — Guardar historial de entrada o ajuste _(responsable: Sistema; 2. REQ FUNCIONALES fila 88)_

- **CUN 4 Controlar inventario** — Consultar productos disponibles para venta _(responsable: Dueño; 2. REQ FUNCIONALES fila 89)_

- **CUN 4 Controlar inventario** — Seleccionar producto disponible desde ventas _(responsable: Dueño; 2. REQ FUNCIONALES fila 90)_

- **CUN 4 Controlar inventario** — Revisar productos con stock bajo o agotado _(responsable: Dueño; 2. REQ FUNCIONALES fila 91)_

- **CUN 4 Controlar inventario** — Consultar historial de movimientos _(responsable: Administrador; 2. REQ FUNCIONALES fila 92)_


## 7. Casos de uso identificados en filas de requerimientos
**Advertencia:** denominaciones y numeraciones `CU`/`CUS` se contradicen; se reproducen tal como están en la matriz. La hoja de priorización trae un catálogo distinto.


- **CUS01 - REGISTRAR PACIENTE** — 2. REQ FUNCIONALES fila 7

- **CUS02 - ACTUALIZAR DATOS PERSONALES DEL PACIENTE** — 2. REQ FUNCIONALES fila 8

- **CUS03 - CONSULTAR HISTORIAL CLÍNICO DEL PACIENTE** — 2. REQ FUNCIONALES fila 9

- **CUS04 - REGISTRAR EXAMEN VISUAL** — 2. REQ FUNCIONALES fila 17

- **CUS05 - REGISTRAR RECETA OFTÁLMICA** — 2. REQ FUNCIONALES fila 24

- **CUS05 -  REGISTRAR VENTA DIRECTA** — 2. REQ FUNCIONALES fila 27

- **CUS06 - REGISTRAR VENTA DE SERVICIOS** — 2. REQ FUNCIONALES fila 33

- **CUS07 -  REGISTRAR VENTA DE CON RECETA** — 2. REQ FUNCIONALES fila 39

- **CUS08 - REVISAR PAGO PENDIENTE** — 2. REQ FUNCIONALES fila 51

- **CUS09 - Consultar historial ventas** — 2. REQ FUNCIONALES fila 56

- **CU10 - GENERAR ORDEN DE TRABAJO** — 2. REQ FUNCIONALES fila 63

- **CU11 - REGISTRAR CONTROL DE CALIDAD** — 2. REQ FUNCIONALES fila 68

- **CU12 - NOTIFICAR DISPONIBILIDAD AL PACIENTE** — 2. REQ FUNCIONALES fila 71

- **CU13 - REGISTRAR ENTREGA DE PEDIDO** — 2. REQ FUNCIONALES fila 73

- **CUS14 - Registrar y actualizar productos del catalogo** — 2. REQ FUNCIONALES fila 75

- **CUS15 - Recepcionar en stock** — 2. REQ FUNCIONALES fila 81

- **CUS16 - Consultar productos disponibles y verificar stock para venta** — 2. REQ FUNCIONALES fila 89

- **CUS17 - INICIAR SESION** — 2. REQ FUNCIONALES fila 94

- **CUS18 - CERRAR SESION** — 2. REQ FUNCIONALES fila 95

- **CUS19 - Registrar y modificar usuarios** — 2. REQ FUNCIONALES fila 96

- **CUS20 - Asignar roles y configurar permisos de acceso** — 2. REQ FUNCIONALES fila 99

- **CUS21 - RECUPERAR CONTRASEÑA** — 2. REQ FUNCIONALES fila 101

- **CU22 -Configurar Datos de la Empresa** — 2. REQ FUNCIONALES fila 107

- **CU23 - Consultar y actualizar parámetros del sistema** — 2. REQ FUNCIONALES fila 111

- **CU24 - Configurar categorías, cristales, tratamientos, servicios y laboratorios** — 2. REQ FUNCIONALES fila 116

- **CU25 - Registrar y actualizar criterios de control de calidad** — 2. REQ FUNCIONALES fila 121

- **CU26 - Generar Reporte de Atención al Paciente** — 2. REQ FUNCIONALES fila 125

- **CU27 - Generar Reporte de Ventas.** — 2. REQ FUNCIONALES fila 127

- **CU28 -Generar Reporte de Laboratorio.** — 2. REQ FUNCIONALES fila 129

- **CU29 - Generar Reporte de Stock.** — 2. REQ FUNCIONALES fila 131

- **CUS17 - INICIAR SESION** — Hoja1 fila 6

- **CUS18 - CERRAR SESION** — Hoja1 fila 7

- **CUS19 - Registrar y modificar usuarios** — Hoja1 fila 8

- **CUS20 - Asignar roles y configurar permisos de acceso** — Hoja1 fila 11

- **CUS21 - RECUPERAR CONTRASEÑA** — Hoja1 fila 13

- **CU22 -Configurar Datos de la Empresa** — Hoja1 fila 19

- **CU23 - Consultar y actualizar parámetros del sistema** — Hoja1 fila 23

- **CU24 - Configurar categorías, cristales, tratamientos, servicios y laboratorios** — Hoja1 fila 28

- **CU25 - Registrar y actualizar criterios de control de calidad** — Hoja1 fila 33

- **CU26 - Generar Reporte de Atención al Paciente** — Hoja1 fila 37

- **CU27 - Generar Reporte de Ventas.** — Hoja1 fila 39

- **CU28 -Generar Reporte de Laboratorio.** — Hoja1 fila 41

- **CU29 - Generar Reporte de Stock.** — Hoja1 fila 43


### 7.1 Casos de uso de la priorización (otra nomenclatura)


- CU-1 — Gestionar cliente — nota 7.7 — PRIMARIO (fila 7)

- CU-2 — Agendar cita — nota 5.700000000000001 — SECUNDARIO (fila 8)

- CU-3 — Registrar atención del cliente — nota 7.2 — PRIMARIO (fila 9)

- CU-4 — Registrar refracción — nota 8.8 — PRIMARIO (fila 10)

- CU-5 — Generar constancia de atención — nota 4.700000000000001 — SECUNDARIO (fila 11)

- CU-6 — Registrar venta directa — nota 7.7 — PRIMARIO (fila 12)

- CU-7 — Registrar venta de servicios — nota 6.2 — SECUNDARIO (fila 13)

- CU-8 — Registrar venta con receta — nota 9.299999999999999 — PRIMARIO (fila 14)

- CU-9 — Gestionar pagos — nota 8.8 — PRIMARIO (fila 15)

- CU-10 — Generar Orden de Trabajo — nota 9 — PRIMARIO (fila 16)

- CU-11 — Registrar Control de Calidad — nota 8.200000000000001 — PRIMARIO (fila 17)

- CU-12 — Notificar Disponibilidad al Paciente — nota 5.5 — SECUNDARIO (fila 18)

- CU-13 — Registrar Entrega de Pedido — nota 7.500000000000001 — PRIMARIO (fila 19)

- CU-14 — Mandar Requerimiento de Compra — nota 6.7 — PRIMARIO (fila 20)

- CU-15 — Recepcionar Productos — nota 6.699999999999999 — PRIMARIO (fila 21)

- CU-16 — sin nombre — nota 0 — SECUNDARIO (fila 22)

- CU-17 — sin nombre — nota 0 — SECUNDARIO (fila 23)

- CU-18 — sin nombre — nota 0 — SECUNDARIO (fila 24)

- CU-19 — sin nombre — nota 0 — SECUNDARIO (fila 25)

- CU-20 — sin nombre — nota 0 — SECUNDARIO (fila 26)

- CU-21 — sin nombre — nota 0 — SECUNDARIO (fila 27)

- CU-22 — sin nombre — nota 0 — SECUNDARIO (fila 28)

- CU-23 — sin nombre — nota 0 — SECUNDARIO (fila 29)

- CU-24 — sin nombre — nota 0 — SECUNDARIO (fila 30)


## 8. Requerimientos funcionales: transcripción íntegra
**Alcance de extracción:** hoja principal + Hoja1 (37 filas espejo, no nuevos RF); se conserva fuente y actor en todas las entradas. La nomenclatura RF se repite con significados diferentes desde RF-83 al entrar en configuración: **no usar código RF como clave única**.


- **RF-2** [2. REQ FUNCIONALES fila 7; CUN 1 Atender paciente en consulta]: El sistema debe permitir registrar los datos personales del paciente | CUS: CUS01 - REGISTRAR PACIENTE | Actor: Recepcionista

- **RF-3** [2. REQ FUNCIONALES fila 8; CUN 1 Atender paciente en consulta]: El sistema debe permitir actualizar los datos personales del paciente | CUS: CUS02 - ACTUALIZAR DATOS PERSONALES DEL PACIENTE | Actor: Recepcionista

- **RF-4** [2. REQ FUNCIONALES fila 9; CUN 1 Atender paciente en consulta]: El sistema debe permitir buscar pacientes registrador por nombre, teléfono o DNI | CUS: CUS03 - CONSULTAR HISTORIAL CLÍNICO DEL PACIENTE | Actor: Recepcionista / Optómetra

- **RF-5** [2. REQ FUNCIONALES fila 10; CUN 1 Atender paciente en consulta]: El sistema debe permitir consultar los datos personales del paciente | CUS: sin asignación explícita | Actor: Recepcionista / Optómetra

- **RF-6** [2. REQ FUNCIONALES fila 11; CUN 1 Atender paciente en consulta]: El sistema debe permitir consultar los examenes anteriores del paciente | CUS: sin asignación explícita | Actor: Recepcionista / Optómetra

- **RF-7** [2. REQ FUNCIONALES fila 12; CUN 1 Atender paciente en consulta]: El sistema debe permitir consultar las medidas ópticas de examenes anteriores del paciente | CUS: sin asignación explícita | Actor: Recepcionista / Optómetra

- **RF-8** [2. REQ FUNCIONALES fila 13; CUN 1 Atender paciente en consulta]: El sistema debe permitir consultar el material recomendado de examenes anteriores del paciente | CUS: sin asignación explícita | Actor: Recepcionista / Optómetra

- **RF-9** [2. REQ FUNCIONALES fila 14; CUN 1 Atender paciente en consulta]: El sistema debe permitir consultar las observaciones de examenes anteriores del paciente | CUS: sin asignación explícita | Actor: Recepcionista / Optómetra

- **RF-10** [2. REQ FUNCIONALES fila 15; CUN 1 Atender paciente en consulta]: El sistema debe permitir consultar la receta de examenes anteriores del paciente | CUS: sin asignación explícita | Actor: Recepcionista / Optómetra

- **RF-12** [2. REQ FUNCIONALES fila 17; CUN 1 Atender paciente en consulta]: El sistema debe permitir seleccionar un paciente para iniciar el registro de un examen visual | CUS: CUS04 - REGISTRAR EXAMEN VISUAL | Actor: Optómetra

- **RF-13** [2. REQ FUNCIONALES fila 18; CUN 1 Atender paciente en consulta]: El sistema debe permitir registrar la esfera, cilindro, y eje del ojo derecho | CUS: sin asignación explícita | Actor: Optómetra

- **RF-14** [2. REQ FUNCIONALES fila 19; CUN 1 Atender paciente en consulta]: El sistema debe permitir registrar la esfera, cilindro, y eje del ojo izquierdo | CUS: sin asignación explícita | Actor: Optómetra

- **RF-15** [2. REQ FUNCIONALES fila 20; CUN 1 Atender paciente en consulta]: El sistema debe permitir registrar la distancia interpupilar del paciente | CUS: sin asignación explícita | Actor: Optómetra

- **RF-16** [2. REQ FUNCIONALES fila 21; CUN 1 Atender paciente en consulta]: El sistema debe permitir registrar la adición, si corresponde | CUS: sin asignación explícita | Actor: Optómetra

- **RF-17** [2. REQ FUNCIONALES fila 22; CUN 1 Atender paciente en consulta]: El sistema debe permitir registrar el material recomendado para los lentes | CUS: sin asignación explícita | Actor: Optómetra

- **RF-18** [2. REQ FUNCIONALES fila 23; CUN 1 Atender paciente en consulta]: El sistema debe guardar el examen visual en el historial del paciente | CUS: sin asignación explícita | Actor: Optómetra

- **RF-19** [2. REQ FUNCIONALES fila 24; CUN 1 Atender paciente en consulta]: El sistema debe generar una receta oftálmica interna, a partir del examen visual | CUS: CUS05 - REGISTRAR RECETA OFTÁLMICA | Actor: Optómetra

- **RF-20** [2. REQ FUNCIONALES fila 25; CUN 1 Atender paciente en consulta]: El sistema debe guardar la receta oftálmica en el historial del paciente | CUS: sin asignación explícita | Actor: Optómetra

- **RF-25** [2. REQ FUNCIONALES fila 28; CUN 2_Vender productos y servicios]: El sistema debe permitir registrar una venta directa de productos | CUS: sin asignación explícita | Actor: Vendedor

- **RF-26** [2. REQ FUNCIONALES fila 29; CUN 2_Vender productos y servicios]: El sistema debe permitir consultar los productos del catálogo | CUS: sin asignación explícita | Actor: Vendedor

- **RF-27** [2. REQ FUNCIONALES fila 30; CUN 2_Vender productos y servicios]: El sistema debe permitir seleccionar uno o varios productos del catálogo | CUS: sin asignación explícita | Actor: Vendedor

- **RF-28** [2. REQ FUNCIONALES fila 31; CUN 2_Vender productos y servicios]: El sistema debe permitir registrar el pago completo de la venta, seleccionando el método de pago | CUS: sin asignación explícita | Actor: Vendedor

- **RF-29** [2. REQ FUNCIONALES fila 32; CUN 2_Vender productos y servicios]: El sistema debe permitir confirmar la venta directa, generando el comprobante correspondiente | CUS: sin asignación explícita | Actor: Vendedor

- **RF-30** [2. REQ FUNCIONALES fila 34; CUN 2_Vender productos y servicios]: El sistema debe permitir registrar una venta asociada a un servicio | CUS: sin asignación explícita | Actor: Vendedor

- **RF-31** [2. REQ FUNCIONALES fila 35; CUN 2_Vender productos y servicios]: El sistema debe permitir buscar un cliente existente por documento/nombre o registrar uno nuevo si no existe en el sistema. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-32** [2. REQ FUNCIONALES fila 36; CUN 2_Vender productos y servicios]: El sistema debe permitir seleccionar el servicio brindado de una lista de servicios disponibles | CUS: sin asignación explícita | Actor: Vendedor

- **RF-33** [2. REQ FUNCIONALES fila 37; CUN 2_Vender productos y servicios]: El sistema debe permitir registrar el pago completo del servicio y seleccionar el método de pago. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-34** [2. REQ FUNCIONALES fila 38; CUN 2_Vender productos y servicios]: El sistema debe permitir confirmar la venta de servicio, generando el comprobante respectivo. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-35** [2. REQ FUNCIONALES fila 40; CUN 2_Vender productos y servicios]: El sistema debe permitir registrar una venta asociada a una receta médica | CUS: sin asignación explícita | Actor: Vendedor

- **RF-36** [2. REQ FUNCIONALES fila 41; CUN 2_Vender productos y servicios]: El sistema debe permitir buscar un paciente existente o seleccionarlo de la base de datos de pacientes. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-37** [2. REQ FUNCIONALES fila 42; CUN 2_Vender productos y servicios]: El sistema debe permitir identificar el tipo de receta (interna o externa) antes de continuar con la venta. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-38** [2. REQ FUNCIONALES fila 43; CUN 2_Vender productos y servicios]: El sistema debe permitir seleccionar una receta interna o registrar los datos de una receta externa | CUS: sin asignación explícita | Actor: Vendedor

- **RF-39** [2. REQ FUNCIONALES fila 44; CUN 2_Vender productos y servicios]: El sistema debe permitir seleccionar la montura por código desde el catálogo de productos. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-40** [2. REQ FUNCIONALES fila 45; CUN 2_Vender productos y servicios]: El sistema debe permitir seleccionar el tipo de cristal | CUS: sin asignación explícita | Actor: Vendedor

- **RF-41** [2. REQ FUNCIONALES fila 46; CUN 2_Vender productos y servicios]: El sistema debe permitir seleccionar el tratamiento del cristal | CUS: sin asignación explícita | Actor: Vendedor

- **RF-42** [2. REQ FUNCIONALES fila 47; CUN 2_Vender productos y servicios]: El sistema debe permitir agregar productos adicionales a la venta | CUS: sin asignación explícita | Actor: Vendedor

- **RF-43** [2. REQ FUNCIONALES fila 48; CUN 2_Vender productos y servicios]: El sistema debe permitir registrar un pago de adelanto  o el pago total de la venta con receta. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-44** [2. REQ FUNCIONALES fila 49; CUN 2_Vender productos y servicios]: El sistema debe permitir registrar la fecha estimada de entrega del producto, | CUS: sin asignación explícita | Actor: Vendedor

- **RF-45** [2. REQ FUNCIONALES fila 50; CUN 2_Vender productos y servicios]: El sistema debe permitir confirmar la venta con receta, generando una orden de trabajo | CUS: sin asignación explícita | Actor: Vendedor

- **RF-46** [2. REQ FUNCIONALES fila 52; CUN 2_Vender productos y servicios]: El sistema debe permitir buscar una venta con receta que tenga saldo pendiente, | CUS: sin asignación explícita | Actor: Vendedor

- **RF-47** [2. REQ FUNCIONALES fila 53; CUN 2_Vender productos y servicios]: El sistema debe permitir revisar el saldo pendiente de la venta con receta encontrada | CUS: sin asignación explícita | Actor: Vendedor

- **RF-48** [2. REQ FUNCIONALES fila 54; CUN 2_Vender productos y servicios]: El sistema debe permitir registrar el pago final del saldo pendiente, seleccionando el método de pago. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-49** [2. REQ FUNCIONALES fila 55; CUN 2_Vender productos y servicios]: El sistema debe permitir confirmar la cancelación del saldo pendiente. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-50** [2. REQ FUNCIONALES fila 57; CUN 2_Vender productos y servicios]: El sistema debe permitir acceder al historial de ventas registradas. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-51** [2. REQ FUNCIONALES fila 58; CUN 2_Vender productos y servicios]: El sistema debe mostrar las ventas registradas con código, fecha, cliente, tipo, total y estado. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-52** [2. REQ FUNCIONALES fila 59; CUN 2_Vender productos y servicios]: El sistema debe permitir buscar ventas por código, cliente o DNI. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-53** [2. REQ FUNCIONALES fila 60; CUN 2_Vender productos y servicios]: El sistema debe permitir filtrar ventas por tipo de venta. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-54** [2. REQ FUNCIONALES fila 61; CUN 2_Vender productos y servicios]: El sistema debe permitir filtrar ventas por estado de venta. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-55** [2. REQ FUNCIONALES fila 63; CUN 3 Hacer orden de trabajo]: El sistema debe permitir al técnico visualizar y filtrar pedidos con estado “Pendiente de OT”. | CUS: CU10 - GENERAR ORDEN DE TRABAJO | Actor: Vendedor

- **RF-56** [2. REQ FUNCIONALES fila 64; CUN 3 Hacer orden de trabajo]: El sistema debe generar una OT con datos del paciente, receta, medidas y especificaciones del lente. | CUS: sin asignación explícita | Actor: Técnico, Vendedor

- **RF-57** [2. REQ FUNCIONALES fila 65; CUN 3 Hacer orden de trabajo]: El sistema debe permitir registrar la salida de la montura y la OT hacia el laboratorio externo. | CUS: sin asignación explícita | Actor: Técnico, Vendedor

- **RF-58** [2. REQ FUNCIONALES fila 66; CUN 3 Hacer orden de trabajo]: El sistema debe permitir cambiar el estado del pedido a “Enviado a Laboratorio”. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-59** [2. REQ FUNCIONALES fila 67; CUN 3 Hacer orden de trabajo]: El sistema debe permitir registrar la recepción del lente armado devuelto por el laboratorio. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-60** [2. REQ FUNCIONALES fila 68; CUN 3 Hacer orden de trabajo]: El sistema debe permitir registrar los resultados de lensometría del producto terminado | CUS: CU11 - REGISTRAR CONTROL DE CALIDAD | Actor: Vendedor

- **RF-61** [2. REQ FUNCIONALES fila 69; CUN 3 Hacer orden de trabajo]: El sistema debe permitir marcar el pedido como “Retrasado” cuando el lente no cumple la receta. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-62** [2. REQ FUNCIONALES fila 70; CUN 3 Hacer orden de trabajo]: El sistema debe permitir aprobar el control de calidad cuando las medidas coinciden con la receta. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-63** [2. REQ FUNCIONALES fila 71; CUN 3 Hacer orden de trabajo]: El sistema debe actualizar el pedido a “Listo para entrega” después de aprobar el control de calidad. | CUS: CU12 - NOTIFICAR DISPONIBILIDAD AL PACIENTE | Actor: Vendedor

- **RF-64** [2. REQ FUNCIONALES fila 72; CUN 3 Hacer orden de trabajo]: El sistema debe registrar que fue notificado al cliente | CUS: sin asignación explícita | Actor: Vendedor

- **RF-65** [2. REQ FUNCIONALES fila 73; CUN 3 Hacer orden de trabajo]: El sistema debe permitir al vendedor consultar el estado del pedido antes de entregarlo. | CUS: CU13 - REGISTRAR ENTREGA DE PEDIDO | Actor: Vendedor

- **RF-66** [2. REQ FUNCIONALES fila 75; CUN 4 Controlar inventario]: El sistema debe permitir consultar productos registrados en el catálogo. | CUS: CUS14 - Registrar y actualizar productos del catalogo | Actor: Vendedor

- **RF-67** [2. REQ FUNCIONALES fila 76; CUN 4 Controlar inventario]: El sistema debe permitir filtrar productos por nombre, código, categoría o estado. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-68** [2. REQ FUNCIONALES fila 77; CUN 4 Controlar inventario]: El sistema debe permitir registrar productos nuevos en el catálogo. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-69** [2. REQ FUNCIONALES fila 78; CUN 4 Controlar inventario]: El sistema debe validar que el producto no exista previamente antes de guardarlo. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-70** [2. REQ FUNCIONALES fila 79; CUN 4 Controlar inventario]: El sistema debe permitir actualizar datos básicos del producto. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-71** [2. REQ FUNCIONALES fila 80; CUN 4 Controlar inventario]: El sistema debe permitir habilitar o deshabilitar productos para venta. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-72** [2. REQ FUNCIONALES fila 81; CUN 4 Controlar inventario]: El sistema debe permitir consultar el stock actual de un producto | CUS: CUS15 - Recepcionar en stock | Actor: Vendedor

- **RF-73** [2. REQ FUNCIONALES fila 82; CUN 4 Controlar inventario]: El sistema debe mostrar stock actual, stock minimo, precio y disponibilidad del producto. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-74** [2. REQ FUNCIONALES fila 83; CUN 4 Controlar inventario]: El sistema debe permitir registrar entrada de productos al stock. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-75** [2. REQ FUNCIONALES fila 84; CUN 4 Controlar inventario]: El sistema debe permitir registrar ajustes de stock por diferencia, perdida o dano. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-76** [2. REQ FUNCIONALES fila 85; CUN 4 Controlar inventario]: El sistema debe validar que la cantidad ingresada sea mayor a cero. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-77** [2. REQ FUNCIONALES fila 86; CUN 4 Controlar inventario]: El sistema debe impedir que un ajuste de salida deje stock negativo. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-78** [2. REQ FUNCIONALES fila 87; CUN 4 Controlar inventario]: El sistema debe actualizar la cantidad disponible despues de una entrada o ajuste. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-79** [2. REQ FUNCIONALES fila 88; CUN 4 Controlar inventario]: El sistema debe guardar el historial de entradas y ajustes de stock. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-80** [2. REQ FUNCIONALES fila 89; CUN 4 Controlar inventario]: El sistema debe mostrar en ventas solo productos activos y disponibles. | CUS: CUS16 - Consultar productos disponibles y verificar stock para venta | Actor: Vendedor

- **RF-81** [2. REQ FUNCIONALES fila 90; CUN 4 Controlar inventario]: El sistema debe validar disponibilidad cuando se seleccione un producto para venta. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-82** [2. REQ FUNCIONALES fila 91; CUN 4 Controlar inventario]: El sistema debe permitir consultar productos con stock bajo o agotado. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-83** [2. REQ FUNCIONALES fila 92; CUN 4 Controlar inventario]: El sistema debe permitir consultar el historial de movimientos de un producto. | CUS: sin asignación explícita | Actor: Vendedor

- **RF-84** [2. REQ FUNCIONALES fila 94; CUN 5 Seguridad]: El sistema debe permitir iniciar sesión mediante usuario y contraseña. | CUS: CUS17 - INICIAR SESION | Actor: Administrador

- **RF-85** [2. REQ FUNCIONALES fila 95; CUN 5 Seguridad]: El sistema debe permitir cerrar sesión de forma segura. | CUS: CUS18 - CERRAR SESION | Actor: Administrador

- **RF-86** [2. REQ FUNCIONALES fila 96; CUN 5 Seguridad]: El sistema debe permitir registrar nuevos usuarios. | CUS: CUS19 - Registrar y modificar usuarios | Actor: Administrador

- **RF-87** [2. REQ FUNCIONALES fila 97; CUN 5 Seguridad]: El sistema debe permitir actualizar los datos de un usuario. | CUS: sin asignación explícita | Actor: Administrador

- **RF-88** [2. REQ FUNCIONALES fila 98; CUN 5 Seguridad]: El sistema debe permitir desactivar usuarios. | CUS: sin asignación explícita | Actor: Administrador

- **RF-89** [2. REQ FUNCIONALES fila 99; CUN 5 Seguridad]: El sistema debe permitir asignar un rol a cada usuario. | CUS: CUS20 - Asignar roles y configurar permisos de acceso | Actor: Administrador

- **RF-90** [2. REQ FUNCIONALES fila 100; CUN 5 Seguridad]: El sistema debe permitir asignar permisos a cada rol. | CUS: sin asignación explícita | Actor: Administrador

- **RF-91** [2. REQ FUNCIONALES fila 101; CUN 5 Seguridad]: El sistema debe permitir solicitar la recuperación de contraseña. | CUS: CUS21 - RECUPERAR CONTRASEÑA | Actor: Administrador

- **RF-92** [2. REQ FUNCIONALES fila 102; CUN 5 Seguridad]: El sistema debe permitir ingresar usuario o correo registrado para recuperar la contraseña. | CUS: sin asignación explícita | Actor: Administrador

- **RF-93** [2. REQ FUNCIONALES fila 103; CUN 5 Seguridad]: El sistema debe validar la cuenta y el código de recuperación. | CUS: sin asignación explícita | Actor: Administrador

- **RF-94** [2. REQ FUNCIONALES fila 104; CUN 5 Seguridad]: El sistema debe permitir registrar una nueva contraseña | CUS: sin asignación explícita | Actor: Administrador

- **RF-95** [2. REQ FUNCIONALES fila 105; CUN 5 Seguridad]: El sistema debe actualizar la contraseña del usuario. | CUS: sin asignación explícita | Actor: Administrador

- **RF-83** [2. REQ FUNCIONALES fila 107; CUN 6 Gestionar Configuración]: El sistema debe permitir registrar los datos de la empresa la primera vez que se configura. | CUS: CU22 -Configurar Datos de la Empresa | Actor: Administrador

- **RF-84** [2. REQ FUNCIONALES fila 108; CUN 6 Gestionar Configuración]: El sistema debe permitir editar los datos de la empresa (razón social, RUC, dirección, teléfono, correo, logo). | CUS: sin asignación explícita | Actor: Administrador

- **RF-85** [2. REQ FUNCIONALES fila 109; CUN 6 Gestionar Configuración]: El sistema debe validar que el RUC tenga 11 dígitos antes de guardar. | CUS: sin asignación explícita | Actor: Administrador

- **RF-86** [2. REQ FUNCIONALES fila 110; CUN 6 Gestionar Configuración]: El sistema debe impedir la creación de un segundo registro de empresa, ya que solo debe existir uno. | CUS: sin asignación explícita | Actor: Administrador

- **RF-87** [2. REQ FUNCIONALES fila 111; CUN 6 Gestionar Configuración]: El sistema debe listar los parámetros configurables con su clave, valor y descripción. | CUS: CU23 - Consultar y actualizar parámetros del sistema | Actor: Administrador

- **RF-88** [2. REQ FUNCIONALES fila 112; CUN 6 Gestionar Configuración]: El sistema debe permitir editar el valor de un parámetro existente. | CUS: sin asignación explícita | Actor: Administrador

- **RF-89** [2. REQ FUNCIONALES fila 113; CUN 6 Gestionar Configuración]: El sistema debe validar que el nuevo valor sea compatible con el tipo de dato esperado por el parámetro. | CUS: sin asignación explícita | Actor: Administrador

- **RF-90** [2. REQ FUNCIONALES fila 114; CUN 6 Gestionar Configuración]: El sistema debe aplicar el nuevo valor del parámetro a las validaciones de los demás módulos de forma inmediata. | CUS: sin asignación explícita | Actor: Administrador

- **RF-91** [2. REQ FUNCIONALES fila 116; CUN 7 Gestionar Mantenimiento]: El sistema debe permitir registrar una nueva categoría de producto, validando que el nombre no esté duplicado. | CUS: CU24 - Configurar categorías, cristales, tratamientos, servicios y laboratorios | Actor: Administrador

- **RF-92** [2. REQ FUNCIONALES fila 117; CUN 7 Gestionar Mantenimiento]: El sistema debe permitir editar el nombre, la descripción y el estado de una categoría de producto. | CUS: sin asignación explícita | Actor: Administrador

- **RF-93** [2. REQ FUNCIONALES fila 118; CUN 7 Gestionar Mantenimiento]: El sistema debe permitir registrar y editar tipos de cristal y tratamientos disponibles para venta con receta. | CUS: sin asignación explícita | Actor: Administrador

- **RF-94** [2. REQ FUNCIONALES fila 119; CUN 7 Gestionar Mantenimiento]: El sistema debe permitir registrar y editar servicios ofrecidos por la óptica, incluyendo su precio referencial. | CUS: sin asignación explícita | Actor: Administrador

- **RF-95** [2. REQ FUNCIONALES fila 120; CUN 7 Gestionar Mantenimiento]: El sistema debe permitir registrar y editar laboratorios externos, incluyendo datos de contacto. | CUS: sin asignación explícita | Actor: Administrador

- **RF-96** [2. REQ FUNCIONALES fila 121; CUN 7 Gestionar Mantenimiento]: El sistema debe permitir registrar y editar criterios de control de calidad, incluyendo unidad de medida y tolerancia por defecto. | CUS: CU25 - Registrar y actualizar criterios de control de calidad | Actor: Administrador

- **RF-97** [2. REQ FUNCIONALES fila 122; CUN 7 Gestionar Mantenimiento]: El sistema debe impedir desactivar un catálogo que tenga registros activos dependientes (por ejemplo, una categoría con productos activos). | CUS: sin asignación explícita | Actor: Administrador

- **RF-98** [2. REQ FUNCIONALES fila 123; CUN 7 Gestionar Mantenimiento]: El sistema debe mostrar mensajes claros cuando se intente registrar un elemento duplicado en cualquier catálogo. | CUS: sin asignación explícita | Actor: Administrador

- **RF-99** [2. REQ FUNCIONALES fila 125; CUN 8 - Reportes]: El sistema debe permitir visualizar el reporte de pacientes atendidos, filtrado por rango de fechas. | CUS: CU26 - Generar Reporte de Atención al Paciente | Actor: Administrador

- **RF-100** [2. REQ FUNCIONALES fila 126; CUN 8 - Reportes]: El sistema debe permitir filtrar el reporte de atenciones por optometrista y tipo de atención (examen, receta). | CUS: sin asignación explícita | Actor: Administrador

- **RF-101** [2. REQ FUNCIONALES fila 127; CUN 8 - Reportes]: El sistema debe permitir visualizar el reporte de ventas, filtrado por periodo, tipo de venta y estado de pago. | CUS: CU27 - Generar Reporte de Ventas. | Actor: Administrador

- **RF-102** [2. REQ FUNCIONALES fila 128; CUN 8 - Reportes]: El sistema debe calcular el total vendido, los pagos recibidos y los saldos pendientes en el periodo consultado. | CUS: sin asignación explícita | Actor: Administrador

- **RF-103** [2. REQ FUNCIONALES fila 129; CUN 8 - Reportes]: El sistema debe permitir visualizar el reporte de pedidos de laboratorio, filtrado por estado (pendiente, enviado, listo, entregado, retrasado). | CUS: CU28 -Generar Reporte de Laboratorio. | Actor: Administrador

- **RF-104** [2. REQ FUNCIONALES fila 130; CUN 8 - Reportes]: El sistema debe calcular el tiempo promedio de entrega y la cantidad de pedidos retrasados en el periodo consultado | CUS: sin asignación explícita | Actor: Administrador

- **RF-105** [2. REQ FUNCIONALES fila 131; CUN 8 - Reportes]: El sistema debe permitir visualizar el reporte de productos con stock bajo o agotado. | CUS: CU29 - Generar Reporte de Stock. | Actor: Administrador

- **RF-106** [2. REQ FUNCIONALES fila 132; CUN 8 - Reportes]: El sistema debe permitir visualizar el reporte de movimientos de stock (entradas, salidas, ajustes) filtrado por periodo y producto. | CUS: sin asignación explícita | Actor: Administrador

- **RF-107** [2. REQ FUNCIONALES fila 133; CUN 8 - Reportes]: El sistema debe permitir exportar cualquiera de los reportes a PDF o Excel. | CUS: sin asignación explícita | Actor: Administrador

- **RF-84** [Hoja1 fila 6; CUN 5 Seguridad]: El sistema debe permitir iniciar sesión mediante usuario y contraseña. | CUS: CUS17 - INICIAR SESION | Actor: Administrador

- **RF-85** [Hoja1 fila 7; CUN 5 Seguridad]: El sistema debe permitir cerrar sesión de forma segura. | CUS: CUS18 - CERRAR SESION | Actor: Administrador

- **RF-86** [Hoja1 fila 8; CUN 5 Seguridad]: El sistema debe permitir registrar nuevos usuarios. | CUS: CUS19 - Registrar y modificar usuarios | Actor: Administrador

- **RF-87** [Hoja1 fila 9; CUN 5 Seguridad]: El sistema debe permitir actualizar los datos de un usuario. | CUS: sin asignación explícita | Actor: Administrador

- **RF-88** [Hoja1 fila 10; CUN 5 Seguridad]: El sistema debe permitir desactivar usuarios. | CUS: sin asignación explícita | Actor: Administrador

- **RF-89** [Hoja1 fila 11; CUN 5 Seguridad]: El sistema debe permitir asignar un rol a cada usuario. | CUS: CUS20 - Asignar roles y configurar permisos de acceso | Actor: Administrador

- **RF-90** [Hoja1 fila 12; CUN 5 Seguridad]: El sistema debe permitir asignar permisos a cada rol. | CUS: sin asignación explícita | Actor: Administrador

- **RF-91** [Hoja1 fila 13; CUN 5 Seguridad]: El sistema debe permitir solicitar la recuperación de contraseña. | CUS: CUS21 - RECUPERAR CONTRASEÑA | Actor: Administrador

- **RF-92** [Hoja1 fila 14; CUN 5 Seguridad]: El sistema debe permitir ingresar usuario o correo registrado para recuperar la contraseña. | CUS: sin asignación explícita | Actor: Administrador

- **RF-93** [Hoja1 fila 15; CUN 5 Seguridad]: El sistema debe validar la cuenta y el código de recuperación. | CUS: sin asignación explícita | Actor: Administrador

- **RF-94** [Hoja1 fila 16; CUN 5 Seguridad]: El sistema debe permitir registrar una nueva contraseña | CUS: sin asignación explícita | Actor: Administrador

- **RF-95** [Hoja1 fila 17; CUN 5 Seguridad]: El sistema debe actualizar la contraseña del usuario. | CUS: sin asignación explícita | Actor: Administrador

- **RF-83** [Hoja1 fila 19; CUN 6 Gestionar Configuración]: El sistema debe permitir registrar los datos de la empresa la primera vez que se configura. | CUS: CU22 -Configurar Datos de la Empresa | Actor: Administrador

- **RF-84** [Hoja1 fila 20; CUN 6 Gestionar Configuración]: El sistema debe permitir editar los datos de la empresa (razón social, RUC, dirección, teléfono, correo, logo). | CUS: sin asignación explícita | Actor: Administrador

- **RF-85** [Hoja1 fila 21; CUN 6 Gestionar Configuración]: El sistema debe validar que el RUC tenga 11 dígitos antes de guardar. | CUS: sin asignación explícita | Actor: Administrador

- **RF-86** [Hoja1 fila 22; CUN 6 Gestionar Configuración]: El sistema debe impedir la creación de un segundo registro de empresa, ya que solo debe existir uno. | CUS: sin asignación explícita | Actor: Administrador

- **RF-87** [Hoja1 fila 23; CUN 6 Gestionar Configuración]: El sistema debe listar los parámetros configurables con su clave, valor y descripción. | CUS: CU23 - Consultar y actualizar parámetros del sistema | Actor: Administrador

- **RF-88** [Hoja1 fila 24; CUN 6 Gestionar Configuración]: El sistema debe permitir editar el valor de un parámetro existente. | CUS: sin asignación explícita | Actor: Administrador

- **RF-89** [Hoja1 fila 25; CUN 6 Gestionar Configuración]: El sistema debe validar que el nuevo valor sea compatible con el tipo de dato esperado por el parámetro. | CUS: sin asignación explícita | Actor: Administrador

- **RF-90** [Hoja1 fila 26; CUN 6 Gestionar Configuración]: El sistema debe aplicar el nuevo valor del parámetro a las validaciones de los demás módulos de forma inmediata. | CUS: sin asignación explícita | Actor: Administrador

- **RF-91** [Hoja1 fila 28; CUN 7 Gestionar Mantenimiento]: El sistema debe permitir registrar una nueva categoría de producto, validando que el nombre no esté duplicado. | CUS: CU24 - Configurar categorías, cristales, tratamientos, servicios y laboratorios | Actor: Administrador

- **RF-92** [Hoja1 fila 29; CUN 7 Gestionar Mantenimiento]: El sistema debe permitir editar el nombre, la descripción y el estado de una categoría de producto. | CUS: sin asignación explícita | Actor: Administrador

- **RF-93** [Hoja1 fila 30; CUN 7 Gestionar Mantenimiento]: El sistema debe permitir registrar y editar tipos de cristal y tratamientos disponibles para venta con receta. | CUS: sin asignación explícita | Actor: Administrador

- **RF-94** [Hoja1 fila 31; CUN 7 Gestionar Mantenimiento]: El sistema debe permitir registrar y editar servicios ofrecidos por la óptica, incluyendo su precio referencial. | CUS: sin asignación explícita | Actor: Administrador

- **RF-95** [Hoja1 fila 32; CUN 7 Gestionar Mantenimiento]: El sistema debe permitir registrar y editar laboratorios externos, incluyendo datos de contacto. | CUS: sin asignación explícita | Actor: Administrador

- **RF-96** [Hoja1 fila 33; CUN 7 Gestionar Mantenimiento]: El sistema debe permitir registrar y editar criterios de control de calidad, incluyendo unidad de medida y tolerancia por defecto. | CUS: CU25 - Registrar y actualizar criterios de control de calidad | Actor: Administrador

- **RF-97** [Hoja1 fila 34; CUN 7 Gestionar Mantenimiento]: El sistema debe impedir desactivar un catálogo que tenga registros activos dependientes (por ejemplo, una categoría con productos activos). | CUS: sin asignación explícita | Actor: Administrador

- **RF-98** [Hoja1 fila 35; CUN 7 Gestionar Mantenimiento]: El sistema debe mostrar mensajes claros cuando se intente registrar un elemento duplicado en cualquier catálogo. | CUS: sin asignación explícita | Actor: Administrador

- **RF-99** [Hoja1 fila 37; CUN 8 - Reportes]: El sistema debe permitir visualizar el reporte de pacientes atendidos, filtrado por rango de fechas. | CUS: CU26 - Generar Reporte de Atención al Paciente | Actor: Administrador

- **RF-100** [Hoja1 fila 38; CUN 8 - Reportes]: El sistema debe permitir filtrar el reporte de atenciones por optometrista y tipo de atención (examen, receta). | CUS: sin asignación explícita | Actor: Administrador

- **RF-101** [Hoja1 fila 39; CUN 8 - Reportes]: El sistema debe permitir visualizar el reporte de ventas, filtrado por periodo, tipo de venta y estado de pago. | CUS: CU27 - Generar Reporte de Ventas. | Actor: Administrador

- **RF-102** [Hoja1 fila 40; CUN 8 - Reportes]: El sistema debe calcular el total vendido, los pagos recibidos y los saldos pendientes en el periodo consultado. | CUS: sin asignación explícita | Actor: Administrador

- **RF-103** [Hoja1 fila 41; CUN 8 - Reportes]: El sistema debe permitir visualizar el reporte de pedidos de laboratorio, filtrado por estado (pendiente, enviado, listo, entregado, retrasado). | CUS: CU28 -Generar Reporte de Laboratorio. | Actor: Administrador

- **RF-104** [Hoja1 fila 42; CUN 8 - Reportes]: El sistema debe calcular el tiempo promedio de entrega y la cantidad de pedidos retrasados en el periodo consultado | CUS: sin asignación explícita | Actor: Administrador

- **RF-105** [Hoja1 fila 43; CUN 8 - Reportes]: El sistema debe permitir visualizar el reporte de productos con stock bajo o agotado. | CUS: CU29 - Generar Reporte de Stock. | Actor: Administrador

- **RF-106** [Hoja1 fila 44; CUN 8 - Reportes]: El sistema debe permitir visualizar el reporte de movimientos de stock (entradas, salidas, ajustes) filtrado por periodo y producto. | CUS: sin asignación explícita | Actor: Administrador

- **RF-107** [Hoja1 fila 45; CUN 8 - Reportes]: El sistema debe permitir exportar cualquiera de los reportes a PDF o Excel. | CUS: sin asignación explícita | Actor: Administrador


## 9. Reglas, restricciones y no funcionales
- La hoja registra **RN01–RN10** y **RES01–RES21** como identificadores; muchas definiciones son `Sin definir`. No inferir sus significados.
- Regla textual 1: si hay deuda pendiente, **bloquear la entrega**.
- Regla textual 2: si la deuda es cero, permitir marcar **Entregado** y finalizar.
- La hoja RNF presenta **RNF01–RNF20** sin criterios de aceptación medibles completos. Categorías: físicos, restricciones de diseño, soporte y uso.
- Atributos de calidad conversados: integridad, rendimiento, auditabilidad; la meta de respuesta de 3 segundos figuró como evidencia propuesta y debe formalizarse.
- La matriz de trazabilidad incluida en Excel prácticamente no está poblada: no permite demostrar cobertura RF–CUS–test.


## 10. DDL: decisiones del modelo discutidas y comprobaciones obligatorias
- Relación empleado–usuario obligatoria y 1:1; usuario sin empleado no permitido.
- Persona–documento 1:1 y examen–receta 1:1 fueron reglas en la versión DDL revisada; verificar si siguen presentes.
- Orden de trabajo enlazada a venta mediante id_venta en órdenes.
- Ventas, pagos, movimientos, exámenes, órdenes y control de calidad con trazabilidad de empleado cuando aplique.
- Venta directa y servicio con pago completo; venta con receta permite adelanto y saldo.
- El estado de pago debe derivarse de PAGOS según normalización discutida; respetar implementación DDL, evitar doble fuente de verdad.
- Mantener precio histórico de tratamientos/ventas y unicidad de combinaciones relevantes.
- Auditoría o triggers para cambios sujetos a trazabilidad; no agregar sin ver el esquema.
- Productos activos/disponibles; entradas/ajustes con historial, cantidades positivas y sin stock negativo. Control de concurrencia imprescindible (bloqueo/fila/versionado, según stack real).
- Revisar controversia `parametro_sistema` tabla de clave/valor vs columnas dedicadas; prevalece el DDL aprobado.


## 11. Inconsistencias y bloqueos detectados
1. **Duplicación documental**: `Hoja1` copia parte de seguridad, configuración, mantenimiento y reportes de la hoja principal.
2. **Reutilización semántica de códigos RF**: RF-83 es historial de inventario y también configuración de empresa; RF-84 a RF-95 abarcan seguridad y también configuración/mantenimiento.
3. **Colisión CUS05/CUS06**: CUS05 figura tanto para receta oftálmica como para venta directa; CUS06 en matriz es venta de servicios, en priorización `CU-6` es venta directa y en conversaciones se denominó CUS06 venta directa. **Bloquea la trazabilidad sin normalización.**
4. Casos CU-16 a CU-24 de priorización están vacíos, pero sí hay CUS17 a CU29 en la hoja funcional.
5. Priorización trae `Agendar cita`, `Registrar refracción`, `Mandar requerimiento de compra` que carecen de especificación funcional clara/equivalente en el detalle principal.
6. Los renglones de calidad muestran ceros por defecto; no constituyen una evaluación aprobada ni cobertura demostrada.
7. En CUN3 se registran Dueño/Técnico como ejecutor pero Vendedor como actor del caso; verificar seguridad antes de usar roles.
8. Faltan versión exacta Java/Spring/Angular, dependencias reales, configuración Oracle, migraciones, tests y contratos API.


## 12. Flujo Git del equipo y fases de backend
1. Corregir conexión real de backend a Oracle y comprobar lectura sencilla sin alterar datos.
2. Auditar entidades/mappings vs DDL definitivo.
3. Implementar seguridad y catálogos compartidos con contratos estables.
4. Entregar verticales independientes (pacientes; exámenes/recetas; inventario; ventas/pagos; órdenes/entrega; configuración/reportes) por PR y con pruebas.
5. Cada integrante debe explicar servicios, decisiones, consultas, validaciones y pruebas de su propio cambio.

```bash
git switch develop
git pull origin develop
git switch -c feature/<modulo>-<funcionalidad>
# implementar, ejecutar tests, revisar diff
git add <archivos>
git commit -m "feat(modulo): describir funcionalidad"
git push -u origin feature/<modulo>-<funcionalidad>
# abrir PR hacia develop y obtener revisión
```


## 13. Archivos adicionales imprescindibles para Antigravity
- DDL oficial completo final (archivo .sql y parche de constraints realmente aplicado).
- Backend real (`pom.xml`, src/, properties de ejemplo SIN secretos, perfiles, tests).
- Frontend Angular real (`package.json`, angular.json, src/).
- Matriz de permisos validada rol × recurso × acción.
- Contratos API existentes si los hubiera.
- Reglas RN/RES y criterios RNF faltantes, junto con una tabla maestra de códigos RF/CUS corregidos.
- Plan de desarrollo backend ya elaborado en la otra conversación, a adjuntar como documento independiente.


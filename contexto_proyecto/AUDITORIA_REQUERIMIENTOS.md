# Auditoría de requerimientos y control de versiones — Óptica Geo Visión

**Fecha:** 20 de octubre de 2026.

## Límite de verificación

Se dispone de una sola versión identificable de la plantilla, `3. Plantilla de requerimientos V3 (3).xlsx`, la misma fuente que sustenta los documentos anteriores. **No se dispone de una segunda plantilla nueva que permita verificar qué celdas cambiaron con respecto a la versión anterior.** Por tanto, lo siguiente es una auditoría estructural del archivo disponible, no un diff entre dos versiones.

**Huella SHA-256 del archivo disponible:** `09b8f368e01b018db3a0f366b3406c7b989d5b1903dd27d8b4d6dfb30432fe2a`

En una próxima actualización, comparar esta huella con la de la copia nueva y producir una comparación celda a celda por hoja, fila y columna (altas, bajas, ediciones y cambios de prioridad). Nunca declarar 'sin cambios' solo por igualdad del nombre del archivo.

## Hallazgos verificables del archivo disponible

| Control | Resultado |
|---|---|
| Filas RF en `2. REQ FUNCIONALES` | **114** |
| Filas RF en `Hoja1` | **37** |
| Filas RF entre ambas hojas | **151** |
| Filas de `Hoja1` duplicadas exactamente por código + descripción en hoja principal | **37/37** |
| Definiciones RF distintas en hoja principal | **114** |
| Códigos RF distintos en hoja principal | **101** |
| Códigos RF conflictivos con 2 descripciones distintas | **13** |
| Código RF no visible en la secuencia 1–107 | **RF-1, RF-11, RF-21, RF-22, RF-23, RF-24** |
| Casos de uso `CU-` con nombre en priorización | **15** |
| Casos de uso `CU-` sin nombre en priorización | **9** |

La ausencia de códigos de RF intermedios no demuestra automáticamente que falten funcionalidades. Requiere confirmar si fueron descartados, fusionados, renumerados o si son errores de la plantilla.

### Colisiones de identificadores

Los códigos `RF-83, RF-84, RF-85, RF-86, RF-87, RF-88, RF-89, RF-90, RF-91, RF-92, RF-93, RF-94, RF-95` están asignados a requisitos diferentes en la hoja principal. Por ejemplo:
- `RF-83` inventario: consultar historial de movimientos; `RF-83` configuración: registrar datos de la empresa.
- `RF-89` seguridad: asignar rol a usuario; `RF-89` configuración: validar tipo de dato de un parámetro.
- `RF-95` seguridad: actualizar contraseña; `RF-95` mantenimiento: registrar/editar laboratorios externos.

**Acción obligatoria:** prohibir que `RF-83`, `RF-89` o cualquier código duplicado sea la única referencia de una tarea o prueba. Usar temporalmente `2. REQ FUNCIONALES:FILA:<número>` y descripción exacta, hasta aprobar un catálogo de ID globales sin colisiones.

### Nomenclatura de casos de uso no reconciliada

- La lista principal incluye `CUS05 - REGISTRAR RECETA OFTÁLMICA` y también `CUS05 - REGISTRAR VENTA DIRECTA`: **colisión dentro de la propia hoja**.
- La priorización indica `CU-6 - Registrar venta directa`; otro sector de requisitos indica `CUS06 - Registrar venta de servicios`. No son equivalentes por numeración.
- La priorización menciona `CU-2 - Agendar cita` y `CU-5 - Generar constancia de atención`, sin correspondencia funcional inequívoca en la lista principal; no programar esos módulos sin confirmar alcance.
- La priorización menciona `CU-14 - Mandar Requerimiento de Compra` y `CU-15 - Recepcionar Productos`; la sección funcional de inventario debe revisarse antes de inferir flujos de compras, proveedores y recepción.

### Prioridad de casos de uso disponible

| Caso | Nombre | Puntuación | Clasificación | Fila |
|---|---|---:|---|---:|
| CU-1 | Gestionar cliente | 7.70 | PRIMARIO | 7 |
| CU-2 | Agendar cita | 5.70 | SECUNDARIO | 8 |
| CU-3 | Registrar atención del cliente | 7.20 | PRIMARIO | 9 |
| CU-4 | Registrar refracción | 8.80 | PRIMARIO | 10 |
| CU-5 | Generar constancia de atención | 4.70 | SECUNDARIO | 11 |
| CU-6 | Registrar venta directa | 7.70 | PRIMARIO | 12 |
| CU-7 | Registrar venta de servicios | 6.20 | SECUNDARIO | 13 |
| CU-8 | Registrar venta con receta | 9.30 | PRIMARIO | 14 |
| CU-9 | Gestionar pagos | 8.80 | PRIMARIO | 15 |
| CU-10 | Generar Orden de Trabajo | 9.00 | PRIMARIO | 16 |
| CU-11 | Registrar Control de Calidad | 8.20 | PRIMARIO | 17 |
| CU-12 | Notificar Disponibilidad al Paciente | 5.50 | SECUNDARIO | 18 |
| CU-13 | Registrar Entrega de Pedido | 7.50 | PRIMARIO | 19 |
| CU-14 | Mandar Requerimiento de Compra | 6.70 | PRIMARIO | 20 |
| CU-15 | Recepcionar Productos | 6.70 | PRIMARIO | 21 |

**Sin nombre en priorización:** CU-16, CU-17, CU-18, CU-19, CU-20, CU-21, CU-22, CU-23, CU-24. Su puntuación/categoría actual no demuestra prioridad real porque las entradas están incompletas.

## Reglas de seguridad que sí se sostienen

- El equipo definió JWT y autorización basada en roles/permisos (RBAC) en su plan de desarrollo.
- La plantilla funcional contempla inicio/cierre de sesión, registro/actualización/desactivación de usuarios, asignación de un rol al usuario, asignación de permisos al rol y recuperación de contraseña.
- La relación 1:1 y obligatoria empleado–usuario procede de los acuerdos de revisión del DDL, no se deduce de la plantilla.
- **No hay matriz de acciones permitidas por rol firmemente aprobada** ni políticas de expiración/rotación/revocación de JWT; definirlas explícitamente en ADRs antes de cerrar el módulo de seguridad.
- El controlador y la SPA no sustituyen la autorización en los servicios/endpoints del backend.

## Criterios de aceptación y trazabilidad obligatorios

Para cada historia registrar: código temporal único, hoja/fila, texto original, actor, proceso CUN, caso de uso canónico (si está aprobado), regla de negocio, entidad DDL, endpoint, servicio, prueba automatizada, prioridad, responsable y estado.

Pruebas transversales mínimas: permisos denegados, validaciones, usuario inactivo, saldo pendiente en entrega, stock no negativo y concurrencia en movimientos/pagos; verificar que las operaciones son transaccionales según el DDL.

## Procedimiento para incorporar una futura plantilla corregida

1. Conservar versión anterior y nueva con huellas SHA-256 y fechas.
2. Extraer todas las hojas y celdas con su posición exacta; distinguir fórmulas, valores y texto.
3. Generar diff por **hoja + fila + columna** y detectar filas movidas.
4. Agrupar los cambios por CUN, módulo y caso de uso, y señalar repercusión en backend/Angular/Oracle/seguridad/tests.
5. Actualizar `CONTEXTO_PROYECTO_GEO_VISION.md` y `AGENTS.md`, sin borrar decisiones de arquitectura válidas.
6. Solicitar aprobación cuando un cambio de requisito contradiga el DDL o una decisión técnica acordada.

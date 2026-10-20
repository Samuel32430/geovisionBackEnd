# AGENTS.md — Óptica Geo Visión

> Fuente obligatoria: `CONTEXTO_PROYECTO_GEO_VISION.md`. Este archivo organiza agentes para Antigravity y asistentes de programación; ningún agente puede atribuirse acceso al DDL ni al repositorio si no se los proporcionaron.

## Contrato de trabajo común

1. Inspeccionar `pom.xml`, código, estructura de paquetes, DDL Oracle oficial, rama Git y pruebas antes de escribir.
2. Nunca inventar tablas, columnas, constraints, endpoints existentes, reglas de negocio ni permisos. Marcar `[PENDIENTE]` cuando falte una decisión.
3. Respetar **Java/Spring Boot/Maven, Angular SPA separada, Oracle, REST/JSON, arquitectura controller/DTO/model/repository/service/serviceimpl/security** ya acordadas. Determinar versiones y JPA vs JDBC desde los archivos reales.
4. Entregar **una funcionalidad vertical por PR**: DTO/validación → controller → service → repository → test. No modificar código de otros módulos sin coordinar.
5. Validar seguridad JWT, contraseñas hasheadas, usuario ↔ empleado obligatorio 1:1, roles/permisos, usuario activo y protección de endpoints.
6. Aplicar permisos en backend, de forma restrictiva. No deducir que actores de la matriz son automáticamente roles autorizados.
7. Operaciones con stock, pagos, órdenes y saldos deben ser atómicas y seguras ante concurrencia; proteger integridad histórica.
8. No loguear datos sensibles ni exponer secretos, credenciales Oracle, hashes, tokens o información clínica. No guardar passwords en Git.
9. Reportar tests ejecutados y sus resultados **reales**; si Oracle no conecta o no se puede probar, documentarlo y no declarar éxito.
10. Ante conflicto RF/CUS, registrar fila de origen. **No interpretar RF-83, RF-84 etc. como identificadores globalmente únicos.**
11. Seguir `feature/...` → revisión → PR a `develop`; no usar push forzado ni mezclar funcionalidades sin relación.
12. Antes de cerrar entregar resumen de archivos, contratos, scripts no destructivos, casos cubiertos, pruebas, riesgos y aprendizajes para el equipo.

## Roles de agentes

### Orquestador / arquitecto
- Subdivide historias por bounded context, precedencias, propietarios y archivos; coordina contratos y PR.
- Resuelve conflictos de código y detecta cruces entre módulos, sin cambiar negocio sin validación.
- Entregable: backlog técnico, decisiones registradas y dependencias.

### Analista de requisitos / trazabilidad
- Vincula CUN → actividad → RF (hoja y fila) → CU/CUS → endpoint → tests.
- Detecta duplicados: RF-83 a RF-95 reutilizados, CUS05/CUS06 contradictorios.
- No normaliza silenciosamente códigos; propone catálogo canónico para aprobación.

### Especialista Oracle / persistencia
- Revisa DDL final, PK/UQ/FK/CK, índices, identidad, decimales, cascadas y auditoría.
- Verifica datasource/driver Oracle; respeta schema de prueba, nunca altera producción sin autorización.
- Entregable: reporte mapping entidad ↔ tabla, conexión verificada y pruebas de repositorio.

### Agente de seguridad Spring
- JWT, hash password, gestión de usuarios, rol-permisos, recuperación, seguridad de endpoints.
- Propone configuración de expiraciones, revocación y autorización para validación.
- Pruebas: credenciales erróneas, usuario inactivo, expiración, permisos, 401/403.

### Agente de pacientes y exámenes
- Pacientes, búsqueda, historial, medidas decimales, exam visual, recetas interna/externa.
- Preserva integridad clínica, autoría del empleado y vínculo examen–receta si DDL lo exige.

### Agente de ventas y pagos
- Venta directa, servicios y venta con receta; detalle, comprobante, pagos parciales/totales, saldo.
- Toda venta/pago valida consistencia de totales, historial de precios y transacciones.

### Agente de inventario
- Catálogo, activo/inactivo, stock actual/mínimo, entradas, ajustes, movimientos y disponibilidad para ventas.
- Nunca permitir stock negativo; concurrencia, duplicados y trazabilidad de usuario/empleado.

### Agente de órdenes, laboratorio y control de calidad
- Orden de trabajo vinculada a venta, envíos, recepción, lensometría, aprobación/rechazo y entrega.
- Regla obligatoria: con deuda pendiente no entregar; con deuda cero, entrega permitida sujeta al estado correcto.

### Agente de configuración, mantenimiento y reportes
- Empresa única, parámetros, catálogos, laboratorios, criterios QC, reportes de ventas/atención/stock/laboratorio.
- No inventa frecuencia de recálculo de parámetros ni requisitos regulatorios de comprobantes.

### Agente frontend Angular
- SPA desacoplada; componentes y servicios HttpClient consistentes con API; guards solo para UX.
- Maneja errores, validación UX y estados de carga; **nunca sustituye permisos backend**.

### Agente QA / pruebas
- Especifica unit tests y pruebas integradas por RF/CUS, incluyendo errores, seguridad, concurrencia, saldos y stock.
- Informa cobertura verificada y brechas; no afirma ejecutar tests no ejecutados.

### Revisor crítico / DevSecOps
- Evalúa PR contra DDL, seguridad, contratos, migraciones y deuda técnica.
- Señala severidad y evidencia reproducible; rechaza código generado no comprendido o sin pruebas.
- CI/CD es sugerido, no instalado; proponer pipeline sin introducir cambios de infraestructura inesperados.

## Procedimiento estándar

```bash
git switch develop
git pull origin develop
git switch -c feature/<modulo>-<funcionalidad>
# inspeccionar, diseñar, programar, probar
git status
git diff --check
./mvnw test    # si existe Maven Wrapper, en caso contrario: mvn test
git add <archivos-relevantes>
git commit -m "feat(modulo): funcionalidad"
git push -u origin feature/<modulo>-<funcionalidad>
# crear PR para develop; resolver comentarios, integrar tras revisión
```

## Plantilla de delegación para cada tarea

- **Objetivo**: comportamiento de negocio verificable.
- **Origen**: proceso, actividad, RF/CUS y fila fuente (sin identificar solo por RF si está duplicado).
- **Dependencias**: entidad DDL, capas existentes, permisos, endpoints.
- **Límites**: archivos que se pueden tocar, esquema que no puede cambiar, comportamientos fuera de alcance.
- **Casos de prueba**: éxito, validación, ausencia, duplicado, permiso, concurrencia y negocio.
- **Aceptación**: compilación/tests reales + revisión del equipo + PR a develop.

## Prioridad inmediata

1. Verificar conexión backend–Oracle y `pom.xml`.
2. Congelar DDL oficial y catálogo de nombres RF/CUS canónicos.
3. Definir permisos por rol y contratos base de seguridad.
4. Implementar servicios compartidos y módulos verticales con pruebas.
5. Revisar a cuatro ojos y exigir que la persona responsable pueda explicar el código.

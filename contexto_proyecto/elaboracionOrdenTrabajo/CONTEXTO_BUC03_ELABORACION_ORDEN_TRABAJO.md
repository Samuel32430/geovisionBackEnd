# BUC03 — Elaboración de Orden de Trabajo

> Documento de contexto versionado — 2026-10-26. No equivale al DDL ni a contratos REST definitivos.

## 0. Autoridad y alcance

- **Nombre oficial:** BUC03 – Elaboración de Orden de Trabajo.
- **Proceso de negocio:** CUN 3 — Hacer orden de trabajo (nombre en matriz).
- **Casos oficiales:** CU9 Generar Orden de Trabajo; CU10 Registrar Control de Calidad; CU11 Notificar Disponibilidad al Paciente; CU12 Registrar Entrega de Pedido.
- **Fuentes:** plantilla local `3. Plantilla de requerimientos V3 (7).xlsx` (hoja `2. REQ FUNCIONALES`, filas 48–59; hoja `3. REGLAS DE NEGOCIO`, filas 26–27), documentación histórica de Drive y decisiones confirmadas el 2026-10-26.
- **Jerarquía:** decisiones confirmadas > matriz V3 vigente > docs históricos. Cualquier hueco queda pendiente, nunca se presenta como hecho.

## 1. Resumen de negocio

El módulo da seguimiento al pedido con receta: generación de OT, envío y recepción desde laboratorio externo, lensometría y control de calidad, aviso WhatsApp (manual/automático) y entrega condicionada a calidad aprobada y saldo pendiente cero. No registra ventas, exámenes ni pagos; consulta otros módulos.

## 2. Actores y permisos funcionales

- **Dueño / Técnico:** CU9: revisar, generar OT y supervisar su seguimiento; CU10: medir y aprobar/rechazar calidad.
- **Vendedor:** CU9: colaborar en envío y recepción según documentos previos; CU11: gestionar notificaciones; CU12: consultar y registrar entrega.
- **Paciente / Cliente:** Recibe la notificación y recoge el pedido. No opera el módulo.
- **Laboratorio externo:** Destino físico de envío y origen del producto armado; no se asume acceso al sistema.

## 3. Requerimientos de la matriz (no reenumerar sin modificar matriz)

| RF | CU | Actividad de negocio | Descripción |
|---|---|---|---|
| RF-55 | CU9 | Revisar ventas pendientes de OT | Visualizar y filtrar pedidos con estado «Pendiente de OT». |
| RF-56 | CU9 | Generar orden de trabajo | Generar una OT con datos del paciente, receta, medidas y especificaciones del lente. |
| RF-57 | CU9 | Registrar envío al laboratorio | Registrar la salida de la montura y la OT hacia el laboratorio externo. |
| RF-58 | CU9 | Confirmar envío del pedido | Cambiar el estado del pedido a «Enviado a Laboratorio». |
| RF-59 | CU9 | Registrar recepción del producto terminado | Registrar la recepción del lente armado devuelto por el laboratorio. |
| RF-60 | CU10 | Realizar control de calidad | Registrar los resultados de lensometría del producto terminado. |
| RF-61 | CU10 | Registrar rechazo del lente cuando no cumple | Marcar el pedido como «Retrasado» cuando el lente no cumple la receta. |
| RF-62 | CU10 | Registrar aprobación del lente cuando cumple | Aprobar el control de calidad cuando las medidas coinciden con la receta. |
| SIN CÓDIGO | CU10→CU11 | Actividad sin RF numerado, fila 56 | Actualizar el pedido a «Listo para entrega» después de aprobar el control de calidad. |
| RF-63 | CU11 | Preparar pedido para entrega | Permitir mandar una notificación vía WhatsApp al cliente. |
| RF-64 | CU11 | Notificar disponibilidad del pedido al paciente | Registrar que se notificó al cliente. |
| RF-65 | CU12 | Entregar pedido al cliente | Permitir al vendedor consultar el estado del pedido antes de entregarlo. |

## 4. Reglas de negocio trazables

| ID trabajo | Regla | Fuente | CU |
|---|---|---|---|
| RN-BUC03-01 | La OT se genera a partir de un pedido elegible «Pendiente de OT», con receta, datos del paciente y especificaciones ópticas disponibles. | Matriz RF-55–56 + documento previo | CU9 |
| RN-BUC03-02 | No registrar un envío sin OT existente ni confirmar recepción sin envío registrado. | Documento previo CU9 | CU9 |
| RN-BUC03-03 | El envío de OT y montura al laboratorio debe quedar registrado; después se refleja «Enviado a Laboratorio». | Matriz RF-57–58 | CU9 |
| RN-BUC03-04 | La recepción del lente armado se registra antes de iniciar control de calidad. | Matriz RF-59 + documento previo | CU9, CU10 |
| RN-BUC03-05 | El técnico/dueño registra resultados de lensometría y contrasta con receta antes de aprobar o rechazar. | Matriz RF-60–62 | CU10 |
| RN-BUC03-06 | Un resultado no conforme se rechaza, se registra observación y el pedido queda «Retrasado». | Matriz RF-61 + documento previo | CU10 |
| RN-BUC03-07 | Solo un control de calidad aprobado permite establecer «Listo para entrega». | Fila 56 sin RF + decisión confirmada | CU10, CU11, CU12 |
| RN-BUC03-08 | Se admiten notificaciones manuales y automáticas por WhatsApp; debe persistirse el intento o la constancia, sin equiparar intento con entrega efectiva del mensaje. | RF-63–64 + decisión confirmada | CU11 |
| RN-BUC03-09 | Una notificación manual no se registra como enviada/realizada sin confirmación del vendedor. | Control de integridad propuesto; pendiente de ratificar | CU11 |
| RN-BUC03-10 | Solo un vendedor autorizado registra la entrega, previa consulta del estado. | RF-65 + decisión confirmada | CU12 |
| RN-BUC03-11 | La entrega solo se confirma con saldo cero y control de calidad aprobado. | Decisión confirmada + hoja «3. REGLAS DE NEGOCIO», filas 26–27 | CU12 |
| RN-BUC03-12 | Después de confirmar la entrega, se registra la fecha/hora y el responsable, evitando una segunda entrega del mismo pedido. | Documento previo CU12; prevención de duplicidad propuesta | CU12 |

## 5. Flujo transversal

```text
Venta con receta / pedido pendiente (otro módulo)
     |
     v
CU9: crear OT -> enviar a laboratorio -> recepcionar producto
     |
     v
CU10: lensometría -> aprobar? -- NO --> rechazar + RETRASADO + gestión de devolución
     | SÍ
     v
LISTO PARA ENTREGA (requisito fila 56, SIN RF)
     |
     v
CU11: aviso WhatsApp MANUAL o AUTOMÁTICO -> registrar resultado real
     |
     v
CU12: calidad aprobada + listo + saldo == 0 ? -- NO --> bloquear
     | SÍ
     v
ENTREGADO + empleado/fecha/hora
```

## 6. Flujos, subflujos y alternativos por caso

### CU9 — Generar Orden de Trabajo
**Actor principal:** Dueño / Técnico. **RF:** RF-55, RF-56, RF-57, RF-58 y RF-59.
**Objetivo:** Gestionar la generación de la OT desde un pedido pendiente, su envío al laboratorio externo y la recepción del producto terminado, sin confundir estos hitos con una sola transacción.

**Precondiciones:**
- Usuario autenticado con permiso operativo de órdenes de trabajo.
- Pedido identificable en estado de negocio «Pendiente de OT».
- Datos de paciente, receta, medidas y especificaciones requeridas disponibles para revisión.
- Condición «venta con receta confirmada» figura en documento previo; su definición exacta requiere validación con ventas.

**Flujo principal:**
1. **Actor / evento:** Dueño/técnico ingresa a elaboración de OT. **Sistema:** El sistema lista pedidos elegibles y permite filtrar por «Pendiente de OT». **Traza:** RF-55.
2. **Actor / evento:** Selecciona un pedido. **Sistema:** Muestra paciente, venta/pedido, receta, medidas y especificaciones. **Traza:** RF-55/56.
3. **Actor / evento:** Revisa datos de la OT y solicita generación. **Sistema:** Ejecuta SF9-01 y verifica que no exista una OT activa incompatible. **Traza:** RF-56.
4. **Actor / evento:** Confirma la generación. **Sistema:** Crea la OT asociada al pedido y muestra identificador y resumen. **Traza:** RF-56.
5. **Actor / evento:** Cuando corresponda, dueño/técnico o vendedor registra salida al laboratorio. **Sistema:** Ejecuta SF9-02 (envío); guarda evento y actualiza «Enviado a Laboratorio». **Traza:** RF-57/58.
6. **Actor / evento:** Cuando retorna el lente, dueño/técnico o vendedor registra recepción. **Sistema:** Ejecuta SF9-03 (recepción); mantiene trazabilidad y habilita CU10. **Traza:** RF-59.

**Subflujos:**
- **SF9-01 — Validación de datos para generar OT:**
  - El sistema verifica pedido elegible y que exista información necesaria de paciente, receta y especificación óptica.
  - Ante datos ausentes, informa cuáles faltan y no genera una OT incompleta.
  - Cuando los datos son válidos, habilita confirmación, registrando autor y relación OT–pedido.
- **SF9-02 — Registrar envío al laboratorio:**
  - El operador abre una OT ya generada y solicita registrar salida al laboratorio externo.
  - El sistema valida que el estado del pedido admita envío y solicita los datos de salida definidos por el formulario.
  - El operador confirma salida de la OT y montura; el sistema registra evento y establece «Enviado a Laboratorio».
  - El diseño de datos concretos de transportista, fecha estimada, documento de remisión queda pendiente de confirmar.
- **SF9-03 — Registrar recepción de lente armado:**
  - El operador localiza una OT efectivamente enviada.
  - Confirma que el laboratorio externo devolvió el producto y registra la recepción.
  - El sistema guarda la constancia de recepción y muestra el pedido como pendiente de control de calidad, sin aprobarlo automáticamente.

**Alternativos:**
- **FA9-01 (No se seleccionó pedido o no está pendiente de OT):** Bloquear generación y mostrar la condición incumplida.
- **FA9-02 (Faltan receta, medidas o especificaciones):** No persistir OT; indicar datos faltantes.
- **FA9-03 (Se intenta duplicar una OT incompatible):** Prevenir duplicación y permitir consultar la OT existente; definición de reemisión pendiente.
- **FA9-04 (Se intenta enviar OT aún no generada):** Bloquear envío sin modificar estado.
- **FA9-05 (Se intenta recepcionar sin envío previo):** Bloquear recepción y mostrar estado actual.
- **FA9-06 (Usuario no autorizado):** Denegar operación; no revelar ni modificar información restringida.

**Postcondiciones:**
- Al generar: OT vinculada al pedido, con responsable y constancia de creación.
- Al enviar: registro de salida de OT/montura y estado de pedido «Enviado a Laboratorio».
- Al recepcionar: constancia de recepción del lente armado, quedando habilitado el siguiente control de calidad.
- Si se cancela o falla un hito, no debe registrarse como realizado.

**Criterios de aceptación:**
- [ ] Un pedido pendiente y completo produce una única OT referenciada por el pedido, con responsable.
- [ ] Los datos faltantes impiden generar OT, y muestran mensajes concretos.
- [ ] El envío registra salida y cambia el estado a «Enviado a Laboratorio».
- [ ] La recepción solo procede cuando existe envío; no se ejecuta control automáticamente.

**Dependencias:** Ventas con receta/pedido; paciente y receta; catálogos de laboratorio y productos; seguridad y auditoría. No se impone nombre físico de tabla.

---

### CU10 — Registrar Control de Calidad
**Actor principal:** Dueño / Técnico. **RF:** RF-60, RF-61, RF-62; transición a listo sin código RF.
**Objetivo:** Evaluar el lente recibido contra la receta mediante lensometría y documentar aprobación o rechazo sin permitir entrega de artículos no conformes.

**Precondiciones:**
- Operador autenticado y autorizado como dueño/técnico.
- Existe OT generada, enviada y producto terminado recepcionado.
- Receta asociada consultable y producto pendiente de control.

**Flujo principal:**
1. **Actor / evento:** Dueño/técnico abre órdenes pendientes de control de calidad. **Sistema:** Lista solo productos que cuentan con recepción registrada. **Traza:** RF-60.
2. **Actor / evento:** Selecciona una OT. **Sistema:** Presenta receta, datos del paciente y especificaciones del producto. **Traza:** RF-60.
3. **Actor / evento:** Ingresa los resultados de lensometría obtenidos. **Sistema:** Ejecuta SF10-01 y valida obligatoriedad/formato. **Traza:** RF-60.
4. **Actor / evento:** Contrasta los resultados con la receta y selecciona aprobar o rechazar. **Sistema:** Muestra resultado y solicita confirmación de la decisión. **Traza:** RF-61/62.
5. **Actor / evento:** Confirma aprobación si cumple. **Sistema:** Ejecuta SF10-02; registra control aprobado y habilita estado «Listo para entrega». **Traza:** RF-62 + fila 56 sin código.

**Subflujos:**
- **SF10-01 — Registrar lensometría:**
  - Obtener los resultados de medición del lente armado efectuados por la persona autorizada.
  - Validar los campos ópticos que el modelo de control de calidad defina como obligatorios.
  - Guardar valores medidos ligados a OT, receta, fecha y responsable, sin modificar medidas de receta histórica.
- **SF10-02 — Aprobar control de calidad:**
  - Verificar que la OT tenga recepción y resultados de lensometría persistidos.
  - Contrastar medidas con receta usando los criterios y tolerancias aprobados, cuando estén definidos.
  - Registrar aprobación con responsable y habilitar continuidad hacia CU11.
- **SF10-03 — Rechazar y devolver a laboratorio:**
  - Ante mediciones no conformes, operador registra observación/motivo.
  - El sistema registra rechazo y cambia el estado de negocio del pedido a «Retrasado».
  - Si se efectúa devolución física, registrar su constancia y referencia a OT; nueva recepción/recontrol pendiente de regla exacta.

**Alternativos:**
- **FA10-01 (Lente recibido no coincide con receta):** Ejecutar SF10-03; no habilitar entrega.
- **FA10-02 (Faltan mediciones o están mal formadas):** Solicitar corrección y prohibir decisión de aprobación.
- **FA10-03 (La OT no tiene recepción):** No permitir iniciar o cerrar control.
- **FA10-04 (Control previo finalizado):** Evitar duplicación; recontrol solo bajo procedimiento aún no definido.
- **FA10-05 (Usuario no autorizado):** Bloquear aprobación y rechazo.

**Postcondiciones:**
- Se registra resultado de lensometría, responsable, fecha/hora y observaciones aplicables.
- Si coincide con los criterios aprobados, el control queda aprobado y se habilita la transición a «Listo para entrega».
- Si no coincide, control rechazado; pedido «Retrasado» y registrada la incidencia/devolución cuando corresponda.

**Criterios de aceptación:**
- [ ] No se puede aprobar un pedido sin recepción ni mediciones requeridas.
- [ ] El técnico/dueño puede registrar mediciones ligadas a OT.
- [ ] Una revisión conforme queda aprobada y puede activar la condición «Listo para entrega».
- [ ] Una revisión no conforme registra razón y estado «Retrasado»; no puede entregarse.
- [ ] El vendedor no puede aprobar ni rechazar el control.

**Dependencias:** CU9 recepción; receta y medidas; criterio/tolerancia de calidad en catálogo (RF-96 de mantenimiento: solo dependencia, no regla del CU); CU11. El valor numérico de tolerancia no está definido.

---

### CU11 — Notificar Disponibilidad al Paciente
**Actor principal:** Vendedor. **RF:** RF-63, RF-64; transición a «Listo para entrega» sin código RF.
**Objetivo:** Comunicar al paciente que el pedido está listo, ya sea mediante intervención manual del vendedor o mediante un mecanismo automático configurado, guardando trazabilidad del resultado.

**Precondiciones:**
- Pedido con control de calidad aprobado.
- El sistema lo identifica como «Listo para entrega» (transición automática posterior a aprobación conforme a fila 56, aún sin código).
- Existe un medio de contacto válido para el canal elegido, o se informa la imposibilidad de notificar.
- Para la modalidad automática, integración/credenciales y autorización previamente configuradas; proveedor no definido.

**Flujo principal:**
1. **Actor / evento:** CU10 finaliza con control aprobado. **Sistema:** El sistema actualiza el pedido a «Listo para entrega» (requisito sin código), habilitando CU11. **Traza:** Fila 56 sin código.
2. **Actor / evento:** Vendedor consulta los pedidos listos y selecciona uno. **Sistema:** Muestra paciente, contacto disponible, estado de notificación y controles de acceso. **Traza:** RF-63/64.
3. **Actor / evento:** Vendedor elige modalidad de comunicación manual o automática habilitada. **Sistema:** Muestra opciones disponibles según la configuración y ejecuta SF11-01 o SF11-02. **Traza:** RF-63/64.
4. **Actor / evento:** Vendedor verifica el resultado o confirma la gestión manual efectuada. **Sistema:** Ejecuta SF11-03; guarda evento de notificación y su estado real comprobable. **Traza:** RF-64.
5. **Actor / evento:** Vendedor vuelve al listado. **Sistema:** Muestra el pedido disponible y el historial de intentos/constancias. **Traza:** RF-64.

**Subflujos:**
- **SF11-01 — Notificación manual por WhatsApp:**
  - El vendedor revisa número y contenido a comunicar, y realiza el contacto mediante canal WhatsApp aprobado.
  - El sistema puede ayudar a preparar el mensaje, sin asumir que abrir una ventana o enlace equivale a envío exitoso.
  - El vendedor confirma explícitamente que realizó la notificación; se registra el evento como manual, con observación si corresponde.
- **SF11-02 — Notificación automática por WhatsApp:**
  - Una acción habilitada inicia el intento automático según el disparador que el equipo defina (pendiente).
  - El backend llama a la integración de mensajería solo si está configurada y obtiene la respuesta técnica disponible.
  - Se guarda intento, referencia de envío si existe y estado recibido (por ejemplo: pendiente, aceptado, fallido); NO se asume «entregado» ni «leído» sin confirmación externa.
- **SF11-03 — Registrar y consultar constancia:**
  - Verificar modalidad, pedido, número/canal y evidencia disponible del contacto.
  - Persistir fecha/hora, actor o proceso responsable y resultado de forma auditable.
  - Mostrar al vendedor el resultado y permitir identificar fallas sin duplicar constancias como si fueran envíos nuevos.

**Alternativos:**
- **FA11-01 (Control no aprobado o pedido no listo):** Bloquear notificación de disponibilidad.
- **FA11-02 (Falta teléfono/WhatsApp utilizable):** Mostrar advertencia, conservar pedido listo y registrar incidencia, no envío exitoso.
- **FA11-03 (Integración automática no configurada o error):** Registrar pendiente/fallido; permitir modalidad manual sin afirmar envío automático exitoso.
- **FA11-04 (El vendedor no confirma notificación manual):** No dar el evento por notificado; no inferir envío por abrir enlace.
- **FA11-05 (Existe notificación previa):** Advertir o mostrar historial; política de reenvíos aún pendiente.

**Postcondiciones:**
- Se conserva estado «Listo para entrega» independientemente del resultado de mensajería.
- Registro de modalidad manual/automática, fecha/hora, responsable o proceso ejecutor, resultado reportado y canal WhatsApp.
- Un intento fallido no debe registrarse falsamente como mensaje entregado; la confirmación de lectura no se presume.

**Criterios de aceptación:**
- [ ] Un pedido no aprobado no aparece como disponible para notificar.
- [ ] Se distinguen modalidades manual/automática y estados de intento.
- [ ] Si falla API o falta número, el pedido sigue «Listo para entrega» y se reporta falla de notificación.
- [ ] La modalidad manual requiere confirmación para constar como efectuada.
- [ ] No se documenta SMS como canal confirmado en esta versión.

**Dependencias:** CU10 aprobó; datos de contacto del paciente; WhatsApp manual; eventual proveedor/API externa por decidir; seguridad y auditoría.

---

### CU12 — Registrar Entrega de Pedido
**Actor principal:** Vendedor. **RF:** RF-65 (consulta); registro y saldo cero: decisión confirmada, RF nuevo pendiente.
**Objetivo:** Confirmar la entrega física de un pedido terminado solo si su control de calidad está aprobado, el pedido está listo y no tiene saldo pendiente.

**Precondiciones:**
- Usuario autenticado con permiso de vendedor/entrega.
- Pedido existente, con control de calidad aprobado y estado «Listo para entrega».
- El importe pendiente consultado en el sistema de pagos es igual a cero.
- Se realiza la entrega física del producto al paciente o receptor autorizado; reglas de entrega por tercero por confirmar.

**Flujo principal:**
1. **Actor / evento:** Paciente solicita recoger su pedido; vendedor abre la pantalla de entrega. **Sistema:** El sistema permite búsqueda por código de pedido o identificador del paciente según pantalla implementada. **Traza:** RF-65.
2. **Actor / evento:** Vendedor selecciona el pedido hallado. **Sistema:** Muestra OT, estado de pedido, resultado de control y saldo pendiente. **Traza:** RF-65 + decisión.
3. **Actor / evento:** Vendedor solicita validar entrega. **Sistema:** Ejecuta SF12-01: verifica control aprobado, estado listo y saldo cero en fuente de pagos. **Traza:** Decisión confirmada.
4. **Actor / evento:** Vendedor realiza entrega física y confirma el registro. **Sistema:** Ejecuta SF12-02: persiste fecha, hora, empleado responsable y estado «Entregado». **Traza:** Brecha RF-65.
5. **Actor / evento:** Vendedor consulta confirmación. **Sistema:** Muestra el pedido como entregado y su constancia. **Traza:** Decisión confirmada.

**Subflujos:**
- **SF12-01 — Validar estado y saldo:**
  - Consultar estado vigente del pedido y control de calidad asociado, no datos obsoletos de la pantalla.
  - Consultar saldo pendiente calculado por ventas/pagos; exigir que sea exactamente cero según regla confirmada.
  - Si falta aprobación, el estado no es listo, hay deuda o no se obtiene el saldo fiable, bloquear entrega e informar causa.
- **SF12-02 — Registrar entrega:**
  - El vendedor declara que hizo la entrega física del pedido al receptor correspondiente.
  - El sistema vuelve a validar estado/saldo en el momento de la operación para evitar cambios concurrentes.
  - El sistema confirma «Entregado» y registra hora, fecha, responsable y relación con OT/pedido.

**Alternativos:**
- **FA12-01 (Saldo pendiente mayor que cero):** No registrar entrega; mostrar saldo a regularizar en ventas/pagos.
- **FA12-02 (Control de calidad no aprobado o pedido no listo):** Bloquear entrega y mostrar estado exacto.
- **FA12-03 (Pedido inexistente o búsqueda sin resultado):** Mostrar aviso y permitir nueva búsqueda.
- **FA12-04 (El vendedor cancela o no confirma entrega):** Mantener estado y no grabar entrega.
- **FA12-05 (Pedido ya entregado):** Mostrar comprobante o historial; bloquear duplicado.
- **FA12-06 (No puede verificarse saldo actualizado):** No permitir entrega hasta obtener saldo fiable.

**Postcondiciones:**
- El pedido queda «Entregado» solo después de validación y confirmación explícita de entrega.
- Se registra fecha/hora, responsable e identificación de la OT/pedido.
- No se permite una segunda confirmación del mismo pedido entregado; si hay deuda, permanece «Listo para entrega» y se informa saldo.

**Criterios de aceptación:**
- [ ] Un pedido listo, con calidad aprobada y saldo cero puede marcarse «Entregado» por el vendedor.
- [ ] Con cualquier saldo pendiente, se bloquea la entrega sin modificar el pedido.
- [ ] Con control rechazado o pendiente, se bloquea aunque saldo sea cero.
- [ ] Se guardan responsable, fecha/hora y vínculo con la OT/pedido.
- [ ] No se permite registrar la entrega dos veces.
- [ ] El hecho de haber notificado no sustituye el saldo cero ni el control aprobado.

**Dependencias:** CU10 y estado listo; CU11 como proceso relacionado (sin asumir que notificación es condición de entrega); ventas/pagos como fuente de saldo; seguridad y auditoría.

---

## 7. Estados y transiciones de negocio (no nombres exactos del catálogo físico)

| Estado | Significado / disparo |
|---|---|
| Pendiente de OT | Entrada a CU9 |
| Enviado a Laboratorio | Salida registrada y envío confirmado |
| Pendiente de Control de Calidad | Producto recibido a la espera de lensometría |
| Retrasado | Rechazado/no conforme; no entregable |
| Listo para entrega | Calidad aprobada (la matriz no numera este RF) |
| Entregado | Calidad aprobada, estado listo, saldo 0 y entrega física confirmada |

## 8. Arquitectura conocida del proyecto
- Angular SPA frontend separado; backend Java/Spring Boot con Maven, API REST y Oracle.
- Capas backend: controller / DTO / service / serviceImpl / repository / model; seguridad hash contraseñas, JWT, roles y permisos.
- Integración BUC03 con ventas y pagos únicamente para consulta de ventas con receta, pedido, saldo y validación de entrega.
- **Prohibido inferir nombres exactos de tablas o endpoints** hasta recibir DDL final y convenciones del repositorio.
- Conceptos de negocio: Pedido, Venta, OT, Laboratorio, Envío, Recepción, Control, Lensometría, Notificación, Entrega, Empleado/Usuario y Pago/Saldo; NO son promesas de entidades físicas.
- Debe registrarse trazabilidad de estados/operaciones en backend; estructura precisa de auditoría pendiente de DDL.

## 9. Contratos y diseño pendientes (NO implementar con supuestos)
- Nombre de ruta REST, DTO y tablas físicos; catálogo de estados exacto.
- Fórmula y valores de tolerancias/calidad, unidades y comportamiento frente a diferencias marginales.
- Proveedor WhatsApp/API, disparador, plantilla, costo y estatus externos (`aceptado` no equivale a `entregado`).
- Criterio de reenvío, recuperación de notificación, datos autorizados del receptor y políticas de privacidad.
- Relación OT:pedido 1:1 u otra cardinalidad según DDL.
- Transacción entre entrega y saldos concurrentes; pagos se consultan, no se cobran en BUC03.
- Reprocesamiento tras rechazo, cancelación, devoluciones, entregas por tercero o parciales.

## 10. Brechas documentales que no deben ocultarse

| Código | Brecha | Tratamiento |
|---|---|---|
| P-01 | Falta asignar código RF a la transición «Listo para entrega» (fila 56) y vincularla oficialmente al CU correspondiente. | No se creó identificador definitivo. |
| P-02 | RF-65 solo exige consulta de estado; la capacidad explícita «Registrar entrega» y la condición de saldo cero requieren RF formal. | Se documenta como decisión confirmada y brecha de matriz. |
| P-03 | No se ha identificado proveedor de WhatsApp, API, costos, plantillas ni evidencia de entrega; tampoco se definió disparador automático. | La documentación contempla ambos canales operativos, pero no promete automatización lista. |
| P-04 | El resultado «Aprobado» depende de criterios/tolerancias; la matriz contiene RF-96 en mantenimiento, sin valores concretos para lensometría. | No se inventaron umbrales. |
| P-05 | No consta el DDL definitivo completo en los archivos revisados en esta ejecución. | Entidades y columnas propuestas no se presentan como tablas existentes. |
| P-06 | No está determinado el comportamiento ante abonos pendientes, anulaciones, devoluciones o entregas parciales; saldo cero sí es regla confirmada. | Quedan fuera de casos aprobados hasta definición. |
| P-07 | No está definido si enviar notificación es obligatorio antes de entregar, o si se permite reenvío, ni cómo identificar duplicados. | No se bloquea la entrega por falta de notificación. |
| P-08 | La plantilla anota «Vendedor» como actor en RF-55 y RF-60–62, en conflicto con responsables del negocio y con la decisión del equipo. | Se adopta dueño/técnico para órdenes y calidad; vendedor para notificación y entrega. |
| P-09 | No hay confirmación de la equivalencia exacta entre estados visibles y el catálogo de estados del DDL. | Nombres de negocio; pendientes de mapear a persistencia. |
| P-10 | Los documentos anteriores especificaban 3 segundos y SMS, pero no se ha comprobado su vigencia en la matriz actual. | No se presentan como obligatorios aprobados. |

## 11. Reglas de implementación para agentes
1. Antes de programar cada CU, revisar este contexto, RF matriz y DDL definitivo. Si difieren, crear incidencia y NO elegir arbitrariamente.
2. Nunca inventar umbral de lensometría, integrador de WhatsApp, número RF, endpoint ni transición de catálogo.
3. Separar claramente registrar intento, aceptar envío técnico y confirmar entrega efectiva en notificaciones.
4. En CU12, consultar saldo fiable justo antes de registrar entrega, y bloquear si no se puede validar saldo cero.
5. La autorización no depende del rol visible de Angular: el backend verifica permisos en cada operación.
6. Pruebas positivas y negativas para todos FA, incluyendo usuario sin permisos, datos incompletos y estado obsoleto/concurrencia.
7. Versionar cambios por CU; documentar origen de reglas y pendientes. No declarar CU terminado sin criterios de aceptación.

## 12. Enlaces documentales de origen
- Carpeta histórica BUC03: https://drive.google.com/drive/folders/1Kqt5hKTS37xRavnSUYs-l_5IxY54ehrp
- Prototipo previo: https://www.figma.com/design/evyHf4riGduIWpYBgxTKtp/Prototipo-Geo-VIsion
- Los archivos DOCX actualizados (CU9 a CU12 y contexto) forman parte del mismo paquete de entrega.

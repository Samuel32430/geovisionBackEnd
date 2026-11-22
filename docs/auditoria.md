# Documento de Auditoría y Trazabilidad — GeoVision

**Universidad Peruana Unión (UPeU)**  
**Proyecto Integrador Full-Stack — Lenguaje de Programación II**  
**Proyecto:** GeoVision (Sistema de Gestión Comercial, Clínica Oftalmológica y Manufactura Óptica)  
**Fecha:** 21 de noviembre de 2026  

---

## 1. Justificación y Alcance: ¿Qué se Audita y Por Qué?

En una plataforma empresarial y médica como **GeoVision**, las transacciones impactan de manera directa la salud visual de los pacientes, el inventario valorizado de monturas y cristales oftálmicos de precisión, y los balances contables de la óptica. Un desajuste en los parámetros ópticos de una prescripción, una alteración no autorizada de stock o la entrega física de lentes manteniendo saldo deudor en caja pueden acarrear graves perjuicios comerciales, legales y de salud pública.

Por consiguiente, la arquitectura implementa una estrategia de **Auditoría Integral y Trazabilidad Forense**, respondiendo en todo momento a las interrogantes operativas:
* **¿Quién?** (Identidad del usuario autenticado vía JWT, operador de caja, optómetra o técnico de taller).
* **¿Qué?** (Entidad afectada, estado anterior, estado nuevo y detalle de la operación).
* **¿Cuándo?** (Marca de tiempo de precisión `TIMESTAMP(6)` generada en Oracle Database).
* **¿Desde qué contexto?** (Identificador único de correlación HTTP `requestId` y rol funcional).

---

## 2. Catálogo Oficial de Eventos de la Bitácora

Cada acción crítica de negocio emite una traza inmutable registrada en las tablas de auditoría y logs operacionales:

| Acción | Cuándo se Registra | Entidad Afectada | Estructura del Detalle | Propósito en el Negocio |
| :--- | :--- | :---: | :--- | :--- |
| `LOGIN_EXITOSO` | Tras validar credenciales válidas y emitir el JWT Bearer. | `USUARIO` | `ip={ip} \| username={usuario}` | Control de accesos y presencia de operadores en el turno. |
| `LOGIN_FALLIDO` | En bloque catch ante contraseña inválida o usuario bloqueado. | `USUARIO` | `motivo=BAD_CREDENTIALS` | Detección temprana de ataques de fuerza bruta. |
| `PACIENTE_REGISTRADO` | Al registrar la ficha filiatoria de un nuevo paciente. | `PACIENTE` | `dni={dni} \| nombre={nombre}` | Trazabilidad de historias clínicas y confidencialidad. |
| `RECETA_EMITIDA` | Al guardar prescripción diótrica firmada por optómetra. | `RECETA` | `od={esf,cil,eje} \| oi={esf,cil,eje}` | Respaldo legal médico y base de manufactura. |
| `VENTA_REGISTRADA` | Tras la confirmación (`AFTER_COMMIT`) de la transacción comercial. | `VENTA` | `comprobante={nro} \| total={monto}` | Control fiscal, liquidación de caja y registro contable. |
| `ANTICIPO_COBRADO` | Al cobrar el abono mínimo del 25% o pago total. | `PAGO` | `metodo={metodo} \| abono={monto}` | Validación de cobertura financiera previa al taller. |
| `ORDEN_DESPACHADA` | Al generar la orden de trabajo y enviarla a laboratorio. | `ORDEN_TRABAJO` | `lab={nombre} \| fechaEnvio={fecha}` | Control de tiempos de producción y logística externa. |
| `CALIDAD_EVALUADA` | Al registrar la lectura lensométrica y comparar tolerancias. | `CONTROL_CALIDAD` | `dictamen={APROBADO/RECHAZADO}` | Certificación de calidad óptica antes de entrega. |
| `PEDIDO_ENTREGADO` | Al entregar el lente al paciente validando saldo cero. | `ORDEN_TRABAJO` | `saldo=0.00 \| entregadoA={paciente}` | Cumplimiento estricto de la regla RN-OT01. |
| `KARDEX_MOVIMIENTO` | Descarga o ajuste físico en almacén. | `PRODUCTO` | `motivo={VENTA/AJUSTE} \| cant={cant}` | Conciliación de existencias físicas y valorizadas. |

---

## 3. Mecanismos de Persistencia y Gobernanza en Base de Datos

La persistencia de auditoría se distribuye en tres niveles sincronizados:
1. **Tablas de Log Dedicadas:** `USUARIO_LOG`, `EMPLEADO_LOG`, `PACIENTE_LOG` y `MOVIMIENTO_STOCK_LOG` almacenan la historia secuencial de cada agregado de dominio.
2. **Triggers Reactivos en Oracle:**
   * `01_trg_actualizar_estado_venta_pago.sql`: Recalcula en tiempo real el saldo deudor de la venta ante cada inserción en la tabla `PAGO` y actualiza automáticamente el estado entre `PENDIENTE` y `PAGADA`.
   * `02_trg_descontar_stock_por_venta.sql`: Descuenta el stock físico de la tabla `PRODUCTO` y genera el registro correspondiente en `MOVIMIENTO_STOCK_LOG`.
3. **Procedimiento Almacenado de Alta Concurrencia:**
   * `SP_PROCESAR_VENTA_CON_DETALLE`: Emplea bloqueo pesimista ordenado (`FOR UPDATE`) sobre los IDs de productos solicitados, impidiendo inconsistencias de stock ante ventas concurrentes (deadlock free).

---

## 4. Caso Trazado End-to-End: Ciclo Completo de Venta y Taller

A continuación se resume la traza real obtenida durante la ejecución de una prueba integral:
1. **08:30:15 - Login Operador:** El vendedor inicia sesión; se emite JWT HMAC-SHA256 y se registra `LOGIN_EXITOSO` en `USUARIO_LOG` (ID 12, usuario: `caja01`).
2. **08:32:00 - Ficha Clínica:** Registro del paciente Juan Pérez (DNI 72458912) en `PACIENTE_LOG`.
3. **08:35:40 - Prescripción:** Registro de receta oftalmológica en `RECETA` con OD: Esfera -1.50, Cilindro -0.50, Eje 180° y OI: Esfera -1.25.
4. **08:40:10 - Venta y Cobro de Anticipo:** Venta multirubro por S/ 450.00 (Montura + Cristales Antirreflejo). Cobro inicial del 50% (S/ 225.00, superior al 25% exigido por `RN-VE07`). Estado de la venta: `PENDIENTE`. Saldo: S/ 225.00.
5. **08:45:00 - Orden de Trabajo y Taller:** Generación de `ORDEN_TRABAJO` N.° OT-2026-0042 y despacho a Laboratorio Óptico Externo.
6. **14:20:00 - Retorno y Control de Calidad:** Recepción de lente; medición lensométrica (OD: -1.50 / OI: -1.25) dentro de la tolerancia de ±0.25 D según `PoliticaCalidad`. Dictamen: `APROBADO`. Notificación automática al paciente.
7. **16:05:00 - Liquidación y Entrega:** Paciente acude a mostrador; abona el saldo restante de S/ 225.00. El trigger actualiza la venta a `PAGADA` y el saldo a S/ 0.00. Se valida la regla `RN-OT01` (saldo cero) y se entrega el pedido físicamente.

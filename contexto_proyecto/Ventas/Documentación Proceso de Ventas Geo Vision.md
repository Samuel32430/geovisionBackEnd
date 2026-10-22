# PROCESO DE VENTAS - GEO VISION

## 1. Objetivo del proceso
Gestionar de manera unificada las ventas realizadas en la óptica, integrando en una sola operación la venta de productos físicos, servicios profesionales y lentes con receta médica. El proceso permite seleccionar ítems del catálogo, verificar recetas, calcular importes, registrar pagos (completos o adelantos) mediante medios tradicionales o código QR dinámico interoperable, gestionar saldos pendientes y consultar el historial de operaciones comerciales registradas internamente.

## 2. Alcance del proceso

### Incluye
El proceso permite:
- Buscar y seleccionar clientes o pacientes registrados.
- Verificar antecedentes de lentes o recetas médicas asociadas (internas o externas).
- Seleccionar productos del catálogo general y verificar stock físico.
- Seleccionar monturas mediante código de producto.
- Seleccionar tipos de cristal y tratamientos ópticos específicos.
- Agregar productos extra o accesorios al pedido.
- Agregar servicios ópticos o clínicos brindados dentro de la misma venta.
- Calcular automáticamente el monto total a pagar.
- Registrar ventas con pago completo o mediante adelanto.
- Validar el adelanto mínimo (25%) para pedidos que requieran trabajo óptico.
- Calcular y registrar el saldo pendiente resultante.
- Registrar la fecha de entrega estimada del trabajo óptico.
- Procesar pagos mediante efectivo, tarjeta con POS físico y código QR dinámico interoperable (Mercado Pago / Yape / Plin).
- Confirmar automáticamente transacciones digitales mediante notificaciones en tiempo real (Webhooks).
- Buscar y listar ventas con saldo pendiente de pago.
- Registrar pagos finales o abonos para liquidar saldos pendientes.
- Actualizar el estado comercial y contable de las ventas registradas.
- Consultar, buscar y filtrar el historial general de ventas.
- Revisar el detalle completo de ventas históricas y códigos de transacción bancaria.

### No incluye
Este proceso no incluye:
- Emisión física o electrónica de comprobantes tributarios ante SUNAT desde el sistema (solo registro y comprobante interno).
- Ejecución clínica del examen visual o refracción.
- Creación y edición clínica de recetas oftálmicas internas (pertenece a Atención al Paciente).
- Procesamiento técnico y tallado en laboratorio óptico.
- Control de calidad físico de los cristales terminados.
- Despacho y entrega física de pedidos al paciente.
- Administración de usuarios, roles y credenciales del sistema.
- Generación de reportes analíticos avanzados y balances contables generales.

## 3. Actores del proceso

| Actor | Descripción |
| :--- | :--- |
| **Vendedor** | Usuario del personal encargado de atender al cliente, registrar la venta unificada, gestionar cobros con QR o medios tradicionales, registrar pagos pendientes y consultar el historial de ventas. |
| **Cliente / Paciente** | Persona que adquiere productos, contrata servicios, solicita la confección de lentes graduados o cancela saldos pendientes. |
| **Sistema** | Software que valida disponibilidad, calcula importes y saldos, se conecta a la API de pagos para generar códigos QR dinámicos, procesa notificaciones de cobro y mantiene la integridad del registro interno. |
| **API de Pagos (Mercado Pago)** | Plataforma externa que provee la infraestructura para generar órdenes con código QR dinámico interoperable (Yape, Plin y billeteras) y notifica el estado de los cobros en tiempo real. |

## 4. Casos de uso del proceso

| Código | Caso de uso | Descripción |
| :--- | :--- | :--- |
| **CUS06** | Registrar Venta | Permite registrar de forma unificada una venta de productos, servicios o lentes con receta médica, calculando importes, procesando pagos completos o adelantos (en efectivo, tarjeta o QR) y guardando el registro interno. |
| **CUS07** | Revisar Pago Pendiente | Permite buscar ventas con saldo adeudado, consultar el balance pendiente y registrar el pago final de liquidación mediante métodos tradicionales o código QR. |
| **CUS08** | Consultar Historial Ventas | Permite listar, buscar y filtrar las transacciones de venta registradas internamente, visualizando su estado general y su detalle completo. |

## 5. Actividades del negocio

> **Nota:** En las actividades del negocio solo se consideran responsables humanos. La participación del sistema se representa en los requerimientos funcionales y en los flujos de respuesta de los casos de uso.

| N° | Actividad | Responsable del negocio | Caso de uso |
| :---: | :--- | :--- | :--- |
| 1 | Buscar o seleccionar al paciente/cliente | Vendedor | CUS06 |
| 2 | Revisar si tiene algún lente o receta asociada (interna o externa) | Vendedor | CUS06 |
| 3 | Seleccionar productos del catálogo (monturas por código, tipos de cristal, tratamientos de cristal y productos extra) | Vendedor | CUS06 |
| 4 | Seleccionar servicios brindados (si corresponde) | Vendedor | CUS06 |
| 5 | Registrar el monto total calculado | Vendedor | CUS06 |
| 6 | Registrar adelanto o pago completo y seleccionar el medio de pago | Vendedor | CUS06 |
| 7 | Registrar la fecha de entrega estimada (aplica para trabajos de óptica con receta) | Vendedor | CUS06 |
| 8 | Confirmar la venta | Vendedor | CUS06 |
| 9 | Buscar venta con saldo pendiente | Vendedor | CUS07 |
| 10 | Revisar saldo pendiente de la venta | Vendedor | CUS07 |
| 11 | Registrar pago final y seleccionar método de pago | Vendedor | CUS07 |
| 12 | Confirmar cancelación del saldo pendiente | Vendedor | CUS07 |
| 13 | Consultar historial de ventas | Vendedor | CUS08 |
| 14 | Listar ventas registradas | Vendedor | CUS08 |
| 15 | Buscar ventas | Vendedor | CUS08 |
| 16 | Filtrar ventas por fecha, tipo o estado | Vendedor | CUS08 |
| 17 | Revisar detalle de venta registrada | Vendedor | CUS08 |

## 6. Requerimientos funcionales

| Código | Requerimiento funcional | Caso de uso | Actor |
| :--- | :--- | :--- | :--- |
| **RF-25** | El sistema debe permitir buscar y seleccionar un cliente o paciente registrado mediante su nombre, DNI o código. | CUS06 | Vendedor |
| **RF-26** | El sistema debe permitir asociar una receta médica (interna o externa) al proceso de venta y verificar antecedentes de lentes del paciente. | CUS06 | Vendedor |
| **RF-27** | El sistema debe permitir agregar al detalle de la venta productos ópticos. | CUS06 | Vendedor |
| **RF-28** | El sistema debe permitir agregar al detalle de la venta servicios profesionales. | CUS06 | Vendedor |
| **RF-29** | El sistema debe calcular automáticamente el monto total a pagar en función de los productos y servicios seleccionados. | CUS06 | Vendedor |
| **RF-30** | El sistema debe permitir registrar el tipo de pago (adelanto o pago total), calcular saldos y seleccionar el medio de pago (efectivo, tarjeta POS o código QR dinámico). | CUS06 | Vendedor |
| **RF-31** | El sistema debe permitir registrar una fecha estimada de entrega y confirmar el registro exitoso de la venta generando su respectivo comprobante. | CUS06 | Vendedor |
| **RF-32** | El sistema debe mostrar un mensaje de confirmación. | CUS06 | Vendedor |
| **RF-33** | El sistema debe confirmar la venta de forma automática al recibir la notificación de pago aprobado del código QR. | CUS06 | Vendedor |
| **RF-46** | El sistema debe permitir buscar una venta que tenga saldo pendiente. | CUS07 | Vendedor |
| **RF-47** | El sistema debe permitir revisar el saldo pendiente de la venta encontrada. | CUS07 | Vendedor |
| **RF-48** | El sistema debe permitir registrar el pago final del saldo pendiente, seleccionando el método de pago. | CUS07 | Vendedor |
| **RF-49** | El sistema debe permitir confirmar la cancelación del saldo pendiente. | CUS07 | Vendedor |
| **RF-50** | El sistema debe permitir acceder al historial de ventas registradas. | CUS08 | Vendedor |
| **RF-51** | El sistema debe mostrar las ventas registradas con código, fecha, cliente, tipo, total y estado. | CUS08 | Vendedor |
| **RF-52** | El sistema debe permitir buscar ventas por código, cliente o DNI. | CUS08 | Vendedor |
| **RF-53** | El sistema debe permitir filtrar ventas por tipo de venta. | CUS08 | Vendedor |
| **RF-54** | El sistema debe permitir filtrar ventas por estado de venta. | CUS08 | Vendedor |
| **RF-55** | El sistema debe permitir revisar el detalle de la venta registrada, incluyendo el código de referencia en pagos con QR. | CUS08 | Vendedor |

## 7. Requerimientos no funcionales

| Código | Requerimiento no funcional |
| :--- | :--- |
| **RNF-VE01** | El sistema debe presentar una interfaz clara, intuitiva y con diseño limpio basada en componentes SPA, evitando la sobrecarga visual. |
| **RNF-VE02** | El sistema debe responder a búsquedas, cálculos de importes y consultas en un tiempo máximo de 3 segundos. |
| **RNF-VE03** | El sistema debe permitir el acceso al módulo de ventas únicamente a usuarios debidamente autenticados y con rol autorizado. |
| **RNF-VE04** | El sistema debe garantizar la confidencialidad de la información clínica de recetas y de los datos transaccionales de los clientes. |
| **RNF-VE05** | El sistema no debe almacenar datos bancarios sensibles (números de tarjeta, CVV o claves PIN) en su base de datos, delegando los pagos digitales a la API de pagos. |
| **RNF-VE06** | El sistema debe asegurar la entrega y recepción confiable de notificaciones Webhook provenientes de la pasarela de pagos. |
| **RNF-VE07** | El sistema debe mantener la integridad referencial y consistencia de datos entre ventas, líneas de detalle, pagos y recetas. |
| **RNF-VE08** | El sistema debe presentar mensajes informativos comprensibles ante eventos de error, stock insuficiente o rechazo de transacciones. |
| **RNF-VE09** | La interfaz debe adaptarse de manera responsiva a pantallas de escritorio y monitores de punto de venta. |
| **RNF-VE10** | El sistema debe asegurar la trazabilidad de cada cobro registrando el identificador único retornado por la pasarela externa de pago. |

## 8. Reglas de negocio

| Código | Regla de negocio |
| :--- | :--- |
| **RN-VE01** | La venta puede incluir productos físicos, cristales oftálmicos y/o servicios profesionales de forma combinada en una misma orden. |
| **RN-VE02** | Toda venta que involucre cristales graduados o monturas con medida debe estar asociada obligatoriamente a un paciente y a una receta médica (interna o externa). |
| **RN-VE03** | Si se selecciona una receta interna, esta debe provenir de un examen visual válido registrado previamente en el módulo de Atención al Paciente. |
| **RN-VE04** | Para la confección de cristales oftálmicos se debe registrar el código de montura, el tipo de cristal y el tratamiento solicitado. |
| **RN-VE05** | No se puede registrar la venta de productos físicos cuyo stock disponible en catálogo sea inferior a la cantidad solicitada. |
| **RN-VE06** | En ventas de productos sin receta o servicios directos, el pago debe registrarse por el 100% del monto total dentro de la misma operación. |
| **RN-VE07** | En ventas con receta médica en preparación, se permite registrar un adelanto inicial no menor al 25% del valor total de la orden. |
| **RN-VE08** | Si una venta se registra con adelanto, debe consignarse de forma obligatoria la fecha estimada de entrega del producto terminado. |
| **RN-VE09** | Las ventas con adelanto quedan registradas con estado "Pago Pendiente" y monto de saldo por cobrar calculado automáticamente. |
| **RN-VE10** | El caso de uso CUS07 (Revisar Pago Pendiente) aplica exclusivamente para aquellas ventas que registren un saldo pendiente de pago. |
| **RN-VE11** | Cuando el cobro se realice mediante código QR dinámico, la venta no debe confirmarse hasta recibir la confirmación de transacción aprobada por parte de la pasarela. |
| **RN-VE12** | En cobros realizados con POS físico tradicional, es obligatorio ingresar el código de operación o voucher emitido por el terminal bancario. |
| **RN-VE13** | Los montos de pago no pueden ser negativos, iguales a cero ni superiores al saldo total adeudado. |
| **RN-VE14** | Una vez cancelado el 100% del saldo pendiente, la venta cambia su estado financiero a "Completado" y no admite nuevos registros de cobro. |
| **RN-VE15** | La consulta del historial de ventas tiene carácter estrictamente informativo y no permite alterar importes, recetas ni transacciones ya registradas. |

## 9. Descripción de casos de uso

### CUS06: Registrar Venta
Permite centralizar en una sola interfaz el registro comercial de productos de catálogo, servicios ópticos y pedidos con receta médica. El vendedor busca al paciente, comprueba recetas existentes o ingresa una externa, agrega monturas, cristales con sus tratamientos, servicios o productos adicionales, y visualiza el total calculado. A continuación, selecciona si el cobro corresponde a un pago completo o a un adelanto (mínimo 25%), fija la fecha de entrega si aplica y define el medio de pago: efectivo, voucher de tarjeta o generación de un código QR dinámico mediante la API de pagos. Tras validar el cobro, la venta queda guardada internamente con su código único y su estado correspondiente (*Completado*, *En Laboratorio* o *Pago Pendiente*).

### CUS07: Revisar Pago Pendiente
Permite gestionar la liquidación de deudas comerciales generadas por ventas previas que quedaron con saldo adeudado tras un adelanto inicial. El vendedor ubica la venta mediante el buscador por cliente o código de pedido, revisa el desglose financiero (total, abonos previos y saldo actual) y procesa el cobro final utilizando efectivo, voucher de tarjeta POS o un código QR generado por el saldo exacto. Al confirmarse el abono, el saldo se actualiza a cero y la venta queda registrada como completamente pagada.

### CUS08: Consultar Historial Ventas
Permite a los usuarios autorizados auditar y hacer seguimiento de las transacciones comerciales registradas en el sistema. Presenta una tabla paginada con la información general de las ventas (código, fecha, cliente, tipo de operación, total y estado). Proporciona filtros dinámicos por rango de fechas, tipo de venta y estado, además de un buscador global por código, cliente o DNI. Mediante la opción de ver detalle, el usuario visualiza el desglose exhaustivo de los ítems vendidos, la receta asociada, los abonos realizados y el identificador de transacción retornado por la pasarela de pagos.

## 10. Flujo general del proceso

### Flujo: Registrar Venta Unificada (CUS06)
1. El cliente acude a la óptica para adquirir productos, servicios o confeccionar sus lentes.
2. El vendedor accede al módulo de registro de venta y busca al cliente o paciente por su nombre o documento de identidad.
3. El vendedor comprueba si el paciente dispone de recetas ópticas internas en el sistema o registra una receta externa.
4. El vendedor añade al detalle de venta las monturas, tipos de cristal, tratamientos ópticos, productos adicionales o servicios brindados.
5. El sistema calcula y actualiza el importe total de la orden en tiempo real.
6. El vendedor selecciona la modalidad de pago:
   - **Pago completo:** para entregas inmediatas de productos o servicios concluidos.
   - **Adelanto:** para pedidos con preparación óptica, registrando un monto igual o superior al 25% del total y fijando la fecha estimada de entrega.
7. El vendedor selecciona el medio de pago:
   - Si es **Efectivo**, se ingresa el monto recibido y el sistema calcula el vuelto correspondiente.
   - Si es **Tarjeta POS**, se cobra en el terminal físico y se anota el número de operación del voucher.
   - Si es **Código QR**, el sistema solicita a la API de pagos una orden dinámica, muestra el QR interoperable en pantalla y espera la aprobación del cliente desde su billetera digital (Yape/Plin/Mercado Pago).
8. El vendedor confirma la transacción (o el sistema la confirma automáticamente tras la notificación Webhook del QR).
9. La venta queda registrada internamente, actualizando el stock y generando su código de control.

### Flujo: Revisar y Liquidar Pago Pendiente (CUS07)
1. El paciente acude a la óptica en la fecha pactada para recoger su pedido terminado.
2. El vendedor accede al módulo de pagos pendientes y busca la venta por código de orden o documento del paciente.
3. El sistema muestra los datos de la venta, el adelanto cancelado con anterioridad y el saldo exacto por cobrar.
4. El vendedor registra el pago final seleccionando el medio de cobro (efectivo, POS físico o código QR por el saldo adeudado).
5. El vendedor confirma la operación de cancelación de saldo.
6. El sistema liquida la deuda, actualiza el estado de la venta a pagado y registra la trazabilidad del cobro.

### Flujo: Consultar Historial de Ventas (CUS08)
1. El vendedor accede a la vista de historial de ventas.
2. El sistema muestra el listado cronológico general de las ventas registradas.
3. El vendedor utiliza los filtros por fecha, tipo de operación, estado o el buscador rápido por código o DNI.
4. El vendedor selecciona la venta deseada y abre el modal de detalle.
5. El sistema presenta el desglose de productos, servicios, receta médica vinculada, pagos efectuados y códigos de transacción bancaria.

## 11. Matriz de relación: actividades y requerimientos

| Actividad | Requerimiento funcional relacionado | Caso de uso |
| :--- | :--- | :---: |
| Buscar o seleccionar al paciente/cliente | RF-25 | CUS06 |
| Revisar si tiene algún lente o receta asociada (interna o externa) | RF-26 | CUS06 |
| Seleccionar productos del catálogo (monturas por código, tipos de cristal, tratamientos de cristal y productos extra) | RF-27 | CUS06 |
| Seleccionar servicios brindados (si corresponde) | RF-28 | CUS06 |
| Registrar el monto total calculado | RF-29 | CUS06 |
| Registrar adelanto o pago completo y seleccionar el medio de pago | RF-30 | CUS06 |
| Registrar la fecha de entrega estimada (aplica para trabajos de óptica con receta) | RF-31 | CUS06 |
| Confirmar la venta | RF-32, RF-33 | CUS06 |
| Buscar venta con saldo pendiente | RF-46 | CUS07 |
| Revisar saldo pendiente de la venta | RF-47 | CUS07 |
| Registrar pago final y seleccionar método de pago | RF-48 | CUS07 |
| Confirmar cancelación del saldo pendiente | RF-49 | CUS07 |
| Consultar historial de ventas | RF-50 | CUS08 |
| Listar ventas registradas | RF-51 | CUS08 |
| Buscar ventas | RF-52 | CUS08 |
| Filtrar ventas por fecha, tipo o estado | RF-53, RF-54 | CUS08 |
| Revisar detalle de venta registrada | RF-55 | CUS08 |

## 12. Entidades o tablas sugeridas

| Entidad | Descripción |
| :--- | :--- |
| **venta** | Almacena los datos generales de la cabecera de la venta: código interno, cliente/paciente, vendedor, montos totales, saldos, modalidad de pago, estado comercial y fecha estimada de entrega. |
| **detalle_venta** | Registra cada uno de los ítems incluidos en la orden comercial (monturas, cristales graduados, tratamientos ópticos, productos de catálogo o servicios profesionales) con sus cantidades y subtotales. |
| **producto** | Catálogo físico de monturas, accesorios, estuches y consumibles ópticos con control de stock y precio de venta. |
| **servicio** | Catálogo de servicios profesionales brindados por la óptica (medidas de vista, ajustes, mantenimientos). |
| **tipo_cristal** | Almacena las variantes de lunas o cristales disponibles (monofocales, bifocales, progresivos, etc.) con sus costos base. |
| **tratamiento_cristal** | Almacena los tratamientos adicionales aplicables a las lunas (antirréflex, filtro azul, fotocromático, etc.) con su tarifa complementaria. |
| **receta_venta** | Registra el vínculo entre la venta y la prescripción visual del paciente, almacenando la referencia a la receta interna o los parámetros de la receta externa ingresada. |
| **pago** | Registra los pagos efectuados hacia una venta (adelantos o pagos finales), consignando monto, método de pago, código de operación POS, identificador de transacción retornado por la pasarela QR (`payment_id`) y estado del cobro. |
| **historial_venta** | Tabla de auditoría que registra los eventos de cambio de estado de la orden (creación de venta, abonos de saldo, liquidación completa, cancelaciones). |

## 13. Pantallas necesarias

| Pantalla / Vista | Tipo | Descripción |
| :--- | :---: | :--- |
| **Historial de Ventas (CUS08)** | Pantalla Principal | Pantalla central con tabla aireada y paginada que lista transacciones con columnas: Código, Fecha, Cliente, Tipo, Total y Estado. Cuenta con barra de búsqueda rápida y selectores de filtrado por fecha, tipo y estado. |
| **Modal Detalle de Venta** | Modal Flotante | Ventana modal superpuesta abierta desde el historial que desglosa los ítems vendidos, receta médica asociada, personal responsable y trazabilidad de pagos con identificadores de pasarela. |
| **Registrar Venta (CUS06)** | Pantalla Dedicada | Pantalla estructurada en dos paneles limpios: panel de selección (búsqueda de cliente, asociación de receta y catálogo unificado de productos/servicios) y panel lateral de liquidación (cálculo de total, selector de adelanto/pago total, fecha de entrega y medios de pago). |
| **Modal Cobro con QR Dinámico** | Modal Flotante | Ventana modal limpia dentro de Registrar Venta o Pagos Pendientes que solicita a la API de Mercado Pago la orden y muestra el código QR interoperable para escaneo desde billeteras digitales con indicador de espera. |
| **Revisar Pago Pendiente (CUS07)** | Pantalla Dedicada | Pantalla enfocada en cuentas por cobrar que lista únicamente las ventas con saldo adeudado, mostrando monto total, adelanto previo, saldo pendiente y botón de acción directa para registrar liquidación. |
| **Modal Confirmación de Pago de Saldo** | Modal Flotante | Ventana modal superpuesta dentro de Pagos Pendientes que muestra el balance pendiente a liquidar, campos de método de pago y confirmación definitiva de cancelación. |

## 14. Relación con otros procesos

| Proceso | Relación |
| :--- | :--- |
| **Atención al Paciente** | Proporciona la receta oftálmica interna generada durante la consulta clínica para ser asociada al proceso de confección de lentes en la venta. |
| **Laboratorio** | Consume las ventas con receta registradas en estado de preparación para la emisión de las órdenes de trabajo y el tallado de cristales. |
| **Inventario / Almacén** | Abastece el catálogo de productos disponibles en venta y descuenta en tiempo real las existencias de monturas y accesorios confirmados en cada venta. |
| **Pasarela de Pagos Externa** | Servicio en la nube (Mercado Pago) que procesa las solicitudes de cobro con QR dinámico interoperable y envía eventos Webhook de confirmación financiera. |
| **Seguridad y Accesos** | Valida la identidad del vendedor autenticado y autoriza el acceso a las funciones de cobro y visualización comercial. |

## 15. Resumen final del proceso

El proceso de **Ventas de Geo Vision** opera bajo una arquitectura unificada que elimina la fragmentación innecesaria de flujos, consolidando en el caso de uso **CUS06 (Registrar Venta)** la venta de productos físicos, servicios profesionales y lentes con prescripción médica dentro de una experiencia integral de punto de venta.

El proceso reconoce la dinámica operativa del negocio óptico permitiendo dos modalidades de pago según la naturaleza del pedido: cobro al 100% para productos de entrega inmediata o servicios, y cobro mediante adelanto (con un piso del 25%) y programación de entrega estimada para pedidos que demandan elaboración en laboratorio. Para gestionar estas cuentas por cobrar de manera independiente y sin mezclar el flujo inicial, se dispone del caso de uso **CUS07 (Revisar Pago Pendiente)**, el cual permite ubicar la venta adeudada y liquidar el saldo restante al momento en que el paciente retira sus lentes terminados.

A nivel tecnológico, el proceso incorpora la **API de Mercado Pago con Código QR Dinámico Interoperable**, lo cual moderniza el mostrador permitiendo cobros sin contacto desde billeteras digitales (Yape, Plin y Mercado Pago) sin comprometer la privacidad del cliente con el dictado manual de datos bancarios. Esta integración digital convive armoniosamente con los cobros en efectivo y terminales POS físicos tradicionales. Finalmente, el caso de uso **CUS08 (Consultar Historial Ventas)** garantiza la trazabilidad operativa y administrativa de todas las órdenes comerciales mediante un panel de consulta enriquecido con filtros dinámicos y visualización modular de detalles.
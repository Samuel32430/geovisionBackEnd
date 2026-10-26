**IR – S\&Sw \- 2026**  
**Actividad U2.1: Realización de caso de uso**

Responsable: SAMUEL VILCA

Caso: CU-SEG15 \- Iniciar Sesión

Grupo: GEOVISION

# **1\. Reglas del negocio:**

| Nombre | Descripción | Caso de Uso |
| :---- | :---- | :---- |
| RN-SEG01 | Todo usuario debe iniciar sesión para acceder al sistema. | CU-SEG15 |
| RN-SEG02 | El usuario debe estar activo para poder ingresar al sistema. | CU-SEG15 |
| RN-SEG03 | El usuario solo debe visualizar los módulos permitidos según su rol. | CU-SEG15 |
| RN-SEG04 | El sistema debe impedir el acceso cuando las credenciales sean incorrectas o el usuario esté inactivo. | CU-SEG15 |

# **2\. Requerimientos funcionales:**

| Requerimientos funcionales | Caso de Uso | Actor |
| :---- | :---- | :---- |
| RF-SEG78: El sistema debe permitir iniciar sesión mediante usuario y contraseña. | CU-SEG15 | Usuario del sistema |

# **3\. Requerimientos NO funcionales:**

| Código | Requerimientos NO funcionales |
| :---- | :---- |
| RNF-SEG01 | El sistema debe permitir el acceso únicamente a usuarios autenticados. |
| RNF-SEG02 | El sistema debe responder al inicio de sesión y validación de acceso en un tiempo máximo de 3 segundos. |
| RNF-SEG03 | Las contraseñas deben almacenarse de forma segura. |
| RNF-SEG04 | La interfaz del módulo de seguridad debe ser clara, ordenada y fácil de usar. |
| RNF-SEG05 | El sistema debe mantener la integridad de la información de usuarios, roles y permisos. |
| RNF-SEG06 | El sistema debe impedir accesos no autorizados a módulos restringidos. |

# **4\. MATRIZ DEL PROYECTO**

Matriz del módulo de seguridad. Caso de uso trabajado marcado con X.

| Marcado | Código | Caso de uso | Requerimientos relacionados | Actores |
| :---- | :---- | :---- | :---- | :---- |
| X | CU-SEG15 | Iniciar Sesión | RF-SEG78 | Usuario del sistema |
|  | CU-SEG16 | Cerrar Sesión | RF-SEG79 | Usuario del sistema |
|  | CU-SEG17 | Gestionar Usuarios | RF-SEG80, RF-SEG81, RF-SEG82 | Administrador |
|  | CU-SEG18 | Gestionar Roles y Permisos | RF-SEG83, RF-SEG84, RF-SEG85 | Administrador |
|  | CU-SEG19 | Recuperar Contraseña | RF-SEG86, RF-SEG87, RF-SEG88, RF-SEG89, RF-SEG90 | Usuario del sistema |

Actividades del módulo de seguridad relacionadas con los casos de uso:

| N° | Actividad | Responsable | RF relacionado | Caso de uso |
| :---- | :---- | :---- | :---- | :---- |
| 76 | Iniciar sesión en el sistema | Usuario del sistema | RF-SEG78 | CU-SEG15 |

# **5\. REALIZACIÓN DE CASOS DE USO**

## **5.1 Especificación de CU**

| Campo | Detalle |
| :---- | :---- |
| Nombre | Iniciar Sesión |
| Tipo | Esencial y Primario |
| Versión | 1.0 |
| Autor | Samuel Vilca / Equipo Geo Vision |
| Actores | Actor principal: Usuario del sistema. Actor secundario: Sistema. |
| Iteración | Iteración 5 |
| Descripción | El caso de uso comienza cuando el usuario abre la pantalla de login e ingresa sus credenciales, y termina cuando el sistema valida el acceso y muestra los módulos permitidos según su rol. |
| Referencias | RF-SEG78 |
| Requerimientos especiales | El sistema debe validar credenciales en un tiempo máximo de 3 segundos, proteger la contraseña y mostrar mensajes claros ante errores de acceso. |
| Precondiciones | El usuario debe estar registrado, activo y contar con credenciales de acceso. |
| Post Condiciones | El usuario accede al sistema y visualiza únicamente los módulos autorizados según su rol. |

**Flujo normal de eventos**

| Acción del actor | Respuesta del sistema |
| :---- | :---- |
| El usuario abre la pantalla de login. | El sistema muestra el formulario de acceso. |
| El usuario ingresa usuario y contraseña. | El sistema valida que los campos estén completos. |
| El usuario presiona el botón “Iniciar sesión”. | El sistema verifica credenciales, estado del usuario y rol asignado. |
| El usuario espera el acceso. | El sistema muestra el dashboard con los módulos permitidos. |

**Flujo alternativo de eventos \<FA 01\>**

| Acción del actor | Respuesta del sistema |
| :---- | :---- |
| FA01 \- Credenciales incorrectas | El sistema muestra un mensaje indicando que el usuario o contraseña no son válidos. |
| FA02 \- Usuario inactivo | El sistema bloquea el acceso e informa que la cuenta está inactiva. |
| FA03 \- Campos vacíos | El sistema solicita completar usuario y contraseña. |

**Subflujo de eventos \<SF01\> \- Validación de credenciales**

| Acción del actor | Respuesta del sistema |
| :---- | :---- |
| El usuario ingresa sus credenciales. | El sistema verifica usuario, contraseña y estado de la cuenta. |
| El usuario solicita acceso. | El sistema identifica el rol asociado al usuario. |
| El usuario espera respuesta. | El sistema habilita el acceso si los datos son válidos. |

**Subflujo de eventos \<SF02\> \- Carga de módulos permitidos**

| Acción del actor | Respuesta del sistema |
| :---- | :---- |
| El usuario ingresa correctamente. | El sistema consulta los permisos del rol. |
| El usuario visualiza el dashboard. | El sistema muestra solo los módulos permitidos. |
| El usuario intenta ingresar a un módulo. | El sistema permite el acceso si el módulo está autorizado. |

**Anexos**

Anexo 1: El caso de uso pertenece al Módulo de Seguridad del sistema de ventas para óptica.

Anexo 2: Las actividades del negocio mantienen responsables humanos; las validaciones del sistema se describen en los flujos y requerimientos funcionales.

## **5.2 Prototipos del Caso de Uso (completo)**

Pantalla de Login con campos usuario y contraseña, botón Iniciar sesión, enlace Recuperar contraseña y mensajes de validación. Luego, dashboard con módulos permitidos según rol.

## **5.3 Checklist**

| Nro | Criterio | Pregunta de validación | Sí/No | Observaciones |
| :---- | :---- | :---- | :---- | :---- |
| 1 | Sintaxis del nombre | Verbo \+ Objeto | Sí | El nombre mantiene estructura funcional. |
| 2 | Nombre y Propósito del CU | ¿El caso de uso tiene un nombre que represente claramente su propósito? | Sí | El propósito se entiende con claridad. |
| 3 | Actor(es) definidos | ¿Están correctamente identificados el o los actores que interactúan? | Sí | Se indican actor principal y actor secundario. |
| 4 | Precondiciones | ¿Se especifica claramente qué debe cumplirse antes de iniciar el caso? | Sí | Las condiciones previas están definidas. |
| 5 | Postcondiciones | ¿Se define claramente el estado esperado tras su ejecución? | Sí | Se describe el resultado final del caso. |
| 6 | Flujo principal completo | ¿El flujo básico cubre desde la activación hasta la finalización? | Sí | El flujo inicia y termina dentro del proceso. |
| 7 | Flujos alternos/subflujos | ¿Se han identificado y descrito adecuadamente? | Sí | Incluye validaciones y situaciones alternativas. |
| 8 | Relaciones con otros casos | ¿Se documentan correctamente las inclusiones o extensiones? | Sí | Se relaciona con el módulo de seguridad y acceso a módulos. |
| 9 | Requisitos asociados | ¿Se han derivado o conectado los requisitos funcionales, RNF, o reglas de negocio, respecto al CU? | Sí | Los RF, RNF y reglas están vinculados. |
| 10 | Consistencia de negocio | ¿Está alineado con los procesos del negocio descritos? | Sí | Está alineado al control de acceso del sistema. |

## **CHECKLIST DE USABILIDAD**

| Cód. | Heurística | ✅ Cumple | ❌ No cumple | ⚠ Parcial | Comentario general |
| :---- | :---- | :---- | :---- | :---- | :---- |
| H1 | Visibilidad del estado del sistema | ✅ |  |  | El sistema muestra mensajes de acceso, error o confirmación. |
| H2 | Relación entre el sistema y el mundo real | ✅ |  |  | Usa términos conocidos: usuario, rol, permiso y contraseña. |
| H3 | Control y libertad del usuario | ✅ |  |  | Permite cancelar o retornar antes de confirmar acciones. |
| H4 | Consistencia y estándares | ✅ |  |  | Mantiene estructura similar en pantallas del módulo. |
| H5 | Prevención de errores | ✅ |  |  | Valida campos obligatorios y estados antes de registrar. |
| H6 | Reconocimiento antes que memoria | ✅ |  |  | Muestra listas de roles, usuarios y módulos disponibles. |
| H7 | Flexibilidad y eficiencia de uso | ✅ |  |  | Permite buscar usuarios y configurar permisos de forma directa. |
| H8 | Diseño estético y minimalista | ✅ |  |  | Secciones claras y sin información innecesaria. |
| H9 | Ayuda para reconocer y corregir errores | ✅ |  |  | Muestra mensajes claros ante errores de ingreso o validación. |
| H10 | Ayuda y documentación | ⚠ |  |  | Puede requerir texto breve de ayuda en recuperación de contraseña. |

**TOTAL GENERAL: Cumple con las heurísticas principales para una interfaz clara, segura y ordenada.**
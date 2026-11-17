**MÓDULO DE SEGURIDAD**

**1\. Objetivo del módulo**

Controlar el acceso al sistema mediante usuarios, contraseñas, roles y permisos, permitiendo que cada trabajador ingrese únicamente a los módulos y funciones que le correspondan según su responsabilidad dentro de la óptica.

El módulo busca proteger la información del sistema, evitar accesos no autorizados y permitir la recuperación de contraseña cuando un usuario olvide sus credenciales.

---

**2\. Alcance del módulo**

**Incluye**

* Iniciar sesión con usuario y contraseña. 

* Cerrar sesión de forma segura. 

* Registrar nuevos usuarios del sistema. 

* Editar datos de usuarios. 

* Desactivar usuarios. 

* Asignar roles a usuarios. 

* Asignar permisos a roles. 

* Definir módulos permitidos por rol. 

* Mostrar solo los módulos autorizados para cada usuario. 

* Recuperar contraseña en caso de olvido. 

**No incluye**

* Registro de pacientes. 

* Registro de ventas. 

* Registro de laboratorio. 

* Registro de adquisiciones. 

* Generación de reportes. 

* Registro clínico o recetas. 

* Gestión de productos o stock. 

---

**3\. Actores del módulo**

| Actor | Descripción |
| :---- | :---- |
| Administrador | Usuario encargado de registrar usuarios, editar datos, desactivar cuentas, asignar roles y configurar permisos. |
| Usuario del sistema | Trabajador que inicia sesión, accede a los módulos permitidos, cierra sesión o recupera su contraseña. |
| Sistema | Valida credenciales, controla accesos, muestra módulos permitidos, bloquea opciones restringidas y actualiza contraseñas. |

---

**4\. Casos de uso del módulo**

| Código | Caso de uso | Descripción |
| :---- | :---- | :---- |
| CU-SEG15 | Iniciar Sesión | Permite al usuario ingresar al sistema mediante usuario y contraseña. |
| CU-SEG16 | Cerrar Sesión | Permite al usuario salir del sistema de forma segura. |
| CU-SEG17 | Gestionar Usuarios | Permite al administrador registrar, editar y desactivar usuarios. |
| CU-SEG18 | Gestionar Roles y Permisos | Permite asignar roles, permisos y módulos permitidos a los usuarios. |
| CU-SEG19 | Recuperar Contraseña | Permite al usuario recuperar el acceso al sistema si olvidó su contraseña. |

---

**5\. Actividades del negocio**

**Nota:** En las actividades del negocio solo se consideran responsables humanos. El sistema no aparece como responsable porque su participación se representa en los requerimientos funcionales y en los flujos de respuesta del caso de uso.

| N° | Actividad | Responsable del negocio | Caso de uso |
| :---- | :---- | :---- | :---- |
| 76 | Iniciar sesión en el sistema | Usuario del sistema | CU-SEG15 |
| 77 | Cerrar sesión del sistema | Usuario del sistema | CU-SEG16 |
| 78 | Registrar nuevo usuario | Administrador | CU-SEG17 |
| 79 | Editar datos de usuario | Administrador | CU-SEG17 |
| 80 | Desactivar usuario | Administrador | CU-SEG17 |
| 81 | Asignar rol a usuario | Administrador | CU-SEG18 |
| 82 | Asignar permisos a rol | Administrador | CU-SEG18 |
| 83 | Definir módulos permitidos por rol | Administrador | CU-SEG18 |
| 84 | Solicitar recuperación de contraseña | Usuario del sistema | CU-SEG19 |
| 85 | Ingresar usuario o correo registrado | Usuario del sistema | CU-SEG19 |
| 86 | Validar cuenta y código de recuperación | Usuario del sistema | CU-SEG19 |
| 87 | Registrar nueva contraseña | Usuario del sistema | CU-SEG19 |
| 88 | Confirmar cambio de contraseña | Usuario del sistema | CU-SEG19 |

---

**6\. Requerimientos funcionales**

Cada actividad tiene un requerimiento funcional relacionado.

| Código | Requerimiento funcional | Caso de uso | Actor |
| :---- | :---- | :---- | :---- |
| RF-SEG78 | El sistema debe permitir iniciar sesión mediante usuario y contraseña. | CU-SEG15 | Usuario del sistema |
| RF-SEG79 | El sistema debe permitir cerrar sesión de forma segura. | CU-SEG16 | Usuario del sistema |
| RF-SEG80 | El sistema debe permitir registrar nuevos usuarios. | CU-SEG17 | Administrador |
| RF-SEG81 | El sistema debe permitir editar los datos de un usuario. | CU-SEG17 | Administrador |
| RF-SEG82 | El sistema debe permitir desactivar usuarios. | CU-SEG17 | Administrador |
| RF-SEG83 | El sistema debe permitir asignar un rol a cada usuario. | CU-SEG18 | Administrador |
| RF-SEG84 | El sistema debe permitir asignar permisos a cada rol. | CU-SEG18 | Administrador |
| RF-SEG85 | El sistema debe permitir definir los módulos permitidos según el rol del usuario. | CU-SEG18 | Administrador |
| RF-SEG86 | El sistema debe permitir solicitar la recuperación de contraseña. | CU-SEG19 | Usuario del sistema |
| RF-SEG87 | El sistema debe permitir ingresar usuario o correo registrado para recuperar la contraseña. | CU-SEG19 | Usuario del sistema |
| RF-SEG88 | El sistema debe validar la cuenta y el código de recuperación. | CU-SEG19 | Usuario del sistema |
| RF-SEG89 | El sistema debe permitir registrar una nueva contraseña. | CU-SEG19 | Usuario del sistema |
| RF-SEG90 | El sistema debe actualizar la contraseña del usuario. | CU-SEG19 | Usuario del sistema |

---

**7\. Requerimientos no funcionales**

| Código | Requerimiento no funcional |
| :---- | :---- |
| RNF-SEG01 | El sistema debe permitir el acceso únicamente a usuarios autenticados. |
| RNF-SEG02 | El sistema debe responder al inicio de sesión y validación de acceso en un tiempo máximo de 3 segundos. |
| RNF-SEG03 | Las contraseñas deben almacenarse de forma segura. |
| RNF-SEG04 | La interfaz del módulo de seguridad debe ser clara, ordenada y fácil de usar. |
| RNF-SEG05 | El sistema debe mantener la integridad de la información de usuarios, roles y permisos. |
| RNF-SEG06 | El sistema debe impedir accesos no autorizados a módulos restringidos. |
| RNF-SEG07 | El sistema debe mostrar mensajes claros cuando las credenciales sean incorrectas o falten datos obligatorios. |
| RNF-SEG08 | El sistema debe permitir recuperar la contraseña de forma segura y controlada. |

---

**8\. Reglas de negocio**

| Código | Regla de negocio |
| :---- | :---- |
| RN-SEG01 | Todo usuario debe iniciar sesión para acceder al sistema. |
| RN-SEG02 | El usuario debe estar activo para poder ingresar al sistema. |
| RN-SEG03 | El usuario solo debe visualizar los módulos permitidos según su rol. |
| RN-SEG04 | El sistema debe impedir el acceso a módulos no autorizados. |
| RN-SEG05 | Solo el administrador puede registrar usuarios. |
| RN-SEG06 | Solo el administrador puede editar datos de usuarios. |
| RN-SEG07 | Solo el administrador puede desactivar usuarios. |
| RN-SEG08 | No se debe eliminar físicamente un usuario; solo se debe desactivar. |
| RN-SEG09 | Todo usuario debe tener un rol asignado. |
| RN-SEG10 | Cada rol debe tener permisos definidos. |
| RN-SEG11 | El cierre de sesión debe finalizar el acceso activo del usuario. |
| RN-SEG12 | La recuperación de contraseña solo debe permitirse para cuentas registradas y activas. |
| RN-SEG13 | La nueva contraseña debe reemplazar la contraseña anterior después de confirmarse el cambio. |

---

**9\. Descripción de casos de uso**

**CU-SEG15: Iniciar Sesión**

Permite al usuario ingresar al sistema mediante usuario y contraseña. El usuario abre la pantalla de login, ingresa sus credenciales y solicita el acceso. El sistema valida los datos, verifica que el usuario esté activo y muestra los módulos permitidos según su rol.

**Resultado:** el usuario accede al sistema y visualiza únicamente los módulos autorizados.

---

**CU-SEG16: Cerrar Sesión**

Permite al usuario salir del sistema de forma segura. El usuario selecciona la opción de cerrar sesión, confirma la acción y el sistema finaliza la sesión activa.

**Resultado:** la sesión queda cerrada y el usuario retorna a la pantalla de login.

---

**CU-SEG17: Gestionar Usuarios**

Permite al administrador registrar nuevos usuarios, editar sus datos y desactivar cuentas cuando corresponda. Este caso de uso sirve para mantener controlados los trabajadores que pueden ingresar al sistema.

**Resultado:** los usuarios quedan registrados, actualizados o desactivados según la acción realizada.

---

**CU-SEG18: Gestionar Roles y Permisos**

Permite al administrador asignar roles a los usuarios, configurar permisos y definir qué módulos puede visualizar o utilizar cada rol. Este caso de uso reemplaza la necesidad de tener un caso separado para “Controlar Acceso a Módulos”, porque el acceso queda definido desde los permisos configurados.

**Resultado:** cada usuario queda asociado a un rol y solo puede acceder a los módulos permitidos.

---

**CU-SEG19: Recuperar Contraseña**

Permite al usuario recuperar el acceso cuando olvida su contraseña. El usuario solicita la recuperación, ingresa su usuario o correo, valida su cuenta mediante un código de recuperación y registra una nueva contraseña.

**Resultado:** la contraseña queda actualizada y el usuario puede volver a iniciar sesión.

---

**10\. Flujo general del módulo**

1. El usuario abre la pantalla de login. 

2. El usuario ingresa su usuario y contraseña. 

3. El sistema valida las credenciales. 

4. El sistema verifica que el usuario esté activo. 

5. El sistema identifica el rol del usuario. 

6. El sistema muestra únicamente los módulos permitidos. 

7. El usuario trabaja solo en los módulos autorizados. 

8. Si necesita salir, el usuario cierra sesión. 

9. Si olvida su contraseña, solicita recuperación desde el login. 

10. El usuario ingresa su usuario o correo registrado. 

11. El usuario valida el código de recuperación. 

12. El usuario registra una nueva contraseña. 

13. El administrador puede registrar, editar o desactivar usuarios. 

14. El administrador puede asignar roles y permisos. 

---

**11\. Matriz de relación: actividades y requerimientos**

| Actividad | Requerimiento funcional relacionado | Caso de uso |
| :---- | :---- | :---- |
| Iniciar sesión en el sistema | RF-SEG78 | CU-SEG15 |
| Cerrar sesión del sistema | RF-SEG79 | CU-SEG16 |
| Registrar nuevo usuario | RF-SEG80 | CU-SEG17 |
| Editar datos de usuario | RF-SEG81 | CU-SEG17 |
| Desactivar usuario | RF-SEG82 | CU-SEG17 |
| Asignar rol a usuario | RF-SEG83 | CU-SEG18 |
| Asignar permisos a rol | RF-SEG84 | CU-SEG18 |
| Definir módulos permitidos por rol | RF-SEG85 | CU-SEG18 |
| Solicitar recuperación de contraseña | RF-SEG86 | CU-SEG19 |
| Ingresar usuario o correo registrado | RF-SEG87 | CU-SEG19 |
| Validar cuenta y código de recuperación | RF-SEG88 | CU-SEG19 |
| Registrar nueva contraseña | RF-SEG89 | CU-SEG19 |
| Confirmar cambio de contraseña | RF-SEG90 | CU-SEG19 |

---

**14\. Pantallas necesarias**

| Pantalla | Descripción |
| :---- | :---- |
| Login | Pantalla para ingresar usuario y contraseña. |
| Recuperar Contraseña | Pantalla para solicitar recuperación, ingresar código y registrar nueva contraseña. |
| Dashboard por rol | Pantalla principal que muestra solo los módulos permitidos. |
| Gestión de Usuarios | Pantalla para registrar, editar y desactivar usuarios. |
| Gestión de Roles y Permisos | Pantalla para asignar roles, permisos y módulos permitidos. |
| Acceso Denegado | Mensaje o pantalla cuando el usuario intenta ingresar a un módulo no autorizado. |

---

**15\. Relación con otros procesos**

| Proceso | Relación |
| :---- | :---- |
| Atención al Paciente | Seguridad controla qué usuarios pueden acceder a datos de pacientes, exámenes y recetas. |
| Ventas | Seguridad define qué usuarios pueden registrar ventas y consultar historial. |
| Laboratorio | Seguridad controla el acceso a órdenes de trabajo y control de calidad. |
| Adquisiciones | Seguridad restringe el acceso a requerimientos de compra y recepción de productos. |
| Reportes | Seguridad define qué usuarios pueden consultar información del negocio. |

---

**16\. Resumen final del módulo**

El módulo de Seguridad permite controlar el acceso al sistema mediante usuarios, contraseñas, roles y permisos. El usuario inicia sesión y el sistema valida sus credenciales, verifica que la cuenta esté activa y muestra únicamente los módulos permitidos según su rol.

El administrador puede gestionar usuarios, asignar roles y configurar permisos para definir qué módulos puede utilizar cada trabajador. Si un usuario intenta acceder a un módulo no autorizado, el sistema bloquea el acceso. Además, el módulo permite recuperar la contraseña cuando un usuario olvida sus credenciales, siempre que la cuenta exista y esté activa.

Con este módulo, cada trabajador utiliza solo las funciones que le corresponden dentro del sistema, protegiendo la información y manteniendo el control de acceso.


workspace "GeoVision" "Sistema de Gestión Comercial, Clínica Oftalmológica y Manufactura Óptica" {

    model {
        # Actores del Sistema
        admin = person "Administrador" "Gestiona usuarios, configuración general, catálogo óptico, inventario y reportes consolidados." "Persona"
        vendedor = person "Vendedor" "Atiende pacientes en mostrador, registra ventas, cobra anticipos y emite comprobantes oficiales." "Persona"
        optometra = person "Optómetra" "Efectúa exámenes clínicos de refracción visual y prescribe recetas oftálmicas diótricas." "Persona"
        tecnico = person "Técnico de Taller" "Controla órdenes de manufactura, gestiona despachos a laboratorio y realiza control de calidad por lensometría." "Persona"
        paciente = person "Paciente" "Recibe atención clínica visual, recetas médicas y notificaciones de entrega de anteojos." "Persona"

        # Sistemas Externos
        mercadoPago = softwareSystem "Mercado Pago" "Pasarela de pagos para generación de códigos QR y confirmación vía Webhooks." "Sistema externo"
        laboratorioExterno = softwareSystem "Laboratorios Ópticos Externos" "Talleres de tallado y tratamiento de cristales oftálmicos especializados." "Sistema externo"
        servidorSmtp = softwareSystem "Servidor SMTP Gmail" "Servicio de mensajería para notificación de credenciales y reseteo de contraseñas." "Sistema externo"

        # Sistema de Software Principal
        geoVision = softwareSystem "GeoVision" "Plataforma empresarial para la gestión clínica, comercial y manufactura óptica." {
            
            # Contenedores
            spa = container "SPA GeoVision" "Interfaz web interactiva para usuarios: login, atención clínica, ventas, taller, inventario y reportes." "Angular 22, TypeScript, Tailwind" "SPA"
            webServer = container "Servidor Web Nginx" "Servidor web proxy inverso con compresión Gzip, política de caché inmutable y ruteo /api." "Nginx 1.27, Alpine Linux" "Servidor Web"
            backend = container "GeoVision Backend" "API REST stateless con reglas de negocio, control transaccional, seguridad RBAC/JWT, reportes PDF y auditoría." "Spring Boot 4, Java 21 LTS" "Backend"
            database = container "Base de Datos Oracle" "Persistencia relacional ACID, esquemas normalizados, tablas particionadas, triggers y procedimientos PL/SQL." "Oracle Database 19c/23c Enterprise" "Base de datos"
            cache = container "Caché Distribuido Redis" "Caché en memoria para catálogos ópticos frecuentes, parámetros de mantenimiento y sesiones." "Redis 7, Alpine" "Caché"

            # Componentes internos del Backend
            filtrosSeguridad = component "Filtros de Seguridad" "Valida Bearer Tokens JWT, extrae identidad del operador y asigna identificador de petición." "JwtAuthenticationFilter"
            controladoresRest = component "Controladores REST" "Exponen endpoints HTTP para autenticación, pacientes, recetas, ventas, pagos, órdenes de trabajo e inventario." "@RestController"
            serviciosDominio = component "Servicios de Dominio" "Implementan reglas de negocio (RN-VE, RN-OT, RN-CC), orquestación transaccional y caching." "@Service @Transactional"
            manejoErrores = component "Manejo Global de Excepciones" "Traduce excepciones a códigos HTTP estándar (400, 401, 403, 404, 409 y 500) en formato JSON unificado." "@RestControllerAdvice"
            utilitariosPdf = component "Generadores de Reportes y Comprobantes" "Generación nativa de comprobantes de venta, constancias clínicas y reportes consolidados." "Apache PDFBox 3.0.8"
            repositoriosJpa = component "Repositorios de Persistencia" "Operaciones CRUD paginadas, consultas JPQL, bloqueos pesimistas ordenados y mapping de vistas." "Spring Data JPA"
            auditoriaLogs = component "Módulo de Auditoría y Trazabilidad" "Registro de logs operacionales para trazabilidad forense de accesos, pacientes y movimientos kardex." "Listener y Log Repositories"
        }

        # Relaciones Nivel 1 (Contexto)
        admin -> geoVision "Administra usuarios, configuraciones y analiza reportes globales [HTTPS]"
        vendedor -> geoVision "Registra ventas, anticipos de pago y consulta comprobantes [HTTPS]"
        optometra -> geoVision "Registra atenciones y emite recetas oftálmicas diótricas [HTTPS]"
        tecnico -> geoVision "Controla órdenes de taller, evalúa lensometría y entrega pedidos [HTTPS]"
        paciente -> geoVision "Consulta prescripción y recibe notificaciones de recojo [HTTPS]"
        geoVision -> mercadoPago "Genera intenciones de cobro QR y recibe eventos webhook [HTTPS/JSON]"
        geoVision -> laboratorioExterno "Envía fichas de trabajo y especificaciones diótricas [Email/Web]"
        geoVision -> servidorSmtp "Envía credenciales de acceso y códigos de restablecimiento [SMTP/TLS]"

        # Relaciones Nivel 2 (Contenedores)
        vendedor -> webServer "Descarga la SPA en el navegador [HTTPS/443]"
        tecnico -> webServer "Descarga la SPA en el navegador [HTTPS/443]"
        admin -> webServer "Descarga la SPA en el navegador [HTTPS/443]"
        optometra -> webServer "Descarga la SPA en el navegador [HTTPS/443]"
        webServer -> spa "Sirve bundles compilados JS/CSS con política de caché inmutable"
        spa -> webServer "Envía peticiones API /api/v1/* [JSON, JWT]"
        webServer -> backend "Reenvía peticiones reverse proxy /api a backend:8080 [HTTP]"
        backend -> database "Lee y escribe entidades de dominio, ejecuta SPs y consultas analíticas [JDBC/JPA]"
        backend -> cache "Consulta y almacena en caché catálogos y parámetros con TTL [RESP]"
        backend -> mercadoPago "Comunica pagos electrónicos QR [HTTPS/REST]"
        backend -> servidorSmtp "Envía correos electrónicos transaccionales [SMTP/TLS]"

        # Relaciones Nivel 3 (Componentes Backend)
        webServer -> filtrosSeguridad "Pasa peticiones entrantes con encabezado Authorization Bearer [HTTP]"
        filtrosSeguridad -> controladoresRest "Pasa peticiones autenticadas y autorizadas con SecurityContext"
        controladoresRest -> serviciosDominio "Delega procesamiento de negocio y casos de uso"
        controladoresRest -> manejoErrores "Delega excepciones descontroladas o de negocio (ReglaNegocioException)"
        serviciosDominio -> repositoriosJpa "Ejecuta operaciones transaccionales y queries paginados"
        serviciosDominio -> cache "Consulta/invalida datos con @Cacheable y @CacheEvict [RESP]"
        serviciosDominio -> utilitariosPdf "Invoca compilación de documentos PDF para comprobantes y recetas"
        serviciosDominio -> auditoriaLogs "Emite trazas operacionales sobre eventos críticos"
        repositoriosJpa -> database "Lee y escribe esquemas normalizados y ejecuta SPs [JDBC]"
        auditoriaLogs -> database "Registra eventos inmutables en tablas *_LOG [JDBC]"
    }

    views {
        systemContext geoVision "DiagramaContexto" "Diagrama de Contexto del Sistema GeoVision (Nivel 1)" {
            include *
            autoLayout lr
        }

        container geoVision "DiagramaContenedores" "Diagrama de Contenedores del Sistema GeoVision (Nivel 2)" {
            include *
            autoLayout tb
        }

        component backend "DiagramaComponentes" "Diagrama de Componentes del Backend GeoVision (Nivel 3)" {
            include *
            autoLayout tb
        }

        styles {
            element "Persona" {
                background #08427b
                color #ffffff
                shape Person
            }
            element "Sistema externo" {
                background #999999
                color #ffffff
            }
            element "SPA" {
                background #1168bd
                color #ffffff
                shape WebBrowser
            }
            element "Servidor Web" {
                background #438dd5
                color #ffffff
            }
            element "Backend" {
                background #1168bd
                color #ffffff
            }
            element "Base de datos" {
                background #225588
                color #ffffff
                shape Cylinder
            }
            element "Caché" {
                background #2277bb
                color #ffffff
                shape Cylinder
            }
            element "Component" {
                background #85bbf0
                color #000000
            }
        }
    }
}

package pe.edu.upeu.geovisionBackEnd.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovisionBackEnd.entity.Permiso;
import pe.edu.upeu.geovisionBackEnd.entity.Rol;
import pe.edu.upeu.geovisionBackEnd.entity.RolPermiso;
import pe.edu.upeu.geovisionBackEnd.enums.ModuloEnum;
import pe.edu.upeu.geovisionBackEnd.repository.PermisoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.RolPermisoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.RolRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Inicializador de datos de seguridad para desarrollo y pruebas.
 * Asegura la existencia de los roles y permisos iniciales de catálogo.
 */
@Component
@Profile("!prod")
public class SecurityDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SecurityDataInitializer.class);

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final RolPermisoRepository rolPermisoRepository;

    public SecurityDataInitializer(
            RolRepository rolRepository,
            PermisoRepository permisoRepository,
            RolPermisoRepository rolPermisoRepository) {
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
        this.rolPermisoRepository = rolPermisoRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        try {
            log.info("Verificando catálogo base de roles y permisos...");

            // 1. Permisos base por módulo
            List<Permiso> permisosCreados = new ArrayList<>();

            permisosCreados.add(crearPermisoSiNoExiste("SEGURIDAD_ADMINISTRAR", ModuloEnum.SEGURIDAD));
            permisosCreados.add(crearPermisoSiNoExiste("SEGURIDAD_LEER", ModuloEnum.SEGURIDAD));

            permisosCreados.add(crearPermisoSiNoExiste("PACIENTES_LEER", ModuloEnum.ATENCION_PACIENTES));
            permisosCreados.add(crearPermisoSiNoExiste("PACIENTES_REGISTRAR", ModuloEnum.ATENCION_PACIENTES));
            permisosCreados.add(crearPermisoSiNoExiste("PACIENTES_GESTIONAR", ModuloEnum.ATENCION_PACIENTES));
            permisosCreados.add(crearPermisoSiNoExiste("EXAMENES_REGISTRAR", ModuloEnum.ATENCION_PACIENTES));
            permisosCreados.add(crearPermisoSiNoExiste("RECETAS_REGISTRAR", ModuloEnum.ATENCION_PACIENTES));

            permisosCreados.add(crearPermisoSiNoExiste("VENTAS_LEER", ModuloEnum.VENTAS));
            permisosCreados.add(crearPermisoSiNoExiste("VENTAS_REGISTRAR", ModuloEnum.VENTAS));
            permisosCreados.add(crearPermisoSiNoExiste("PAGOS_GESTIONAR", ModuloEnum.VENTAS));

            permisosCreados.add(crearPermisoSiNoExiste("ORDENES_TRABAJO_LEER", ModuloEnum.ORDENES_TRABAJO));
            permisosCreados.add(crearPermisoSiNoExiste("ORDENES_TRABAJO_GESTIONAR", ModuloEnum.ORDENES_TRABAJO));

            permisosCreados.add(crearPermisoSiNoExiste("INVENTARIO_LEER", ModuloEnum.INVENTARIO));
            permisosCreados.add(crearPermisoSiNoExiste("INVENTARIO_GESTIONAR", ModuloEnum.INVENTARIO));

            permisosCreados.add(crearPermisoSiNoExiste("MANTENIMIENTO_GESTIONAR", ModuloEnum.GESTION_MANTENIMIENTO));
            permisosCreados.add(crearPermisoSiNoExiste("REPORTES_VER", ModuloEnum.REPORTES));

            // 2. Roles base
            Rol admin = crearRolSiNoExiste("ADMINISTRADOR");
            Rol vendedor = crearRolSiNoExiste("VENDEDOR");
            Rol optometra = crearRolSiNoExiste("OPTOMETRA");
            Rol tecnico = crearRolSiNoExiste("TECNICO");
            Rol recepcionista = crearRolSiNoExiste("RECEPCIONISTA");

            // 3. Asignación inicial de permisos a ADMINISTRADOR si no tiene
            if (admin != null) {
                for (Permiso p : permisosCreados) {
                    if (p != null && !rolPermisoRepository.existsByRolIdRolAndPermisoIdPermiso(admin.getIdRol(), p.getIdPermiso())) {
                        rolPermisoRepository.save(RolPermiso.builder().rol(admin).permiso(p).build());
                    }
                }
            }

            // 4. Asignación inicial de ejemplo para VENDEDOR (Ventas + Registrar Paciente)
            if (vendedor != null) {
                asignarPermisoARol(vendedor, "VENTAS_LEER");
                asignarPermisoARol(vendedor, "VENTAS_REGISTRAR");
                asignarPermisoARol(vendedor, "PAGOS_GESTIONAR");
                asignarPermisoARol(vendedor, "PACIENTES_LEER");
                asignarPermisoARol(vendedor, "PACIENTES_REGISTRAR");
            }

            log.info("Catálogo base de seguridad inicializado correctamente");
        } catch (Exception e) {
            log.warn("Inicialización de catálogo de seguridad omitida o no requerida: {}", e.getMessage());
        }
    }

    private Permiso crearPermisoSiNoExiste(String nombre, ModuloEnum modulo) {
        return permisoRepository.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> permisoRepository.save(Permiso.builder()
                        .nombre(nombre)
                        .modulo(modulo)
                        .build()));
    }

    private Rol crearRolSiNoExiste(String nombre) {
        return rolRepository.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> rolRepository.save(Rol.builder()
                        .nombre(nombre)
                        .build()));
    }

    private void asignarPermisoARol(Rol rol, String nombrePermiso) {
        permisoRepository.findByNombreIgnoreCase(nombrePermiso).ifPresent(p -> {
            if (!rolPermisoRepository.existsByRolIdRolAndPermisoIdPermiso(rol.getIdRol(), p.getIdPermiso())) {
                rolPermisoRepository.save(RolPermiso.builder().rol(rol).permiso(p).build());
            }
        });
    }
}

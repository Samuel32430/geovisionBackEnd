package pe.edu.upeu.geovision_backend.config.security;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.entity.security.Permiso;
import pe.edu.upeu.geovision_backend.entity.security.Rol;
import pe.edu.upeu.geovision_backend.entity.security.RolPermiso;
import pe.edu.upeu.geovision_backend.enums.security.ModuloEnum;
import pe.edu.upeu.geovision_backend.repository.security.PermisoRepository;
import pe.edu.upeu.geovision_backend.repository.security.RolPermisoRepository;
import pe.edu.upeu.geovision_backend.repository.security.RolRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile({"dev", "test"})
@RequiredArgsConstructor
public class SecurityDataInitializer implements CommandLineRunner {

    // >>>> CONSTANTS
    private static final Logger log = LoggerFactory.getLogger(SecurityDataInitializer.class);

    // >>>> DEPENDENCIES
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final RolPermisoRepository rolPermisoRepository;

    // >>>> OPERATIONS
    @Override
    @Transactional
    public void run(String... args) {
        try {
            log.info("Verificando catálogo base de roles y permisos...");

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
            permisosCreados.add(crearPermisoSiNoExiste("ORDENES_TRABAJO_CALIDAD", ModuloEnum.ORDENES_TRABAJO));

            permisosCreados.add(crearPermisoSiNoExiste("INVENTARIO_LEER", ModuloEnum.INVENTARIO));
            permisosCreados.add(crearPermisoSiNoExiste("PRODUCTOS_LEER", ModuloEnum.INVENTARIO));
            permisosCreados.add(crearPermisoSiNoExiste("PRODUCTOS_REGISTRAR", ModuloEnum.INVENTARIO));
            permisosCreados.add(crearPermisoSiNoExiste("PRODUCTOS_GESTIONAR", ModuloEnum.INVENTARIO));
            permisosCreados.add(crearPermisoSiNoExiste("INVENTARIO_REGISTRAR", ModuloEnum.INVENTARIO));
            permisosCreados.add(crearPermisoSiNoExiste("INVENTARIO_AJUSTAR", ModuloEnum.INVENTARIO));
            permisosCreados.add(crearPermisoSiNoExiste("INVENTARIO_GESTIONAR", ModuloEnum.INVENTARIO));

            permisosCreados.add(crearPermisoSiNoExiste("MANTENIMIENTO_GESTIONAR", ModuloEnum.GESTION_MANTENIMIENTO));
            permisosCreados.add(crearPermisoSiNoExiste("REPORTES_VER", ModuloEnum.REPORTES));

            Rol admin = crearRolSiNoExiste("ADMINISTRADOR");
            Rol vendedor = crearRolSiNoExiste("VENDEDOR");
            Rol optometra = crearRolSiNoExiste("OPTOMETRA");
            Rol tecnico = crearRolSiNoExiste("TECNICO");
            Rol recepcionista = crearRolSiNoExiste("RECEPCIONISTA");

            if (admin != null) {
                for (Permiso p : permisosCreados) {
                    if (p != null && !rolPermisoRepository.existsByRolIdAndPermisoId(admin.getId(), p.getId())) {
                        rolPermisoRepository.save(new RolPermiso(null, admin, p));
                    }
                }
            }

            if (vendedor != null) {
                asignarPermisoARol(vendedor, "VENTAS_LEER");
                asignarPermisoARol(vendedor, "VENTAS_REGISTRAR");
                asignarPermisoARol(vendedor, "PAGOS_GESTIONAR");
                asignarPermisoARol(vendedor, "PACIENTES_LEER");
                asignarPermisoARol(vendedor, "PACIENTES_REGISTRAR");
                asignarPermisoARol(vendedor, "PRODUCTOS_LEER");
                asignarPermisoARol(vendedor, "INVENTARIO_LEER");
            }

            log.info("Catálogo base de seguridad inicializado correctamente");
        } catch (Exception e) {
            log.warn("Inicialización de catálogo de seguridad omitida o no requerida: {}", e.getMessage());
        }
    }

    // >>>> HELPERS
    private Permiso crearPermisoSiNoExiste(String nombre, ModuloEnum modulo) {
        return permisoRepository.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> permisoRepository.save(new Permiso(null, nombre, modulo)));
    }

    private Rol crearRolSiNoExiste(String nombre) {
        return rolRepository.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> rolRepository.save(new Rol(null, nombre)));
    }

    private void asignarPermisoARol(Rol rol, String nombrePermiso) {
        permisoRepository.findByNombreIgnoreCase(nombrePermiso).ifPresent(p -> {
            if (!rolPermisoRepository.existsByRolIdAndPermisoId(rol.getId(), p.getId())) {
                rolPermisoRepository.save(new RolPermiso(null, rol, p));
            }
        }
        );
    }
}

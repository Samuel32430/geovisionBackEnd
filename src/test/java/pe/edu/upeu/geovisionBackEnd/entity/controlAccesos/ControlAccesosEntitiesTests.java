package pe.edu.upeu.geovisionBackEnd.entity.controlAccesos;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Persona;
import pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento.Cargo;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ControlAccesosEntitiesTests {

    @Test
    void allControlAccesosMappingsBuildWithoutDatabase() {
        var registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.OracleDialect")
                .applySetting("hibernate.boot.allow_jdbc_metadata_access", false)
                .applySetting("hibernate.hbm2ddl.auto", "none")
                .build();
        try {
            var sources = new MetadataSources(registry);
            for (var type : new Class<?>[]{
                    Persona.class, Cargo.class,
                    Usuario.class, Empleado.class, Rol.class, Modulo.class, Permiso.class,
                    UsuarioRol.class, RolPermiso.class, Auditoria.class, EmpleadoLog.class}) {
                sources.addAnnotatedClass(type);
            }
            try (var factory = sources.buildMetadata().buildSessionFactory()) {
                assertEquals(11, factory.getMetamodel().getEntities().size());
            }
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}

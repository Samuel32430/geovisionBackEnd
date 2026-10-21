package pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta;

import jakarta.validation.Validation;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PatientEntitiesTests {
    @Test
    void allPatientMappingsBuildWithoutOtherModulesOrDatabase() {
        var registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.OracleDialect")
                .applySetting("hibernate.boot.allow_jdbc_metadata_access", false)
                .applySetting("hibernate.hbm2ddl.auto", "none")
                .build();
        try {
            var sources = new MetadataSources(registry);
            for (var type : new Class<?>[]{Persona.class, Documento.class, Paciente.class,
                    Examen.class, Medida.class, MedidaOjo.class, Receta.class, PacienteLog.class,
                    TipoDocumento.class, TipoMedida.class, TipoOjo.class}) {
                sources.addAnnotatedClass(type);
            }
            try (var factory = sources.buildMetadata().buildSessionFactory()) {
                assertEquals(11, factory.getMetamodel().getEntities().size());
                assertEquals(Long.class, factory.getMetamodel().entity(Paciente.class).getIdType().getJavaType());
            }
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    @Test
    void opticalConstraintsAllowSignedPowersAndRejectInvalidMeasurements() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            var eye = new MedidaOjo();
            eye.setMedida(new Medida());
            eye.setTipoOjo(new TipoOjo());
            eye.setEsfera(new BigDecimal("-2.50"));
            eye.setCilindro(new BigDecimal("-1.25"));
            eye.setEje(new BigDecimal("180"));
            eye.setAgudezaVisual(new BigDecimal("0.123456789"));
            assertTrue(validator.validate(eye).isEmpty());
            eye.setEje(new BigDecimal("180.01"));
            eye.setDistanciaPupilarMm(BigDecimal.ZERO);
            eye.setPrisma(new BigDecimal("-1"));
            assertEquals(3, validator.validate(eye).size());
        }
    }

    @Test
    void prescriptionDeliveryCannotPrecedeRegistration() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var receta = new Receta();
            receta.setExamen(new Examen());
            receta.setIdEmpleadoRegistro(1L);
            receta.setFechaRegistro(LocalDateTime.of(2026, 10, 20, 12, 0));
            assertTrue(factory.getValidator().validate(receta).isEmpty());
            receta.setFechaDeEntrega(receta.getFechaRegistro().minusSeconds(1));
            assertFalse(factory.getValidator().validate(receta).isEmpty());
            receta.setFechaDeEntrega(receta.getFechaRegistro());
            assertTrue(factory.getValidator().validate(receta).isEmpty());
        }
    }
}

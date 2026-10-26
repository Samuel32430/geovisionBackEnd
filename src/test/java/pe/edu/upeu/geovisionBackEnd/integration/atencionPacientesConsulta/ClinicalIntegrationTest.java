package pe.edu.upeu.geovisionBackEnd.integration.atencionPacientesConsulta;

import jakarta.persistence.EntityManagerFactory;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.exception.*;
import pe.edu.upeu.geovisionBackEnd.repository.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.service.impl.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta.*;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Isolated test database, never the configured application datasource. */
@SpringJUnitConfig(ClinicalIntegrationTest.Config.class)
class ClinicalIntegrationTest {
    @Configuration
    @EnableTransactionManagement
    @EnableJpaRepositories(basePackageClasses = PacienteRepository.class)
    @Import({SoportePaciente.class, PacienteServiceImpl.class, PersonaServiceImpl.class,
            DocumentoServiceImpl.class, ExamenServiceImpl.class, MedidaServiceImpl.class,
            MedidaOjoServiceImpl.class, RecetaServiceImpl.class, CatalogoPacienteServiceImpl.class})
    static class Config {
        @Bean(destroyMethod = "close") com.zaxxer.hikari.HikariDataSource dataSource() {
            var ds = new com.zaxxer.hikari.HikariDataSource();
            ds.setJdbcUrl("jdbc:h2:mem:patient_integration;MODE=Oracle;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=5000");
            ds.setUsername("sa");
            ds.setPassword("");
            ds.setMinimumIdle(1);
            ds.setMaximumPoolSize(6);
            return ds;
        }
        @Bean LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource ds) {
            var bean = new LocalContainerEntityManagerFactoryBean();
            bean.setDataSource(ds);
            bean.setPackagesToScan("pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta");
            bean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            bean.setJpaPropertyMap(Map.of("hibernate.hbm2ddl.auto", "create-drop",
                    "hibernate.dialect", "org.hibernate.dialect.H2Dialect"));
            return bean;
        }
        @Bean PlatformTransactionManager transactionManager(EntityManagerFactory factory) {
            return new JpaTransactionManager(factory);
        }
        @Bean static MethodValidationPostProcessor validation() {
            var processor = new MethodValidationPostProcessor();
            processor.setProxyTargetClass(true);
            return processor;
        }
        @Bean ActorPacienteActual actor() { return mock(ActorPacienteActual.class); }
        @Bean JdbcTemplate jdbc(DataSource ds) { return new JdbcTemplate(ds); }
    }

    @Autowired PacienteService patients;
    @Autowired PersonaService persons;
    @Autowired DocumentoService documents;
    @Autowired ExamenService exams;
    @Autowired MedidaService measures;
    @Autowired MedidaOjoService eyes;
    @Autowired RecetaService prescriptions;
    @Autowired ActorPacienteActual actor;
    @Autowired TipoDocumentoRepository documentTypes;
    @Autowired TipoMedidaRepository measureTypes;
    @Autowired TipoOjoRepository eyeTypes;
    @Autowired JdbcTemplate jdbc;
    @Autowired PlatformTransactionManager transactions;
    Long documentType, measureType, eyeType;

    @BeforeEach void setup() {
        for (String table : List.of("PACIENTE_LOG", "RECETA", "MEDIDA_OJO", "MEDIDA", "EXAMEN",
                "DOCUMENTO", "PACIENTE", "PERSONA", "TIPO_DOCUMENTO", "TIPO_MEDIDA", "TIPO_OJO")) {
            jdbc.update("delete from " + table);
        }
        reset(actor);
        when(actor.lectura()).thenReturn(new ActorPacienteActual.Actor(10L, 20L));
        when(actor.escritura()).thenReturn(new ActorPacienteActual.Actor(10L, 20L));
        var doc = new TipoDocumento(); doc.setNombre("DNI");
        documentType = documentTypes.saveAndFlush(doc).getIdTipoDocumento();
        var measure = new TipoMedida(); measure.setNombre("LEJOS");
        measureType = measureTypes.saveAndFlush(measure).getIdTipoMedida();
        var eye = new TipoOjo(); eye.setNombre("DERECHO");
        eyeType = eyeTypes.saveAndFlush(eye).getIdTipoOjo();
    }

    PacienteRegistroRequestDTO registration(String number) {
        var person = new PersonaRequestDTO();
        person.setNombres(" Ana ");
        person.setApellidos(" Perez ");
        person.setTelefono("999123456");
        person.setDireccion(" Calle 1 ");
        var request = new PacienteRegistroRequestDTO();
        request.setPersona(person);
        if (number != null) {
            var doc = new DocumentoRegistroRequestDTO();
            doc.setIdTipoDocumento(documentType);
            doc.setNumero(number);
            request.setDocumentos(List.of(doc));
        }
        return request;
    }
    Long patient(String number) { return patients.registrar(registration(number)).getPaciente().getIdPersona(); }
    Long exam(Long patient) {
        var req = new ExamenRequestDTO(); req.setIdPaciente(patient);
        return exams.create(req).getIdExamen();
    }
    Long measure(Long exam) {
        var req = new MedidaRequestDTO(); req.setIdExamen(exam); req.setIdTipoMedida(measureType);
        return measures.create(req).getIdMedida();
    }
    RecetaRequestDTO prescription(Long exam) {
        var req = new RecetaRequestDTO(); req.setIdExamen(exam); return req;
    }
    long count(String table) { return jdbc.queryForObject("select count(*) from " + table, Long.class); }

    @Test void completeClinicalFlowPersistsAndReturnsDetachedDtos() {
        Long patient = patient("12345678"), exam = exam(patient), measure = measure(exam);
        var eye = new MedidaOjoRequestDTO();
        eye.setIdMedida(measure); eye.setIdTipoOjo(eyeType);
        eye.setEsfera(new BigDecimal("-2.50"));
        eye.setAgudezaVisual(new BigDecimal("0.123456789"));
        eye.setEje(new BigDecimal("180"));
        var savedEye = eyes.create(eye);
        assertEquals(0, new BigDecimal("-2.50").compareTo(eyes.read(savedEye.getIdMedidaOjo()).getEsfera()));
        assertEquals(0, new BigDecimal("0.123456789").compareTo(eyes.read(savedEye.getIdMedidaOjo()).getAgudezaVisual()));
        assertEquals("Calle 1", patients.read(patient).getPersona().getDireccion());
        assertEquals(1, exams.read(exam).getMedidas().size());
        assertEquals(1, exams.read(exam).getMedidas().getFirst().getOjos().size());
        var saved = prescriptions.create(prescription(exam));
        assertEquals(saved.getIdReceta(), exams.read(exam).getReceta().getIdReceta());
        var delivery = new RecetaEntregaRequestDTO();
        delivery.setFechaDeEntrega(saved.getFechaRegistro().plusDays(1));
        prescriptions.registrarEntrega(saved.getIdReceta(), delivery);
        assertNotNull(prescriptions.read(saved.getIdReceta()).getFechaDeEntrega());
        assertEquals(6, count("PACIENTE_LOG"));
    }

    @Test void noFilterAndDocumentSearchActuallyWorkOnOracleModeDatabase() {
        patient("12345678");
        assertEquals(1, patients.listar(null, null, 0, 10, null, null).totalElementos());
        assertEquals(1, patients.listar(" ANA ", 1, 0, 10, null, null).totalElementos());
        assertEquals(1, patients.listar("12345678", null, 0, 10, null, null).totalElementos());
        assertEquals(1, patients.listar("999123", null, 0, 10, null, null).totalElementos());
        assertEquals(0, patients.listar("%", null, 0, 10, null, null).totalElementos());
        assertEquals(0, patients.listar(null, 0, 0, 10, null, null).totalElementos());
    }

    @Test void duplicateDocumentRollsBackNewPersonPatientAndAudit() {
        patient("12345678");
        assertThrows(ReglaNegocioException.class, () -> patient("12345678"));
        assertEquals(1, count("PERSONA"));
        assertEquals(1, count("PACIENTE"));
        assertEquals(1, count("DOCUMENTO"));
        assertEquals(1, count("PACIENTE_LOG"));
    }

    @Test void unknownCatalogRollsBackAtomicRegistration() {
        var req = registration("123");
        req.getDocumentos().getFirst().setIdTipoDocumento(Long.MAX_VALUE);
        assertThrows(RecursosNoEncontradosException.class, () -> patients.registrar(req));
        assertEquals(0, count("PERSONA"));
        assertEquals(0, count("PACIENTE"));
        assertEquals(0, count("PACIENTE_LOG"));
    }

    @Test void realServiceProxyValidatesRequestsAndIds() {
        assertThrows(ConstraintViolationException.class, () -> patients.registrar(new PacienteRegistroRequestDTO()));
        assertThrows(ConstraintViolationException.class, () -> patients.read(0L));
        assertEquals(0, count("PERSONA"));
    }

    @Test void documentCannotBeMovedToAnotherPerson() {
        Long first = patient("111"), second = patient("222");
        var original = patients.read(first).getDocumentos().getFirst();
        var req = new DocumentoRequestDTO();
        req.setIdPersona(second); req.setIdTipoDocumento(documentType); req.setNumero("111");
        assertThrows(ReglaNegocioException.class, () -> documents.update(original.getIdDocumento(), req));
        assertEquals(first, documents.read(original.getIdDocumento()).getIdPersona());
    }

    @Test void issuedPrescriptionPreventsRealClinicalEdits() {
        Long exam = exam(patient(null)), measure = measure(exam);
        prescriptions.create(prescription(exam));
        var req = new MedidaRequestDTO();
        req.setIdExamen(exam); req.setIdTipoMedida(measureType); req.setObservaciones("Cambio");
        assertThrows(ReglaNegocioException.class, () -> measures.update(measure, req));
        assertNull(measures.read(measure).getMedida().getObservaciones());
    }

    @Test void onlyOneConcurrentPrescriptionCanCommit() throws Exception {
        Long exam = exam(patient(null));
        var start = new CountDownLatch(1);
        var executor = Executors.newFixedThreadPool(2);
        try {
            Callable<Boolean> issue = () -> {
                start.await();
                try { prescriptions.create(prescription(exam)); return true; }
                catch (ReglaNegocioException ex) { return false; }
            };
            var first = executor.submit(issue);
            var second = executor.submit(issue);
            start.countDown();
            boolean a = first.get(15, TimeUnit.SECONDS), b = second.get(15, TimeUnit.SECONDS);
            assertNotEquals(a, b);
            assertEquals(1, count("RECETA"));
        } finally { executor.shutdownNow(); }
    }

    @Test void temporalResponsesUseOracleDateSecondPrecision() {
        Long exam = exam(patient(null));
        assertEquals(0, exams.read(exam).getExamen().getFechaRegistro().getNano());
        var prescription = prescriptions.create(prescription(exam));
        assertEquals(0, prescription.getFechaRegistro().getNano());
        var delivery = new RecetaEntregaRequestDTO();
        delivery.setFechaDeEntrega(prescription.getFechaRegistro().plusDays(1).withNano(123456789));
        var saved = prescriptions.registrarEntrega(prescription.getIdReceta(), delivery);
        assertEquals(0, saved.getFechaDeEntrega().getNano());
        assertEquals(saved.getFechaDeEntrega(), prescriptions.read(saved.getIdReceta()).getFechaDeEntrega());
    }

    @Test void clinicalEditWaitsForIssuanceAndThenRejectsTheFrozenExam() throws Exception {
        Long exam = exam(patient(null)), measure = measure(exam);
        var issued = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        var editing = new CountDownLatch(1);
        var executor = Executors.newFixedThreadPool(2);
        try {
            var first = executor.submit(() -> new org.springframework.transaction.support.TransactionTemplate(transactions)
                    .execute(status -> {
                        var prescription = prescriptions.create(prescription(exam));
                        issued.countDown();
                        try {
                            if (!release.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("Test timeout");
                        } catch (InterruptedException ex) { Thread.currentThread().interrupt(); throw new IllegalStateException(ex); }
                        return prescription;
                    }));
            assertTrue(issued.await(5, TimeUnit.SECONDS));
            var second = executor.submit(() -> {
                var request = new MedidaRequestDTO();
                request.setIdExamen(exam); request.setIdTipoMedida(measureType); request.setObservaciones("No guardar");
                editing.countDown();
                try { measures.update(measure, request); return false; }
                catch (ReglaNegocioException ex) { return true; }
            });
            assertTrue(editing.await(5, TimeUnit.SECONDS));
            assertThrows(TimeoutException.class, () -> second.get(200, TimeUnit.MILLISECONDS));
            release.countDown();
            assertNotNull(first.get(10, TimeUnit.SECONDS));
            assertTrue(second.get(10, TimeUnit.SECONDS));
            assertNull(measures.read(measure).getMedida().getObservaciones());
        } finally { release.countDown(); executor.shutdownNow(); }
    }

    @Test void onlyOneConcurrentPatientLinkCanCommit() throws Exception {
        Long person = persons.create(registration(null).getPersona()).getIdPersona();
        var start = new CountDownLatch(1);
        var executor = Executors.newFixedThreadPool(2);
        try {
            Callable<Boolean> link = () -> {
                start.await();
                var req = new PacienteRequestDTO(); req.setIdPersona(person);
                try { patients.create(req); return true; }
                catch (ReglaNegocioException ex) { return false; }
            };
            var first = executor.submit(link);
            var second = executor.submit(link);
            start.countDown();
            assertNotEquals(first.get(15, TimeUnit.SECONDS), second.get(15, TimeUnit.SECONDS));
            assertEquals(1, count("PACIENTE"));
            assertEquals(1, count("PACIENTE_LOG"));
        } finally { executor.shutdownNow(); }
    }
}


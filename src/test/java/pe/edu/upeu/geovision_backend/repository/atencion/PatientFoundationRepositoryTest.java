package pe.edu.upeu.geovision_backend.repository.atencion;

import java.util.LinkedHashSet;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.geovision_backend.repository.inventario.MonturaRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.VentaRepository;

import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.entity.security.Empleado;
import pe.edu.upeu.geovision_backend.entity.atencion.Lente;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.MaterialCristal;
import pe.edu.upeu.geovision_backend.entity.atencion.Medida;
import pe.edu.upeu.geovision_backend.entity.atencion.MedidaOjo;
import pe.edu.upeu.geovision_backend.entity.inventario.Montura;
import pe.edu.upeu.geovision_backend.entity.atencion.Paciente;
import pe.edu.upeu.geovision_backend.entity.atencion.PacienteLog;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.entity.inventario.Producto;
import pe.edu.upeu.geovision_backend.entity.atencion.Receta;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.TratamientoCristal;
import pe.edu.upeu.geovision_backend.entity.ventas.Venta;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.TipoComprobanteEnum;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoDocumentoEnum;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoMedidaEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor
class PatientFoundationRepositoryTest {

    // >>>> FIXTURES
    private final EntityManager em;
    private final JdbcTemplate jdbc;
    private final MedidaRepository measures;
    private final PacienteRepository patients;
    private final RecetaRepository prescriptions;
    private final MonturaRepository frames;
    private final VentaRepository sales;
    private final LenteRepository lenses;

    // >>>> HELPERS
    Persona person(String names) {
        var p = new Persona(null, names, "Prueba", "999123456", null, null, null, UUID.randomUUID().toString(), TipoDocumentoEnum.DNI);
        em.persist(p);
        return p;
    }
    Paciente patient() {
        var p = new Paciente(null, person("Paciente"), true, null, null);
        em.persist(p);
        return p;
    }
    Empleado employee() {
        var e = new Empleado(null, person("Profesional"), true, null, null);
        em.persist(e);
        return e;
    }
    Receta prescription(Paciente p, Empleado e) {
        var r = new Receta(null, LocalDateTime.of(2026, 11, 1, 12, 0), 0, p, e);
        em.persist(r);
        return r;
    }

    // >>>> TESTS
    @Test void booleanActivoRoundTripsThroughTheExistingNumericEstadoColumn() {
        var patient = patient();
        em.flush();
        Long id = patient.getId();
        assertEquals(1, jdbc.queryForObject("select estado from PACIENTE where id_persona = ?", Integer.class, id));

        patient.setActivo(false);
        em.flush();
        assertEquals(0, jdbc.queryForObject("select estado from PACIENTE where id_persona = ?", Integer.class, id));
        em.clear();
        assertEquals(Boolean.FALSE, patients.findById(id).orElseThrow().getActivo());

        jdbc.update("update PACIENTE set estado = 1 where id_persona = ?", id);
        em.clear();
        assertEquals(Boolean.TRUE, patients.findById(id).orElseThrow().getActivo());

        var unspecified = new Paciente(null, person("Predeterminado"), null, null, null);
        em.persist(unspecified);
        em.flush();
        assertEquals(Boolean.TRUE, unspecified.getActivo());
        assertEquals(1, jdbc.queryForObject("select estado from PACIENTE where id_persona = ?",
                Integer.class, unspecified.getId()
            )
        );
    }

    @Test void explicitMeasurementCodesPersistAndDefaultToLejos() {
        for (var type : TipoMedidaEnum.values()) {
            var measure = new Medida(null, type, null, null, null, null, null, null, null);
            em.persist(measure);
            em.flush();
            assertEquals(type.getCodigo(), jdbc.queryForObject(
                    "select tipo_medida from MEDIDA where id_medida = ?", Integer.class, measure.getId()
                )
            );
            em.clear();
            assertEquals(type, measures.findById(measure.getId()).orElseThrow().getTipoMedida());
        }
        var defaultMeasure = new Medida(null, TipoMedidaEnum.LEJOS, null, null, null, null, null, null, null);
        defaultMeasure.setTipoMedida(null);
        em.persist(defaultMeasure);
        em.flush();
        assertEquals(1, jdbc.queryForObject("select tipo_medida from MEDIDA where id_medida = ?",
                Integer.class, defaultMeasure.getId()
            )
        );
    }

    @Test void retrievesPatientByDocumentAndSearchFetchesPerson() {
        var patient = patient();
        var id = patient.getId();
        var document = patient.getPersona().getNumeroDocumento();
        em.flush();
        em.clear();
        var found = patients.findByPersonaNumeroDocumento(document).orElseThrow();
        assertEquals(id, found.getId());
        assertTrue(Hibernate.isInitialized(found.getPersona()));
        em.clear();
        var page = patients.buscarPacientes(document, true, PageRequest.of(0, 10));
        assertEquals(1, page.getTotalElements());
        assertTrue(Hibernate.isInitialized(page.getContent().getFirst().getPersona()));
        assertFalse(patients.buscarPacientes(document, false, PageRequest.of(0, 10)).hasContent());
        assertTrue(patients.buscarPacientes("paciente prueba", null, PageRequest.of(0, 10))
                .stream().anyMatch(p -> p.getId().equals(id))
        );
        assertTrue(patients.buscarPacientes("999123456", null, PageRequest.of(0, 10))
                .stream().anyMatch(p -> p.getId().equals(id))
        );
        assertTrue(patients.buscarPacientes(null, true, PageRequest.of(0, 10))
                .stream().anyMatch(p -> p.getId().equals(id))
        );
    }

    @Test void prescriptionHistoryHasStableOrderAndReadyToMapNames() {
        var patient = patient();
        var employee = employee();
        var first = prescription(patient, employee);
        var second = prescription(patient, employee);
        var id = patient.getId();
        Long firstId = first.getId(), secondId = second.getId();
        em.flush();
        em.clear();
        var history = prescriptions.findByPacienteIdOrderByFechaRegistroDescIdDesc(id);
        assertEquals(List.of(secondId, firstId), history.stream().map(Receta::getId).toList());
        assertTrue(Hibernate.isInitialized(history.getFirst().getPaciente().getPersona()));
        assertTrue(Hibernate.isInitialized(history.getFirst().getEmpleado().getPersona()));
        var page = prescriptions.findByPacienteId(id,
                PageRequest.of(0, 1, Sort.by(Sort.Order.desc("fechaRegistro"), Sort.Order.desc("id")))
        );
        assertEquals(2, page.getTotalElements());
        assertEquals(secondId, page.getContent().getFirst().getId());
    }

    @Test void selectingLensMeasurementsCannotReturnAnotherPrescriptionsRows() {
        var patient = patient();
        var employee = employee();
        var recipe = prescription(patient, employee);
        var other = prescription(patient, employee);
        var right = new MedidaOjo(null, "20/20", new BigDecimal("-1.25"), null, 90, null);
        var left = new MedidaOjo(null, "20/25", new BigDecimal("-1.50"), null, 85, null);
        em.persist(right);
        em.persist(left);
        var own = new Medida(null, TipoMedidaEnum.MEDIO, null, null, null, recipe, right, left, null);
        var foreign = new Medida(null, TipoMedidaEnum.LEJOS, null, null, null, other, null, null, null);
        em.persist(own);
        em.persist(foreign);
        Long recipeId = recipe.getId(), ownId = own.getId(), foreignId = foreign.getId();
        em.flush();
        em.clear();
        var selected = measures.findByIdInAndRecetaId(List.of(ownId, foreignId), recipeId);
        assertEquals(List.of(ownId), selected.stream().map(Medida::getId).toList());
        assertTrue(Hibernate.isInitialized(selected.getFirst().getMedidaOjoDerecha()));
        assertTrue(Hibernate.isInitialized(selected.getFirst().getMedidaOjoIzquierda()));
        assertEquals(1, measures.findByRecetaIdAndTipoMedida(recipeId, TipoMedidaEnum.MEDIO).size());
        assertEquals(1, measures.findByRecetaIdOrderByIdAsc(recipeId).size());
    }

    // >>>> HELPERS
    Montura frame(Boolean state, int stock) {
        var product = new Producto(null, UUID.randomUUID().toString(), "Montura", null, null, new BigDecimal("100"), stock, state, null, null);
        em.persist(product);
        var frame = new Montura(null, product, null, null, null);
        em.persist(frame);
        return frame;
    }

    // >>>> TESTS
    @Test void opticalCatalogExcludesInactiveAndOutOfStockFrames() {
        var available = frame(true, 3);
        frame(true, 0);
        frame(false, 3);
        Long id = available.getId();
        em.flush();
        em.clear();
        var page = frames.findByProductoActivoAndProductoStockGreaterThan(
                true, 0, PageRequest.of(0, 10)
        );
        assertEquals(List.of(id), page.getContent().stream().map(Montura::getId).toList());
        assertTrue(Hibernate.isInitialized(page.getContent().getFirst().getProducto()));
    }

    @Test void recipeSaleLookupSupportsTheFutureImmutabilityBoundary() {
        var patient = patient();
        var employee = employee();
        var recipe = prescription(patient, employee);
        assertFalse(sales.existsByRecetaId(recipe.getId()));
        var sale = new Venta(null, null, BigDecimal.ZERO, EstadoVentaEnum.PENDIENTE, null, TipoComprobanteEnum.BOLETA, employee, patient, recipe);
        em.persist(sale);
        em.flush();
        assertTrue(sales.existsByRecetaId(recipe.getId()));
    }

    @Test void lensDetailsSupportOwnFramesAndFetchTreatmentsAndEyes() {
        var recipe = prescription(patient(), employee());
        var material = new MaterialCristal(null, "Resina de prueba");
        var treatment = new TratamientoCristal(null, "Filtro de prueba");
        em.persist(material);
        em.persist(treatment);
        var lens = new Lente(null, BigDecimal.ZERO, recipe, material, null, new LinkedHashSet<>(), new LinkedHashSet<>());
        em.persist(lens);
        var eye = new MedidaOjo(null, "20/20", null, null, null, null);
        em.persist(eye);
        var measure = new Medida(null, TipoMedidaEnum.LEJOS, null, null, null, recipe, eye, null, null);
        em.persist(measure);
        lens.getMedidas().add(measure);
        lens.getTratamientosCristal().add(treatment);
        Long recipeId = recipe.getId(), lensId = lens.getId();
        em.flush();
        em.clear();
        var detail = lenses.findByRecetaIdOrderByIdAsc(recipeId).getFirst();
        assertNull(detail.getMontura());
        assertTrue(Hibernate.isInitialized(detail.getMaterialCristal()));
        assertEquals(1, detail.getMedidas().size());
        assertTrue(Hibernate.isInitialized(detail.getMedidas().iterator().next().getMedidaOjoDerecha()));
        assertTrue(Hibernate.isInitialized(detail.getTratamientosCristal().iterator().next()));
    }

    @Test void manyToManyLinksAreUniqueOrderedAndNeverDeleteSharedClinicalMeasurements() {
        var recipe = prescription(patient(), employee());
        var material = new MaterialCristal(null, "Material de relación compartida");
        em.persist(material);
        var far = new Medida(null, TipoMedidaEnum.LEJOS, null, null, null, recipe, null, null, null);
        var near = new Medida(null, TipoMedidaEnum.CERCA, null, null, null, recipe, null, null, null);
        em.persist(far);
        em.persist(near);
        var first = new Lente(null, BigDecimal.ZERO, recipe, material, null,
                new LinkedHashSet<>(List.of(near, far)), new LinkedHashSet<>()
        );
        var second = new Lente(null, BigDecimal.ZERO, recipe, material, null,
                new LinkedHashSet<>(List.of(far)), new LinkedHashSet<>()
        );
        em.persist(first);
        em.persist(second);
        em.flush();
        Long firstId = first.getId(), secondId = second.getId();
        Long farId = far.getId(), nearId = near.getId();
        assertEquals(3, jdbc.queryForObject("SELECT COUNT(*) FROM LENTE_MEDIDA", Integer.class));
        em.clear();

        var lazy = lenses.findById(firstId).orElseThrow();
        assertFalse(Hibernate.isInitialized(lazy.getMedidas()));
        var detail = lenses.findByIdWithDetails(firstId).orElseThrow();
        assertEquals(List.of(farId, nearId), detail.getMedidas().stream().map(Medida::getId).toList());
        assertFalse(detail.getMedidas().add(em.getReference(Medida.class, farId)));
        em.flush();
        assertEquals(3, jdbc.queryForObject("SELECT COUNT(*) FROM LENTE_MEDIDA", Integer.class));

        detail.getMedidas().clear();
        em.flush();
        assertEquals(1, jdbc.queryForObject("SELECT COUNT(*) FROM LENTE_MEDIDA", Integer.class));
        em.remove(lenses.findById(secondId).orElseThrow());
        em.flush();
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM LENTE_MEDIDA", Integer.class));
        assertTrue(measures.existsById(farId));
        assertTrue(measures.existsById(nearId));
    }

    @Test void treatmentLinksAreUniqueOrderedLazyAndPreserveSharedTreatments() {
        var recipe = prescription(patient(), employee());
        var material = new MaterialCristal(null, "Material de tratamientos compartidos");
        var firstTreatment = new TratamientoCristal(null, "Tratamiento primero");
        var secondTreatment = new TratamientoCristal(null, "Tratamiento segundo");
        em.persist(material);
        em.persist(firstTreatment);
        em.persist(secondTreatment);
        var first = new Lente(null, BigDecimal.ZERO, recipe, material, null,
                new LinkedHashSet<>(),
                new LinkedHashSet<>(List.of(secondTreatment, firstTreatment))
        );
        var second = new Lente(null, BigDecimal.ZERO, recipe, material, null,
                new LinkedHashSet<>(), new LinkedHashSet<>(List.of(firstTreatment))
        );
        em.persist(first);
        em.persist(second);
        em.flush();
        Long firstId = first.getId(), secondId = second.getId();
        Long treatmentId = firstTreatment.getId(), otherId = secondTreatment.getId();
        em.clear();
        assertFalse(Hibernate.isInitialized(lenses.findById(firstId).orElseThrow().getTratamientosCristal()));
        var detail = lenses.findByIdWithDetails(firstId).orElseThrow();
        assertEquals(List.of(treatmentId, otherId), detail.getTratamientosCristal().stream()
                .map(TratamientoCristal::getId).toList()
        );
        assertFalse(detail.getTratamientosCristal().add(em.getReference(TratamientoCristal.class, treatmentId)));
        detail.getTratamientosCristal().clear();
        em.remove(lenses.findById(secondId).orElseThrow());
        em.flush();
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM LENTE_TRATAMIENTO_CRISTAL", Integer.class));
        assertNotNull(em.find(TratamientoCristal.class, treatmentId));
        assertNotNull(em.find(TratamientoCristal.class, otherId));
    }

    @Test void patientCareDefaultDatesHaveSecondPrecision() {
        var patient = patient();
        var recipe = new Receta(null, null, 0, patient, employee());
        var log = new PacienteLog(null, null, null, patient);
        em.persist(recipe);
        em.persist(log);
        em.flush();
        assertEquals(0, patient.getFechaRegistro().getNano());
        assertEquals(0, recipe.getFechaRegistro().getNano());
        assertEquals(0, log.getFechaLog().getNano());
    }
}


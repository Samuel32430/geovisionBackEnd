package pe.edu.upeu.geovision_backend.repository.atencion;

import pe.edu.upeu.geovision_backend.repository.inventario.MonturaRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.VentaRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.entity.security.Empleado;
import pe.edu.upeu.geovision_backend.entity.atencion.Lente;
import pe.edu.upeu.geovision_backend.entity.atencion.LenteMedida;
import pe.edu.upeu.geovision_backend.entity.atencion.LenteTratamientoCristal;
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
import pe.edu.upeu.geovision_backend.enums.EstadoGeneralEnum;
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
class PatientFoundationRepositoryTest {
    @PersistenceContext EntityManager em;
    @Autowired JdbcTemplate jdbc;
    @Autowired MedidaRepository measures;
    @Autowired PacienteRepository patients;
    @Autowired RecetaRepository prescriptions;
    @Autowired MonturaRepository frames;
    @Autowired VentaRepository sales;
    @Autowired LenteRepository lenses;
    @Autowired LenteMedidaRepository lensMeasures;
    @Autowired LenteTratamientoCristalRepository lensTreatments;

    Persona person(String names) {
        var p = Persona.builder().nombres(names).apellidos("Prueba").telefono("999123456")
                .numeroDocumento(UUID.randomUUID().toString()).tipoDocumento(TipoDocumentoEnum.DNI).build();
        em.persist(p);
        return p;
    }
    Paciente patient() {
        var p = Paciente.builder().persona(person("Paciente")).build();
        em.persist(p);
        return p;
    }
    Empleado employee() {
        var e = Empleado.builder().persona(person("Profesional")).build();
        em.persist(e);
        return e;
    }
    Receta prescription(Paciente p, Empleado e) {
        var r = Receta.builder().paciente(p).empleado(e)
                .fechaRegistro(LocalDateTime.of(2026, 11, 1, 12, 0)).build();
        em.persist(r);
        return r;
    }

    @Test void explicitMeasurementCodesPersistAndDefaultToLejos() {
        for (var type : TipoMedidaEnum.values()) {
            var measure = Medida.builder().tipoMedida(type).build();
            em.persist(measure);
            em.flush();
            assertEquals(type.getCodigo(), jdbc.queryForObject(
                    "select tipo_medida from MEDIDA where id_medida = ?", Integer.class, measure.getIdMedida()));
            em.clear();
            assertEquals(type, measures.findById(measure.getIdMedida()).orElseThrow().getTipoMedida());
        }
        var defaultMeasure = new Medida();
        defaultMeasure.setTipoMedida(null);
        em.persist(defaultMeasure);
        em.flush();
        assertEquals(1, jdbc.queryForObject("select tipo_medida from MEDIDA where id_medida = ?",
                Integer.class, defaultMeasure.getIdMedida()));
    }

    @Test void retrievesPatientByDocumentAndSearchFetchesPerson() {
        var patient = patient();
        var id = patient.getIdPersona();
        var document = patient.getPersona().getNumeroDocumento();
        em.flush();
        em.clear();
        var found = patients.findByPersonaNumeroDocumento(document).orElseThrow();
        assertEquals(id, found.getIdPersona());
        assertTrue(Hibernate.isInitialized(found.getPersona()));
        em.clear();
        var page = patients.buscarPacientes(document, EstadoGeneralEnum.ACTIVO, PageRequest.of(0, 10));
        assertEquals(1, page.getTotalElements());
        assertTrue(Hibernate.isInitialized(page.getContent().getFirst().getPersona()));
        assertFalse(patients.buscarPacientes(document, EstadoGeneralEnum.INACTIVO, PageRequest.of(0, 10)).hasContent());
        assertTrue(patients.buscarPacientes("paciente prueba", null, PageRequest.of(0, 10))
                .stream().anyMatch(p -> p.getIdPersona().equals(id)));
        assertTrue(patients.buscarPacientes("999123456", null, PageRequest.of(0, 10))
                .stream().anyMatch(p -> p.getIdPersona().equals(id)));
        assertTrue(patients.buscarPacientes(null, EstadoGeneralEnum.ACTIVO, PageRequest.of(0, 10))
                .stream().anyMatch(p -> p.getIdPersona().equals(id)));
    }

    @Test void prescriptionHistoryHasStableOrderAndReadyToMapNames() {
        var patient = patient();
        var employee = employee();
        var first = prescription(patient, employee);
        var second = prescription(patient, employee);
        var id = patient.getIdPersona();
        Long firstId = first.getIdReceta(), secondId = second.getIdReceta();
        em.flush();
        em.clear();
        var history = prescriptions.findByPacienteIdPersonaOrderByFechaRegistroDescIdRecetaDesc(id);
        assertEquals(List.of(secondId, firstId), history.stream().map(Receta::getIdReceta).toList());
        assertTrue(Hibernate.isInitialized(history.getFirst().getPaciente().getPersona()));
        assertTrue(Hibernate.isInitialized(history.getFirst().getEmpleado().getPersona()));
        var page = prescriptions.findByPacienteIdPersona(id,
                PageRequest.of(0, 1, Sort.by(Sort.Order.desc("fechaRegistro"), Sort.Order.desc("idReceta"))));
        assertEquals(2, page.getTotalElements());
        assertEquals(secondId, page.getContent().getFirst().getIdReceta());
    }

    @Test void selectingLensMeasurementsCannotReturnAnotherPrescriptionsRows() {
        var patient = patient();
        var employee = employee();
        var recipe = prescription(patient, employee);
        var other = prescription(patient, employee);
        var right = MedidaOjo.builder().agudezaVisual("20/20").esfera(new BigDecimal("-1.25")).eje(90).build();
        var left = MedidaOjo.builder().agudezaVisual("20/25").esfera(new BigDecimal("-1.50")).eje(85).build();
        em.persist(right);
        em.persist(left);
        var own = Medida.builder().receta(recipe).tipoMedida(TipoMedidaEnum.MEDIO)
                .medidaOjoDerecha(right).medidaOjoIzquierda(left).build();
        var foreign = Medida.builder().receta(other).build();
        em.persist(own);
        em.persist(foreign);
        Long recipeId = recipe.getIdReceta(), ownId = own.getIdMedida(), foreignId = foreign.getIdMedida();
        em.flush();
        em.clear();
        var selected = measures.findByIdMedidaInAndRecetaIdReceta(List.of(ownId, foreignId), recipeId);
        assertEquals(List.of(ownId), selected.stream().map(Medida::getIdMedida).toList());
        assertTrue(Hibernate.isInitialized(selected.getFirst().getMedidaOjoDerecha()));
        assertTrue(Hibernate.isInitialized(selected.getFirst().getMedidaOjoIzquierda()));
        assertEquals(1, measures.findByRecetaIdRecetaAndTipoMedida(recipeId, TipoMedidaEnum.MEDIO).size());
        assertEquals(1, measures.findByRecetaIdRecetaOrderByIdMedidaAsc(recipeId).size());
    }

    Montura frame(EstadoGeneralEnum state, int stock) {
        var product = Producto.builder().codigo(UUID.randomUUID().toString()).nombre("Montura")
                .precio(new BigDecimal("100")).stock(stock).estado(state).build();
        em.persist(product);
        var frame = Montura.builder().producto(product).build();
        em.persist(frame);
        return frame;
    }

    @Test void opticalCatalogExcludesInactiveAndOutOfStockFrames() {
        var available = frame(EstadoGeneralEnum.ACTIVO, 3);
        frame(EstadoGeneralEnum.ACTIVO, 0);
        frame(EstadoGeneralEnum.INACTIVO, 3);
        Long id = available.getIdProducto();
        em.flush();
        em.clear();
        var page = frames.findByProductoEstadoAndProductoStockGreaterThan(
                EstadoGeneralEnum.ACTIVO, 0, PageRequest.of(0, 10));
        assertEquals(List.of(id), page.getContent().stream().map(Montura::getIdProducto).toList());
        assertTrue(Hibernate.isInitialized(page.getContent().getFirst().getProducto()));
    }

    @Test void recipeSaleLookupSupportsTheFutureImmutabilityBoundary() {
        var patient = patient();
        var employee = employee();
        var recipe = prescription(patient, employee);
        assertFalse(sales.existsByRecetaIdReceta(recipe.getIdReceta()));
        var sale = Venta.builder().paciente(patient).empleado(employee).receta(recipe)
                .total(BigDecimal.ZERO).estado(EstadoVentaEnum.PENDIENTE)
                .tipoComprobante(TipoComprobanteEnum.BOLETA).build();
        em.persist(sale);
        em.flush();
        assertTrue(sales.existsByRecetaIdReceta(recipe.getIdReceta()));
    }

    @Test void lensDetailsSupportOwnFramesAndFetchTreatmentsAndEyes() {
        var recipe = prescription(patient(), employee());
        var material = MaterialCristal.builder().nombre("Resina de prueba").build();
        var treatment = TratamientoCristal.builder().nombre("Filtro de prueba").build();
        em.persist(material);
        em.persist(treatment);
        var lens = Lente.builder().receta(recipe).materialCristal(material).subTotal(BigDecimal.ZERO).build();
        em.persist(lens);
        var eye = MedidaOjo.builder().agudezaVisual("20/20").build();
        em.persist(eye);
        var measure = Medida.builder().receta(recipe).medidaOjoDerecha(eye).build();
        em.persist(measure);
        em.persist(LenteMedida.builder().lente(lens).medida(measure).build());
        em.persist(LenteTratamientoCristal.builder().lente(lens).tratamientoCristal(treatment).build());
        Long recipeId = recipe.getIdReceta(), lensId = lens.getIdLente();
        em.flush();
        em.clear();
        var detail = lenses.findByRecetaIdRecetaOrderByIdLenteAsc(recipeId).getFirst();
        assertNull(detail.getMontura());
        assertTrue(Hibernate.isInitialized(detail.getMaterialCristal()));
        var links = lensMeasures.findByLenteIdLente(lensId);
        assertTrue(Hibernate.isInitialized(links.getFirst().getMedida().getMedidaOjoDerecha()));
        assertTrue(Hibernate.isInitialized(lensTreatments.findByLenteIdLente(lensId)
                .getFirst().getTratamientoCristal()));
    }

    @Test void patientCareDefaultDatesHaveSecondPrecision() {
        var patient = patient();
        var recipe = Receta.builder().paciente(patient).empleado(employee()).build();
        var log = PacienteLog.builder().paciente(patient).build();
        em.persist(recipe);
        em.persist(log);
        em.flush();
        assertEquals(0, patient.getFechaRegistro().getNano());
        assertEquals(0, recipe.getFechaRegistro().getNano());
        assertEquals(0, log.getFechaLog().getNano());
    }
}


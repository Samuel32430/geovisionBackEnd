package pe.edu.upeu.geovision_backend.controller.atencion;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.dto.atencion.LenteRequestDTO;
import pe.edu.upeu.geovision_backend.entity.security.Empleado;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.MaterialCristal;
import pe.edu.upeu.geovision_backend.entity.atencion.Medida;
import pe.edu.upeu.geovision_backend.entity.atencion.MedidaOjo;
import pe.edu.upeu.geovision_backend.entity.inventario.Montura;
import pe.edu.upeu.geovision_backend.entity.atencion.Paciente;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.entity.inventario.Producto;
import pe.edu.upeu.geovision_backend.entity.atencion.Receta;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.TratamientoCristal;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoDocumentoEnum;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoMedidaEnum;
import pe.edu.upeu.geovision_backend.repository.security.EmpleadoRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.LenteMedidaRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.LenteRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.LenteTratamientoCristalRepository;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.MaterialCristalRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.MedidaOjoRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.MedidaRepository;
import pe.edu.upeu.geovision_backend.repository.inventario.MonturaRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.PacienteRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.PersonaRepository;
import pe.edu.upeu.geovision_backend.repository.inventario.ProductoRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.RecetaRepository;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.TratamientoCristalRepository;
import pe.edu.upeu.geovision_backend.service.atencion.RecetaService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.jpa.open-in-view=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser(authorities = "PACIENTES_LEER")
class HistorialClinicoControllerTest {

    @Autowired MockMvc mvc;
    @Autowired RecetaService service;
    @Autowired RecetaRepository prescriptions;
    @Autowired PacienteRepository patients;
    @Autowired EmpleadoRepository employees;
    @Autowired PersonaRepository people;
    @Autowired MedidaRepository measures;
    @Autowired MedidaOjoRepository eyes;
    @Autowired LenteRepository lenses;
    @Autowired LenteMedidaRepository lensMeasures;
    @Autowired LenteTratamientoCristalRepository lensTreatments;
    @Autowired MaterialCristalRepository materials;
    @Autowired TratamientoCristalRepository treatments;
    @Autowired ProductoRepository products;
    @Autowired MonturaRepository frames;
    @Autowired EntityManagerFactory emf;
    @PersistenceContext EntityManager em;
    Paciente patient;
    Empleado employee;
    MaterialCristal material;
    TratamientoCristal treatment;
    Montura frame;

    Persona person(String name) {
        return people.saveAndFlush(Persona.builder().nombres(name).apellidos("Prueba")
                .numeroDocumento(UUID.randomUUID().toString()).tipoDocumento(TipoDocumentoEnum.DNI).build());
    }

    @BeforeEach
    void fixture() {
        patient = patients.saveAndFlush(Paciente.builder().persona(person("Paciente")).build());
        employee = employees.saveAndFlush(Empleado.builder().persona(person("Profesional")).build());
        material = materials.saveAndFlush(MaterialCristal.builder().nombre("Material " + UUID.randomUUID()).build());
        treatment = treatments.saveAndFlush(TratamientoCristal.builder().nombre("Filtro " + UUID.randomUUID()).build());
        Producto product = products.saveAndFlush(Producto.builder().codigo(UUID.randomUUID().toString())
                .nombre("Montura histórica").precio(new BigDecimal("120.00")).stock(4).build());
        frame = frames.saveAndFlush(Montura.builder().producto(product).build());
    }

    Receta recipe(LocalDateTime date, int external) {
        return prescriptions.saveAndFlush(Receta.builder().paciente(patient).empleado(employee)
                .fechaRegistro(date).esExterno(external).build());
    }

    Medida measure(Receta recipe, TipoMedidaEnum type) {
        MedidaOjo right = eyes.saveAndFlush(MedidaOjo.builder().esfera(new BigDecimal("-1.25"))
                .cilindro(new BigDecimal("-0.50")).eje(90).agudezaVisual("20/20").build());
        MedidaOjo left = eyes.saveAndFlush(MedidaOjo.builder().esfera(new BigDecimal("2.50"))
                .adicion(new BigDecimal("1.00")).eje(80).agudezaVisual("20/25").build());
        return measures.saveAndFlush(Medida.builder().receta(recipe).tipoMedida(type)
                .distanciaPupilar(new BigDecimal("62.50")).prisma(new BigDecimal("0.30"))
                .observaciones("Material recomendado: resina").medidaOjoDerecha(right).medidaOjoIzquierda(left).build());
    }

    Long lens(Receta recipe, Long frameId, List<Long> selectedMeasures, boolean withTreatment) {
        return service.configurarLente(recipe.getIdReceta(), LenteRequestDTO.builder().idReceta(recipe.getIdReceta())
                .idMaterialCristal(material.getIdMaterialCristal()).idProducto(frameId).subTotal(new BigDecimal("380.00"))
                .medidasIds(selectedMeasures)
                .tratamientosCristalIds(withTreatment ? List.of(treatment.getIdTratamientoCristal()) : null)
                .build()).idLente();
    }

    @Test
    void detalleIncluyeLentesMedidasPropiasTratamientosYMonturas() throws Exception {
        var recipe = recipe(LocalDateTime.of(2026, 11, 1, 10, 0), 1);
        var far = measure(recipe, TipoMedidaEnum.LEJOS);
        var near = measure(recipe, TipoMedidaEnum.CERCA);
        Long ownLens = lens(recipe, null, null, true);
        Long storeLens = lens(recipe, frame.getIdProducto(), List.of(near.getIdMedida()), false);
        Long id = recipe.getIdReceta(), patientId = patient.getIdPersona(), frameId = frame.getIdProducto();
        String document = patient.getPersona().getNumeroDocumento(), materialName = material.getNombre();
        em.flush();
        em.clear();
        mvc.perform(get("/api/v1/recetas/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.idPaciente").value(patientId))
                .andExpect(jsonPath("$.numeroDocumentoPaciente").value(document))
                .andExpect(jsonPath("$.nombreCompletoEmpleado").value("Profesional Prueba"))
                .andExpect(jsonPath("$.esRecetaExterna").value(true))
                .andExpect(jsonPath("$.medidas.length()").value(2))
                .andExpect(jsonPath("$.medidas[0].idMedida").value(far.getIdMedida()))
                .andExpect(jsonPath("$.medidas[0].ojoDerecho.esfera").value(-1.25))
                .andExpect(jsonPath("$.medidas[0].ojoIzquierdo.agudezaVisual").value("20/25"))
                .andExpect(jsonPath("$.medidas[0].observaciones").value("Material recomendado: resina"))
                .andExpect(jsonPath("$.lentes.length()").value(2))
                .andExpect(jsonPath("$.lentes[0].idLente").value(ownLens))
                .andExpect(jsonPath("$.lentes[0].idProductoMontura").doesNotExist())
                .andExpect(jsonPath("$.lentes[0].nombreMaterialCristal").value(materialName))
                .andExpect(jsonPath("$.lentes[0].tratamientos.length()").value(1))
                .andExpect(jsonPath("$.lentes[0].medidas.length()").value(2))
                .andExpect(jsonPath("$.lentes[1].idLente").value(storeLens))
                .andExpect(jsonPath("$.lentes[1].idProductoMontura").value(frameId))
                .andExpect(jsonPath("$.lentes[1].nombreMontura").value("Montura histórica"))
                .andExpect(jsonPath("$.lentes[1].medidas[0].idMedida").value(near.getIdMedida()))
                .andExpect(jsonPath("$.lentes[1].tratamientos").isEmpty());
    }

    @Test
    void historialEsCronologicoConDesempateEstableYPaginacionSinOmitirRegistros() throws Exception {
        var old = recipe(LocalDateTime.of(2026, 10, 1, 10, 0), 0);
        var sameDateFirst = recipe(LocalDateTime.of(2026, 11, 1, 10, 0), 0);
        var sameDateLast = recipe(LocalDateTime.of(2026, 11, 1, 10, 0), 1);
        var otherPatient = patients.saveAndFlush(Paciente.builder().persona(person("Otro paciente")).build());
        prescriptions.saveAndFlush(Receta.builder().paciente(otherPatient).empleado(employee)
                .fechaRegistro(LocalDateTime.of(2026, 11, 2, 10, 0)).build());
        Long patientId = patient.getIdPersona();
        em.flush();
        em.clear();
        mvc.perform(get("/api/v1/recetas/paciente/{id}/historial", patientId).param("size", "2"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.content[0].idReceta").value(sameDateLast.getIdReceta()))
                .andExpect(jsonPath("$.content[1].idReceta").value(sameDateFirst.getIdReceta()));
        mvc.perform(get("/api/v1/recetas/paciente/{id}/historial", patientId).param("size", "2").param("page", "1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].idReceta").value(old.getIdReceta()));
        assertThat(service.listarHistorialPorPaciente(patientId)).extracting("idReceta")
                .containsExactly(sameDateLast.getIdReceta(), sameDateFirst.getIdReceta(), old.getIdReceta());
    }

    @Test
    void conservaDetalleEnHistorialYSoloIncluyeHijosDeLaRecetaCorrespondiente() throws Exception {
        var old = recipe(LocalDateTime.of(2026, 10, 1, 10, 0), 0);
        var recent = recipe(LocalDateTime.of(2026, 11, 1, 10, 0), 1);
        var oldMeasure = measure(old, TipoMedidaEnum.LEJOS);
        var recentMeasure = measure(recent, TipoMedidaEnum.MEDIO);
        Long oldLens = lens(old, null, null, true);
        Long recentLens = lens(recent, frame.getIdProducto(), null, false);
        Long patientId = patient.getIdPersona();
        em.flush();
        em.clear();
        mvc.perform(get("/api/v1/recetas/paciente/{id}/historial", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].medidas[0].idMedida").value(recentMeasure.getIdMedida()))
                .andExpect(jsonPath("$.content[0].lentes[0].idLente").value(recentLens))
                .andExpect(jsonPath("$.content[0].lentes[0].tratamientos").isEmpty())
                .andExpect(jsonPath("$.content[1].medidas[0].idMedida").value(oldMeasure.getIdMedida()))
                .andExpect(jsonPath("$.content[1].lentes[0].idLente").value(oldLens))
                .andExpect(jsonPath("$.content[1].lentes[0].tratamientos.length()").value(1));
    }

    @Test
    void historialVacioYPaginaFueraDeRangoDevuelvenPaginaVacia() throws Exception {
        mvc.perform(get("/api/v1/recetas/paciente/{id}/historial", patient.getIdPersona()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
        recipe(LocalDateTime.of(2026, 11, 1, 10, 0), 0);
        mvc.perform(get("/api/v1/recetas/paciente/{id}/historial", patient.getIdPersona()).param("page", "3"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void pacientesInactivosConservanAccesoAlHistorial() throws Exception {
        var recipe = recipe(LocalDateTime.of(2026, 11, 1, 10, 0), 0);
        patient.setActivo(false);
        patients.saveAndFlush(patient);
        mvc.perform(get("/api/v1/recetas/paciente/{id}/historial", patient.getIdPersona()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].idReceta").value(recipe.getIdReceta()));
        mvc.perform(get("/api/v1/recetas/{id}", recipe.getIdReceta())).andExpect(status().isOk());
    }

    @Test
    void datosHistoricosOpcionalesNoProvocanErrorNiSeInventanValores() throws Exception {
        var empty = recipe(LocalDateTime.of(2026, 11, 1, 10, 0), 0);
        var legacy = measures.saveAndFlush(Medida.builder().receta(empty).build());
        em.flush();
        em.clear();
        mvc.perform(get("/api/v1/recetas/{id}", empty.getIdReceta())).andExpect(status().isOk())
                .andExpect(jsonPath("$.medidas[0].idMedida").value(legacy.getIdMedida()))
                .andExpect(jsonPath("$.medidas[0].ojoDerecho").doesNotExist())
                .andExpect(jsonPath("$.medidas[0].ojoIzquierdo").doesNotExist())
                .andExpect(jsonPath("$.lentes").isEmpty());
    }

    @Test
    void validaIdentificadoresPaginacionYRecursosInexistentes() throws Exception {
        mvc.perform(get("/api/v1/recetas/99999999")).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/recetas/paciente/99999999/historial")).andExpect(status().isNotFound());
        for (String invalid : List.of("0", "-1", "abc")) {
            mvc.perform(get("/api/v1/recetas/" + invalid)).andExpect(status().isBadRequest());
            mvc.perform(get("/api/v1/recetas/paciente/" + invalid + "/historial")).andExpect(status().isBadRequest());
        }
        for (String[] invalid : new String[][]{{"page", "-1"}, {"size", "0"}, {"size", "101"}, {"page", "abc"}}) {
            mvc.perform(get("/api/v1/recetas/paciente/{id}/historial", patient.getIdPersona())
                    .param(invalid[0], invalid[1])).andExpect(status().isBadRequest());
        }
        mvc.perform(get("/api/v1/recetas/paciente/{id}/historial", patient.getIdPersona())
                .param("page", "2147483647").param("size", "100")).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = {"RECETAS_REGISTRAR", "EXAMENES_REGISTRAR"})
    void permisosDeEscrituraNoOtorganLecturaClinica() throws Exception {
        mvc.perform(get("/api/v1/recetas/1")).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/recetas/paciente/{id}/historial", patient.getIdPersona()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void exigeAutenticacion() throws Exception {
        mvc.perform(get("/api/v1/recetas/1")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/recetas/paciente/{id}/historial", patient.getIdPersona()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void consultasAgrupadasMantienenNumeroDeSentenciasAlCrecerLaPagina() {
        for (int i = 1; i <= 4; i++) {
            var recipe = recipe(LocalDateTime.of(2026, 11, i, 10, 0), i % 2);
            measure(recipe, TipoMedidaEnum.LEJOS);
            lens(recipe, frame.getIdProducto(), null, true);
        }
        Long patientId = patient.getIdPersona();
        em.flush();
        var statistics = emf.unwrap(SessionFactory.class).getStatistics();
        boolean previouslyEnabled = statistics.isStatisticsEnabled();
        statistics.setStatisticsEnabled(true);
        try {
            em.clear();
            statistics.clear();
            assertThat(service.listarRecetasPaginadas(patientId, PageRequest.of(0, 1)).getContent()).hasSize(1);
            long smallPageStatements = statistics.getPrepareStatementCount();
            em.clear();
            statistics.clear();
            var result = service.listarRecetasPaginadas(patientId, PageRequest.of(0, 4));
            long largePageStatements = statistics.getPrepareStatementCount();
            assertThat(result.getContent()).hasSize(4).allSatisfy(dto -> assertThat(dto.lentes()).hasSize(1));
            assertThat(largePageStatements).isLessThanOrEqualTo(smallPageStatements + 1).isLessThanOrEqualTo(9);
        } finally {
            statistics.setStatisticsEnabled(previouslyEnabled);
        }
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void serializaDetalleFueraDeTransaccionSinOpenInView() throws Exception {
        var recipe = recipe(LocalDateTime.of(2026, 11, 1, 10, 0), 0);
        var measure = measure(recipe, TipoMedidaEnum.LEJOS);
        Long lensId = lens(recipe, frame.getIdProducto(), null, true);
        try {
            mvc.perform(get("/api/v1/recetas/{id}", recipe.getIdReceta())).andExpect(status().isOk())
                    .andExpect(jsonPath("$.lentes[0].idLente").value(lensId))
                    .andExpect(jsonPath("$.lentes[0].medidas[0].ojoDerecho.esfera").value(-1.25));
            mvc.perform(get("/api/v1/recetas/paciente/{id}/historial", patient.getIdPersona()))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].lentes[0].idLente").value(lensId));
        } finally {
            lensTreatments.deleteAll(lensTreatments.findByLenteIdLente(lensId));
            lensMeasures.deleteAll(lensMeasures.findByLenteIdLente(lensId));
            lenses.deleteById(lensId);
            measures.deleteById(measure.getIdMedida());
            eyes.deleteAllById(List.of(measure.getMedidaOjoDerecha().getIdMedidaOjo(),
                    measure.getMedidaOjoIzquierda().getIdMedidaOjo()));
            prescriptions.deleteById(recipe.getIdReceta());
            patients.deleteById(patient.getIdPersona());
            employees.deleteById(employee.getIdPersona());
            people.deleteAllById(List.of(patient.getIdPersona(), employee.getIdPersona()));
            frames.deleteById(frame.getIdProducto());
            products.deleteById(frame.getIdProducto());
            materials.deleteById(material.getIdMaterialCristal());
            treatments.deleteById(treatment.getIdTratamientoCristal());
        }
    }
}

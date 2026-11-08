package pe.edu.upeu.geovision_backend.controller.atencion;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.dto.atencion.MedidaOjoDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.MedidaRequestDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.RecetaRequestDTO;
import pe.edu.upeu.geovision_backend.entity.security.Empleado;
import pe.edu.upeu.geovision_backend.entity.atencion.MedidaOjo;
import pe.edu.upeu.geovision_backend.entity.atencion.Paciente;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.entity.security.Usuario;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoDocumentoEnum;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoMedidaEnum;
import pe.edu.upeu.geovision_backend.repository.security.EmpleadoRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.MedidaOjoRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.MedidaRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.PacienteRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.PersonaRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.RecetaRepository;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioRepository;
import pe.edu.upeu.geovision_backend.service.atencion.RecetaService;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser(username = "optometra.prueba", authorities = {"EXAMENES_REGISTRAR", "RECETAS_REGISTRAR"})
class RecetaControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired RecetaService service;
    @Autowired RecetaRepository prescriptions;
    @Autowired MedidaRepository measures;
    @MockitoSpyBean MedidaOjoRepository eyes;
    @Autowired PacienteRepository patients;
    @Autowired PersonaRepository people;
    @Autowired UsuarioRepository users;
    @Autowired EmpleadoRepository employees;
    @PersistenceContext EntityManager em;
    Usuario actor;
    Empleado employee;
    Paciente patient;

    Persona person(String name) {
        return people.saveAndFlush(new Persona(null, name, "Prueba", null, null, null, null, UUID.randomUUID().toString(), TipoDocumentoEnum.DNI));
    }

    @BeforeEach
    void fixture() {
        Persona professional = person("Profesional");
        actor = users.saveAndFlush(new Usuario(null, "optometra.prueba", "hash-de-prueba", UUID.randomUUID() + "@test.invalid", true, professional));
        employee = employees.saveAndFlush(new Empleado(null, professional, true, null, actor));
        patient = patients.saveAndFlush(new Paciente(null, person("Paciente"), true, null, null));
    }

    MedidaRequestDTO measure(TipoMedidaEnum type) {
        return new MedidaRequestDTO(type, new BigDecimal("62.50"), new BigDecimal("0.30"), "Material recomendado: policarbonato", null, new MedidaOjoDTO(null, "20/20", new BigDecimal("-1.25"), new BigDecimal("-0.50"), 0, new BigDecimal("1.00")), new MedidaOjoDTO(null, "20/25", new BigDecimal("2.50"), BigDecimal.ZERO, 180, null), null);
    }

    RecetaRequestDTO request() {
        return new RecetaRequestDTO(patient.getId(), employee.getId(), 0, null, new ArrayList<>(List.of(measure(TipoMedidaEnum.LEJOS))));
    }

    void expectPost(String endpoint, RecetaRequestDTO request, int status) throws Exception {
        mvc.perform(post("/api/v1/recetas/" + endpoint).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(request))).andExpect(status().is(status));
    }

    @Test
    void examenPersisteTresTiposAmbosOjosYRespuestaCompleta() throws Exception {
        long beforeEyes = eyes.count();
        var request = request();
        request.medidas().add(measure(TipoMedidaEnum.MEDIO));
        request.medidas().add(measure(TipoMedidaEnum.CERCA));
        String response = mvc.perform(post("/api/v1/recetas/examen-visual").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.esExterno").value(0))
                .andExpect(jsonPath("$.esRecetaExterna").value(false))
                .andExpect(jsonPath("$.nombreCompletoPaciente").value("Paciente Prueba"))
                .andExpect(jsonPath("$.nombreCompletoEmpleado").value("Profesional Prueba"))
                .andExpect(jsonPath("$.idEmpleado").value(employee.getId()))
                .andExpect(jsonPath("$.medidas.length()").value(3))
                .andExpect(jsonPath("$.medidas[0].ojoDerecho.agudezaVisual").value("20/20"))
                .andExpect(jsonPath("$.medidas[0].ojoIzquierdo.esfera").value(2.50))
                .andExpect(jsonPath("$.medidas[0].observaciones").value("Material recomendado: policarbonato"))
                .andExpect(jsonPath("$.lentes").isEmpty())
                .andReturn().getResponse().getContentAsString();
        long id = json.readTree(response).get("id").asLong();
        var persisted = measures.findByRecetaIdOrderByIdAsc(id);
        assertThat(persisted).extracting("tipoMedida")
                .containsExactly(TipoMedidaEnum.LEJOS, TipoMedidaEnum.MEDIO, TipoMedidaEnum.CERCA);
        assertThat(eyes.count()).isEqualTo(beforeEyes + 6);
        assertThat(persisted).allSatisfy(measure -> {
            assertThat(measure.getMedidaOjoDerecha().getId())
                    .isNotEqualTo(measure.getMedidaOjoIzquierda().getId());
            assertThat(measure.getReceta().getPaciente().getId()).isEqualTo(patient.getId());
        });
    }

    @Test
    void externaSeMarcaPorEndpointYConservaFechaSinFracciones() throws Exception {
        var request = request();
        request = new RecetaRequestDTO(request.idPaciente(), request.idEmpleado(), request.esExterno(), LocalDateTime.of(2026, 10, 30, 10, 15, 0, 123456789), request.medidas());
        mvc.perform(post("/api/v1/recetas/externa").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(request)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.esExterno").value(1))
                .andExpect(jsonPath("$.esRecetaExterna").value(true))
                .andExpect(jsonPath("$.fechaRegistro").value("2026-10-30T10:15:00"));
    }

    @Test
    void dioptriaInvalidaEnCualquierCampoYOjoNoEscribeDatos() throws Exception {
        long count = prescriptions.count();
        for (int field = 0; field < 6; field++) {
            var request = request();
            var eye = field < 3 ? request.medidas().getFirst().ojoDerecho()
                    : request.medidas().getFirst().ojoIzquierdo();
            switch (field % 3) {
                case 0 -> eye = new MedidaOjoDTO(eye.id(), eye.agudezaVisual(), new BigDecimal("1.35"), eye.cilindro(), eye.eje(), eye.adicion());
                case 1 -> eye = new MedidaOjoDTO(eye.id(), eye.agudezaVisual(), eye.esfera(), new BigDecimal("-0.30"), eye.eje(), eye.adicion());
                case 2 -> eye = new MedidaOjoDTO(eye.id(), eye.agudezaVisual(), eye.esfera(), eye.cilindro(), eye.eje(), new BigDecimal("1.01"));
            }
            var measure = request.medidas().getFirst();
            request.medidas().set(0, field < 3 ? new MedidaRequestDTO(measure.tipoMedida(), measure.distanciaPupilar(), measure.prisma(), measure.observaciones(), measure.idReceta(), eye, measure.ojoIzquierdo(), measure.idEnvioLaboratorio())
                    : new MedidaRequestDTO(measure.tipoMedida(), measure.distanciaPupilar(), measure.prisma(), measure.observaciones(), measure.idReceta(), measure.ojoDerecho(), eye, measure.idEnvioLaboratorio()));
            expectPost("examen-visual", request, 409);
        }
        assertThat(prescriptions.count()).isEqualTo(count);
    }

    @Test
    void validaDtoAnidadoOjosDpEjePrecisionYEnum() throws Exception {
        var request = request();
        request.medidas().set(0, new MedidaRequestDTO(request.medidas().getFirst().tipoMedida(), request.medidas().getFirst().distanciaPupilar(), request.medidas().getFirst().prisma(), request.medidas().getFirst().observaciones(), request.medidas().getFirst().idReceta(), null, request.medidas().getFirst().ojoIzquierdo(), request.medidas().getFirst().idEnvioLaboratorio()));
        expectPost("examen-visual", request, 400);
        request = request();
        request.medidas().set(0, new MedidaRequestDTO(request.medidas().getFirst().tipoMedida(), BigDecimal.ZERO, request.medidas().getFirst().prisma(), request.medidas().getFirst().observaciones(), request.medidas().getFirst().idReceta(), request.medidas().getFirst().ojoDerecho(), request.medidas().getFirst().ojoIzquierdo(), request.medidas().getFirst().idEnvioLaboratorio()));
        expectPost("examen-visual", request, 400);
        request = request();
        var measure = request.medidas().getFirst();
        request.medidas().set(0, new MedidaRequestDTO(measure.tipoMedida(), measure.distanciaPupilar(), measure.prisma(), measure.observaciones(), measure.idReceta(), measure.ojoDerecho(), new MedidaOjoDTO(measure.ojoIzquierdo().id(), measure.ojoIzquierdo().agudezaVisual(), measure.ojoIzquierdo().esfera(), measure.ojoIzquierdo().cilindro(), 181, measure.ojoIzquierdo().adicion()), measure.idEnvioLaboratorio()));
        expectPost("examen-visual", request, 400);
        request = request();
        measure = request.medidas().getFirst();
        request.medidas().set(0, new MedidaRequestDTO(measure.tipoMedida(), measure.distanciaPupilar(), measure.prisma(), measure.observaciones(), measure.idReceta(), new MedidaOjoDTO(measure.ojoDerecho().id(), measure.ojoDerecho().agudezaVisual(), new BigDecimal("1000.00"), measure.ojoDerecho().cilindro(), measure.ojoDerecho().eje(), measure.ojoDerecho().adicion()), measure.ojoIzquierdo(), measure.idEnvioLaboratorio()));
        expectPost("examen-visual", request, 400);
        request = request();
        request.medidas().add(null);
        expectPost("examen-visual", request, 400);
        mvc.perform(post("/api/v1/recetas/examen-visual").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(request()).replace("LEJOS", "INVALIDO")))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/recetas/examen-visual").contentType(MediaType.APPLICATION_JSON)
                .content("{")).andExpect(status().isBadRequest());
    }

    @Test
    void rechazaPacienteInactivoYNoEncontrado() throws Exception {
        patient.setActivo(false);
        patients.saveAndFlush(patient);
        expectPost("examen-visual", request(), 409);
        expectPost("externa", request(), 409);
        var missing = request();
        missing = new RecetaRequestDTO(99999999L, missing.idEmpleado(), missing.esExterno(), missing.fechaRegistro(), missing.medidas());
        expectPost("examen-visual", missing, 404);
    }

    @Test
    void noPermiteSuplantarEmpleadoNiUsarEmpleadoOUsuarioInactivo() throws Exception {
        var request = request();
        request = new RecetaRequestDTO(request.idPaciente(), employee.getId() + 99999, request.esExterno(), request.fechaRegistro(), request.medidas());
        expectPost("examen-visual", request, 403);
        employee.setActivo(false);
        employees.saveAndFlush(employee);
        expectPost("examen-visual", request(), 403);
        employee.setActivo(true);
        employees.saveAndFlush(employee);
        actor.setActivo(false);
        users.saveAndFlush(actor);
        expectPost("externa", request(), 403);
    }

    @Test
    void usuarioSinEmpleadoVinculadoNoPuedeRegistrar() throws Exception {
        employee.setUsuario(null);
        employees.saveAndFlush(employee);
        expectPost("examen-visual", request(), 403);
    }

    @Test
    void rechazaIdsDeMedidasExistentesYEnviosYTipoExternoEnExamen() throws Exception {
        var request = request();
        request = new RecetaRequestDTO(request.idPaciente(), request.idEmpleado(), 1, request.fechaRegistro(), request.medidas());
        expectPost("examen-visual", request, 409);
        for (int field = 0; field < 4; field++) {
            request = request();
            var measure = request.medidas().getFirst();
            switch (field) {
                case 0 -> measure = new MedidaRequestDTO(measure.tipoMedida(), measure.distanciaPupilar(), measure.prisma(), measure.observaciones(), 1L, measure.ojoDerecho(), measure.ojoIzquierdo(), measure.idEnvioLaboratorio());
                case 1 -> measure = new MedidaRequestDTO(measure.tipoMedida(), measure.distanciaPupilar(), measure.prisma(), measure.observaciones(), measure.idReceta(), measure.ojoDerecho(), measure.ojoIzquierdo(), 1L);
                case 2 -> measure = new MedidaRequestDTO(measure.tipoMedida(), measure.distanciaPupilar(), measure.prisma(), measure.observaciones(), measure.idReceta(), new MedidaOjoDTO(1L, measure.ojoDerecho().agudezaVisual(), measure.ojoDerecho().esfera(), measure.ojoDerecho().cilindro(), measure.ojoDerecho().eje(), measure.ojoDerecho().adicion()), measure.ojoIzquierdo(), measure.idEnvioLaboratorio());
                case 3 -> measure = new MedidaRequestDTO(measure.tipoMedida(), measure.distanciaPupilar(), measure.prisma(), measure.observaciones(), measure.idReceta(), measure.ojoDerecho(), new MedidaOjoDTO(1L, measure.ojoIzquierdo().agudezaVisual(), measure.ojoIzquierdo().esfera(), measure.ojoIzquierdo().cilindro(), measure.ojoIzquierdo().eje(), measure.ojoIzquierdo().adicion()), measure.idEnvioLaboratorio());
            }
            request.medidas().set(0, measure);
            expectPost("externa", request, 409);
        }
    }

    @Test
    @WithMockUser(authorities = "PACIENTES_REGISTRAR")
    void deniegaSinPermisosClinicos() throws Exception {
        expectPost("examen-visual", request(), 403);
        expectPost("externa", request(), 403);
    }

    @Test
    @WithMockUser(username = "optometra.prueba", authorities = "EXAMENES_REGISTRAR")
    void separaPermisosDeExamenYRecetaExterna() throws Exception {
        expectPost("examen-visual", request(), 201);
        expectPost("externa", request(), 403);
    }

    @Test
    @WithAnonymousUser
    void exigeAutenticacion() throws Exception {
        expectPost("examen-visual", request(), 401);
        expectPost("externa", request(), 401);
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 4})
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void falloEnOjoRevierteCabeceraMedidasYOjos(int failingWrite) {
        long headers = prescriptions.count(), measurements = measures.count(), eyeCount = eyes.count();
        AtomicInteger writes = new AtomicInteger();
        doAnswer(invocation -> {
            if (writes.incrementAndGet() == failingWrite) {
                throw new DataAccessResourceFailureException("Fallo simulado al guardar un ojo");
            }
            // Inserciones SQL reales dentro de la transacción del servicio antes del fallo simulado.
            MedidaOjo eye = invocation.getArgument(0);
            em.persist(eye);
            em.flush();
            return eye;
        }).when(eyes).saveAndFlush(any(MedidaOjo.class));
        try {
            var request = request();
            request.medidas().add(measure(TipoMedidaEnum.CERCA));
            assertThatThrownBy(() -> service.registrarExamenVisual(request, actor.getNombre()))
                    .isInstanceOf(DataAccessResourceFailureException.class);
            assertThat(writes.get()).isEqualTo(failingWrite);
            assertThat(prescriptions.count()).isEqualTo(headers);
            assertThat(measures.count()).isEqualTo(measurements);
            assertThat(eyes.count()).isEqualTo(eyeCount);
        } finally {
            patients.deleteById(patient.getId());
            people.deleteById(patient.getId());
            employees.deleteById(employee.getId());
            users.deleteById(actor.getId());
            people.deleteById(employee.getId());
        }
    }
}

package pe.edu.upeu.geovision_backend.controller.atencion;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.test.context.transaction.AfterTransaction;
import pe.edu.upeu.geovision_backend.entity.security.Empleado;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.MaterialCristal;
import pe.edu.upeu.geovision_backend.entity.atencion.Paciente;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.entity.security.Rol;
import pe.edu.upeu.geovision_backend.entity.security.RolPermiso;
import pe.edu.upeu.geovision_backend.entity.security.Usuario;
import pe.edu.upeu.geovision_backend.entity.security.UsuarioRol;
import pe.edu.upeu.geovision_backend.entity.ventas.Venta;
import pe.edu.upeu.geovision_backend.dto.atencion.PacienteRequestDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.PersonaRequestDTO;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.TipoComprobanteEnum;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoDocumentoEnum;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.repository.security.EmpleadoRepository;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.MaterialCristalRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.PacienteLogRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.PacienteRepository;
import pe.edu.upeu.geovision_backend.repository.security.PermisoRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.PersonaRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.RecetaRepository;
import pe.edu.upeu.geovision_backend.repository.security.RolPermisoRepository;
import pe.edu.upeu.geovision_backend.repository.security.RolRepository;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioLogRepository;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioRepository;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioRolRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.VentaRepository;
import pe.edu.upeu.geovision_backend.security.service.JwtService;
import pe.edu.upeu.geovision_backend.service.atencion.PacienteService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.jpa.open-in-view=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor
class AtencionPacienteIntegrationTest {

    // >>>> FIXTURES
    private final MockMvc mvc;
    private final ObjectMapper json;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final PacienteService patientService;
    private final PersonaRepository people;
    private final UsuarioRepository users;
    private final EmpleadoRepository employees;
    private final PacienteRepository patients;
    private final PacienteLogRepository patientLogs;
    private final RolRepository roles;
    private final PermisoRepository permissions;
    private final RolPermisoRepository rolePermissions;
    private final UsuarioRolRepository userRoles;
    private final MaterialCristalRepository materials;
    private final VentaRepository sales;
    private final RecetaRepository prescriptions;
    private final UsuarioLogRepository userLogs;
    private final PlatformTransactionManager transactions;
    @Value("${app.jwt.secret}") private final String testSecret;
    Usuario actor;
    Empleado employee;
    Rol role;

    // >>>> HELPERS
    Persona person(String name) {
        return people.saveAndFlush(new Persona(null, name, "Integración", null, null, null, null, UUID.randomUUID().toString(), TipoDocumentoEnum.DNI));
    }

    // >>>> SETUP
    @BeforeEach
    void setup() {
        // REQUIRES_NEW login auditing requires a committed account fixture.
        var fixtureTransaction = new TransactionTemplate(transactions);
        fixtureTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        fixtureTransaction.executeWithoutResult(status -> createFixture());
    }

    // >>>> HELPERS
    void createFixture() {
        Persona professional = person("Profesional");
        actor = users.saveAndFlush(new Usuario(null, "integracion-" + UUID.randomUUID(), encoder.encode("PruebaSegura123!"), UUID.randomUUID() + "@test.invalid", true, professional));
        employee = employees.saveAndFlush(new Empleado(null, professional, true, null, actor));
        role = roles.saveAndFlush(new Rol(null, "PRUEBA-" + UUID.randomUUID()));
        for (String permission : List.of("PACIENTES_LEER", "PACIENTES_REGISTRAR", "PACIENTES_GESTIONAR",
                "EXAMENES_REGISTRAR", "RECETAS_REGISTRAR"
            )
        ) {
            rolePermissions.saveAndFlush(new RolPermiso(null, role, permissions.findByNombreIgnoreCase(permission).orElseThrow()));
        }
        userRoles.saveAndFlush(new UsuarioRol(null, actor, role));
    }

    // >>>> CLEANUP
    @AfterTransaction
    void cleanup() {
        new TransactionTemplate(transactions).executeWithoutResult(status -> {
            userLogs.deleteAll(userLogs.findByUsuarioIdOrderByFechaLogDesc(actor.getId()));
            userRoles.deleteByUsuarioId(actor.getId());
            rolePermissions.deleteByRolId(role.getId());
            employees.deleteById(employee.getId());
            users.deleteById(actor.getId());
            people.deleteById(employee.getId());
            roles.deleteById(role.getId());
        }
        );
    }

    // >>>> HELPERS
    String login() throws Exception {
        String response = mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("username", actor.getNombre(), "password", "PruebaSegura123!")))
        )
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return "Bearer " + json.readTree(response).get("token").asText();
    }

    String exam(long patientId) {
        return """
                {"idPaciente":%d,"idEmpleado":%d,"esExterno":0,"medidas":[{
                  "tipoMedida":"LEJOS","distanciaPupilar":62.50,
                  "observaciones":"Material recomendado: policarbonato",
                  "ojoDerecho":{"esfera":-1.25,"cilindro":-0.50,"eje":90,"agudezaVisual":"20/20"},
                  "ojoIzquierdo":{"esfera":2.50,"eje":80,"agudezaVisual":"20/25"}}]}
                """.formatted(patientId, employee.getId());
    }

    // >>>> TESTS
    @Test
    void flujoCompletoConLoginJwtAuditoriaRecetaLenteHistorialPdfYBloqueoPorVenta() throws Exception {
        String bearer = login();
        String document = UUID.randomUUID().toString();
        String patientJson = json.writeValueAsString(Map.of("persona", Map.of(
                "nombres", "Paciente", "apellidos", "Integración", "tipoDocumento", "DNI", "numeroDocumento", document
                )
            )
        );
        JsonNode patient = json.readTree(mvc.perform(post("/api/v1/pacientes").header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON).content(patientJson)
            )
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()
        );
        long patientId = patient.get("id").asLong();
        mvc.perform(post("/api/v1/pacientes").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content(patientJson)
        ).andExpect(status().isConflict());
        JsonNode recipe = json.readTree(mvc.perform(post("/api/v1/recetas/examen-visual").header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON).content(exam(patientId))
            )
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()
        );
        long recipeId = recipe.get("id").asLong();
        var material = materials.saveAndFlush(new MaterialCristal(null, "Material " + UUID.randomUUID()));
        mvc.perform(get("/api/v1/catalogos-opticos/materiales-cristal").header("Authorization", bearer))
                .andExpect(status().isOk());
        String lens = json.writeValueAsString(Map.of("idReceta", recipeId,
                "idMaterialCristal", material.getId(), "subTotal", new BigDecimal("380.00")
            )
        );
        mvc.perform(post("/api/v1/recetas/{id}/lentes", recipeId).header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content(lens)
        ).andExpect(status().isCreated());
        mvc.perform(get("/api/v1/recetas/{id}", recipeId).header("Authorization", bearer))
                .andExpect(status().isOk()).andExpect(jsonPath("$.lentes.length()").value(1));
        byte[] pdf = mvc.perform(get("/api/v1/recetas/{id}/pdf", recipeId).header("Authorization", bearer)
                        .accept(MediaType.APPLICATION_PDF)
        ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();
        try (var parsed = Loader.loadPDF(pdf)) {
            assertThat(new PDFTextStripper().getText(parsed)).contains("Paciente Integración", "-1.25", "Material");
        }
        mvc.perform(patch("/api/v1/pacientes/{id}/estado", patientId).header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":0}")
        ).andExpect(status().isOk());
        mvc.perform(post("/api/v1/recetas/examen-visual").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content(exam(patientId))
        ).andExpect(status().isConflict());
        mvc.perform(get("/api/v1/recetas/paciente/{id}/historial", patientId).header("Authorization", bearer))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(patch("/api/v1/pacientes/{id}/estado", patientId).header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":1}")
        ).andExpect(status().isOk());
        mvc.perform(post("/api/v1/recetas/externa").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content(exam(patientId))
        )
                .andExpect(status().isCreated()).andExpect(jsonPath("$.esExterno").value(1));
        sales.saveAndFlush(new Venta(null, null, new BigDecimal("380.00"), EstadoVentaEnum.PENDIENTE, null, TipoComprobanteEnum.BOLETA, employee, patients.findById(patientId).orElseThrow(), prescriptions.findById(recipeId).orElseThrow()));
        mvc.perform(post("/api/v1/recetas/{id}/lentes", recipeId).header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content(lens)
        ).andExpect(status().isConflict());
        assertThat(patientLogs.findByPacienteIdOrderByFechaLogDesc(patientId)).hasSize(3)
                .allSatisfy(log -> assertThat(log.getUsuario().getId()).isEqualTo(actor.getId()));
    }

    @Test
    void tokenDeRecuperacionNoSirveComoTokenDeAcceso() throws Exception {
        String recovery = jwt.generarTokenRecuperacion(actor.getCorreo(), 5);
        assertThat(jwt.validarTokenRecuperacion(recovery)).isTrue();
        assertThat(jwt.extraerEmailRecuperacion(recovery)).isEqualTo(actor.getCorreo());
        mvc.perform(get("/api/v1/pacientes").header("Authorization", "Bearer " + recovery))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenValidoNoConservaPermisosRevocadosNiHabilitaUsuarioInactivo() throws Exception {
        String bearer = login();
        var read = permissions.findByNombreIgnoreCase("PACIENTES_LEER").orElseThrow();
        var assignment = rolePermissions.findByRolIdAndPermisoId(role.getId(), read.getId()).orElseThrow();
        rolePermissions.delete(assignment);
        rolePermissions.flush();
        mvc.perform(get("/api/v1/pacientes").header("Authorization", bearer)).andExpect(status().isForbidden());
        actor.setActivo(false);
        users.saveAndFlush(actor);
        mvc.perform(get("/api/v1/pacientes").header("Authorization", bearer)).andExpect(status().isUnauthorized());
    }

    @Test
    void tokenDeEmpleadoInactivoExpiradoOMalformadoSeRechaza() throws Exception {
        String bearer = login();
        employee.setActivo(false);
        employees.saveAndFlush(employee);
        mvc.perform(get("/api/v1/pacientes").header("Authorization", bearer)).andExpect(status().isUnauthorized());
        var expired = new JwtService(testSecret, -1).generarToken(User.withUsername(actor.getNombre())
                .password("unused").authorities("PACIENTES_LEER").build()
        );
        mvc.perform(get("/api/v1/pacientes").header("Authorization", "Bearer " + expired)).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/pacientes").header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void idsYEjesFraccionariosNoSeRedondeanEnSilencio() throws Exception {
        String bearer = login();
        var patient = patients.saveAndFlush(new Paciente(null, person("Paciente"), true, null, null));
        for (String invalid : List.of(exam(patient.getId()).replace("\"idPaciente\":" + patient.getId(),
                "\"idPaciente\":" + patient.getId() + ".5"
                ),
                exam(patient.getId()).replace("\"eje\":90", "\"eje\":90.5")
            )
        ) {
            mvc.perform(post("/api/v1/recetas/examen-visual").header("Authorization", bearer)
                    .contentType(MediaType.APPLICATION_JSON).content(invalid)
            ).andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("El cuerpo JSON tiene un formato inválido o contiene valores incompatibles"));
        }
        mvc.perform(patch("/api/v1/pacientes/{id}/estado", patient.getId()).header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":0.5}")
        ).andExpect(status().isBadRequest());
    }

    @Test
    void tipoMedidaNumericoNoSeInterpretaComoOrdinalJava() throws Exception {
        String bearer = login();
        var patient = patients.saveAndFlush(new Paciente(null, person("Paciente"), true, null, null));
        for (String numeric : List.of("1", "\"1\"")) {
            mvc.perform(post("/api/v1/recetas/examen-visual").header("Authorization", bearer)
                    .contentType(MediaType.APPLICATION_JSON).content(exam(patient.getId())
                            .replace("\"tipoMedida\":\"LEJOS\"", "\"tipoMedida\":" + numeric)
                    )
            )
                    .andExpect(status().isBadRequest());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void registrosConcurrentesNoDuplicanPersonaPacienteNiAuditoria(boolean existingPerson) throws Exception {
        String document = UUID.randomUUID().toString();
        var independent = new TransactionTemplate(transactions);
        independent.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        var request = new PacienteRequestDTO(null, null, 1, null);
        if (existingPerson) {
            Long id = independent.execute(status -> people.saveAndFlush(new Persona(null, "Paciente concurrente", "Prueba", null, null, null, null, document, TipoDocumentoEnum.DNI)).getId());
            request = new PacienteRequestDTO(id, request.persona(), request.estado(), request.idUsuario());
        } else {
            request = new PacienteRequestDTO(request.id(), new PersonaRequestDTO("Paciente concurrente", "Prueba", null, null, null, null, TipoDocumentoEnum.DNI, document), request.estado(), request.idUsuario());
        }
        CyclicBarrier start = new CyclicBarrier(2);
        var registration = request;
        Callable<Boolean> register = () -> {
            start.await(10, TimeUnit.SECONDS);
            try {
                patientService.registrarPaciente(registration, actor.getNombre());
                return true;
            } catch (ReglaNegocioException ex) {
                return false;
            }
        };
        try (var executor = Executors.newFixedThreadPool(2)) {
            var first = executor.submit(register);
            var second = executor.submit(register);
            assertThat(List.of(first.get(20, TimeUnit.SECONDS), second.get(20, TimeUnit.SECONDS)))
                    .containsExactlyInAnyOrder(true, false);
            var created = patients.findByPersonaNumeroDocumento(document).orElseThrow();
            assertThat(patientLogs.findByPacienteIdOrderByFechaLogDesc(created.getId())).hasSize(1);
        } finally {
            independent.executeWithoutResult(status -> people.findByNumeroDocumento(document).ifPresent(person -> {
                patientLogs.deleteAll(patientLogs.findByPacienteIdOrderByFechaLogDesc(person.getId()));
                if (patients.existsById(person.getId())) patients.deleteById(person.getId());
                people.deleteById(person.getId());
            }
                )
            );
        }
    }
}

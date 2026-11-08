package pe.edu.upeu.geovision_backend.controller.atencion;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
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
import pe.edu.upeu.geovision_backend.enums.EstadoGeneralEnum;
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
class AtencionPacienteIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired PasswordEncoder encoder;
    @Autowired JwtService jwt;
    @Autowired PacienteService patientService;
    @Autowired PersonaRepository people;
    @Autowired UsuarioRepository users;
    @Autowired EmpleadoRepository employees;
    @Autowired PacienteRepository patients;
    @Autowired PacienteLogRepository patientLogs;
    @Autowired RolRepository roles;
    @Autowired PermisoRepository permissions;
    @Autowired RolPermisoRepository rolePermissions;
    @Autowired UsuarioRolRepository userRoles;
    @Autowired MaterialCristalRepository materials;
    @Autowired VentaRepository sales;
    @Autowired RecetaRepository prescriptions;
    @Autowired UsuarioLogRepository userLogs;
    @Autowired PlatformTransactionManager transactions;
    @Value("${app.jwt.secret}") String testSecret;
    Usuario actor;
    Empleado employee;
    Rol role;

    Persona person(String name) {
        return people.saveAndFlush(Persona.builder().nombres(name).apellidos("Integración")
                .numeroDocumento(UUID.randomUUID().toString()).tipoDocumento(TipoDocumentoEnum.DNI).build());
    }

    @BeforeEach
    void setup() {
        // La auditoría de login usa REQUIRES_NEW y necesita una cuenta ya confirmada.
        var fixtureTransaction = new TransactionTemplate(transactions);
        fixtureTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        fixtureTransaction.executeWithoutResult(status -> createFixture());
    }

    void createFixture() {
        Persona professional = person("Profesional");
        actor = users.saveAndFlush(Usuario.builder().nombre("integracion-" + UUID.randomUUID())
                .correo(UUID.randomUUID() + "@test.invalid").password(encoder.encode("PruebaSegura123!"))
                .persona(professional).build());
        employee = employees.saveAndFlush(Empleado.builder().persona(professional).usuario(actor).build());
        role = roles.saveAndFlush(Rol.builder().nombre("PRUEBA-" + UUID.randomUUID()).build());
        for (String permission : List.of("PACIENTES_LEER", "PACIENTES_REGISTRAR", "PACIENTES_GESTIONAR",
                "EXAMENES_REGISTRAR", "RECETAS_REGISTRAR")) {
            rolePermissions.saveAndFlush(RolPermiso.builder().rol(role)
                    .permiso(permissions.findByNombreIgnoreCase(permission).orElseThrow()).build());
        }
        userRoles.saveAndFlush(UsuarioRol.builder().usuario(actor).rol(role).build());
    }

    @AfterTransaction
    void cleanup() {
        new TransactionTemplate(transactions).executeWithoutResult(status -> {
            userLogs.deleteAll(userLogs.findByUsuarioIdUsuarioOrderByFechaLogDesc(actor.getIdUsuario()));
            userRoles.deleteByUsuarioIdUsuario(actor.getIdUsuario());
            rolePermissions.deleteByRolIdRol(role.getIdRol());
            employees.deleteById(employee.getIdPersona());
            users.deleteById(actor.getIdUsuario());
            people.deleteById(employee.getIdPersona());
            roles.deleteById(role.getIdRol());
        });
    }

    String login() throws Exception {
        String response = mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(java.util.Map.of("username", actor.getNombre(), "password", "PruebaSegura123!"))))
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
                """.formatted(patientId, employee.getIdPersona());
    }

    @Test
    void flujoCompletoConLoginJwtAuditoriaRecetaLenteHistorialPdfYBloqueoPorVenta() throws Exception {
        String bearer = login();
        String document = UUID.randomUUID().toString();
        String patientJson = json.writeValueAsString(java.util.Map.of("persona", java.util.Map.of(
                "nombres", "Paciente", "apellidos", "Integración", "tipoDocumento", "DNI", "numeroDocumento", document)));
        JsonNode patient = json.readTree(mvc.perform(post("/api/v1/pacientes").header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON).content(patientJson))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
        long patientId = patient.get("idPersona").asLong();
        mvc.perform(post("/api/v1/pacientes").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content(patientJson)).andExpect(status().isConflict());
        JsonNode recipe = json.readTree(mvc.perform(post("/api/v1/recetas/examen-visual").header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON).content(exam(patientId)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
        long recipeId = recipe.get("idReceta").asLong();
        var material = materials.saveAndFlush(MaterialCristal.builder().nombre("Material " + UUID.randomUUID()).build());
        mvc.perform(get("/api/v1/catalogos-opticos/materiales-cristal").header("Authorization", bearer))
                .andExpect(status().isOk());
        String lens = json.writeValueAsString(java.util.Map.of("idReceta", recipeId,
                "idMaterialCristal", material.getIdMaterialCristal(), "subTotal", new BigDecimal("380.00")));
        mvc.perform(post("/api/v1/recetas/{id}/lentes", recipeId).header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content(lens)).andExpect(status().isCreated());
        mvc.perform(get("/api/v1/recetas/{id}", recipeId).header("Authorization", bearer))
                .andExpect(status().isOk()).andExpect(jsonPath("$.lentes.length()").value(1));
        byte[] pdf = mvc.perform(get("/api/v1/recetas/{id}/pdf", recipeId).header("Authorization", bearer)
                        .accept(MediaType.APPLICATION_PDF)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();
        try (var parsed = Loader.loadPDF(pdf)) {
            assertThat(new PDFTextStripper().getText(parsed)).contains("Paciente Integración", "-1.25", "Material");
        }
        mvc.perform(patch("/api/v1/pacientes/{id}/estado", patientId).header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":0}")).andExpect(status().isOk());
        mvc.perform(post("/api/v1/recetas/examen-visual").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content(exam(patientId))).andExpect(status().isConflict());
        mvc.perform(get("/api/v1/recetas/paciente/{id}/historial", patientId).header("Authorization", bearer))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(patch("/api/v1/pacientes/{id}/estado", patientId).header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":1}")).andExpect(status().isOk());
        mvc.perform(post("/api/v1/recetas/externa").header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content(exam(patientId)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.esExterno").value(1));
        sales.saveAndFlush(Venta.builder().receta(prescriptions.findById(recipeId).orElseThrow())
                .paciente(patients.findById(patientId).orElseThrow()).empleado(employee)
                .total(new BigDecimal("380.00")).estado(EstadoVentaEnum.PENDIENTE)
                .tipoComprobante(TipoComprobanteEnum.BOLETA).build());
        mvc.perform(post("/api/v1/recetas/{id}/lentes", recipeId).header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content(lens)).andExpect(status().isConflict());
        assertThat(patientLogs.findByPacienteIdPersonaOrderByFechaLogDesc(patientId)).hasSize(3)
                .allSatisfy(log -> assertThat(log.getUsuario().getIdUsuario()).isEqualTo(actor.getIdUsuario()));
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
        var assignment = rolePermissions.findByRolIdRolAndPermisoIdPermiso(role.getIdRol(), read.getIdPermiso()).orElseThrow();
        rolePermissions.delete(assignment);
        rolePermissions.flush();
        mvc.perform(get("/api/v1/pacientes").header("Authorization", bearer)).andExpect(status().isForbidden());
        actor.setEstado(EstadoGeneralEnum.INACTIVO);
        users.saveAndFlush(actor);
        mvc.perform(get("/api/v1/pacientes").header("Authorization", bearer)).andExpect(status().isUnauthorized());
    }

    @Test
    void tokenDeEmpleadoInactivoExpiradoOMalformadoSeRechaza() throws Exception {
        String bearer = login();
        employee.setEstado(EstadoGeneralEnum.INACTIVO);
        employees.saveAndFlush(employee);
        mvc.perform(get("/api/v1/pacientes").header("Authorization", bearer)).andExpect(status().isUnauthorized());
        var expired = new JwtService(testSecret, -1).generarToken(User.withUsername(actor.getNombre())
                .password("unused").authorities("PACIENTES_LEER").build());
        mvc.perform(get("/api/v1/pacientes").header("Authorization", "Bearer " + expired)).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/pacientes").header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void idsYEjesFraccionariosNoSeRedondeanEnSilencio() throws Exception {
        String bearer = login();
        var patient = patients.saveAndFlush(Paciente.builder().persona(person("Paciente")).build());
        for (String invalid : List.of(exam(patient.getIdPersona()).replace("\"idPaciente\":" + patient.getIdPersona(),
                "\"idPaciente\":" + patient.getIdPersona() + ".5"),
                exam(patient.getIdPersona()).replace("\"eje\":90", "\"eje\":90.5"))) {
            mvc.perform(post("/api/v1/recetas/examen-visual").header("Authorization", bearer)
                    .contentType(MediaType.APPLICATION_JSON).content(invalid)).andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("El cuerpo JSON tiene un formato inválido o contiene valores incompatibles"));
        }
        mvc.perform(patch("/api/v1/pacientes/{id}/estado", patient.getIdPersona()).header("Authorization", bearer)
                .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":0.5}")).andExpect(status().isBadRequest());
    }

    @Test
    void tipoMedidaNumericoNoSeInterpretaComoOrdinalJava() throws Exception {
        String bearer = login();
        var patient = patients.saveAndFlush(Paciente.builder().persona(person("Paciente")).build());
        for (String numeric : List.of("1", "\"1\"")) {
            mvc.perform(post("/api/v1/recetas/examen-visual").header("Authorization", bearer)
                    .contentType(MediaType.APPLICATION_JSON).content(exam(patient.getIdPersona())
                            .replace("\"tipoMedida\":\"LEJOS\"", "\"tipoMedida\":" + numeric)))
                    .andExpect(status().isBadRequest());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void registrosConcurrentesNoDuplicanPersonaPacienteNiAuditoria(boolean existingPerson) throws Exception {
        String document = UUID.randomUUID().toString();
        var independent = new TransactionTemplate(transactions);
        independent.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        var request = PacienteRequestDTO.builder().build();
        if (existingPerson) {
            Long id = independent.execute(status -> people.saveAndFlush(Persona.builder()
                    .nombres("Paciente concurrente").apellidos("Prueba").numeroDocumento(document)
                    .tipoDocumento(TipoDocumentoEnum.DNI).build()).getIdPersona());
            request.setIdPersona(id);
        } else {
            request.setPersona(PersonaRequestDTO.builder().nombres("Paciente concurrente").apellidos("Prueba")
                    .numeroDocumento(document).tipoDocumento(TipoDocumentoEnum.DNI).build());
        }
        CyclicBarrier start = new CyclicBarrier(2);
        Callable<Boolean> register = () -> {
            start.await(10, TimeUnit.SECONDS);
            try {
                patientService.registrarPaciente(request, actor.getNombre());
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
            assertThat(patientLogs.findByPacienteIdPersonaOrderByFechaLogDesc(created.getIdPersona())).hasSize(1);
        } finally {
            independent.executeWithoutResult(status -> people.findByNumeroDocumento(document).ifPresent(person -> {
                patientLogs.deleteAll(patientLogs.findByPacienteIdPersonaOrderByFechaLogDesc(person.getIdPersona()));
                if (patients.existsById(person.getIdPersona())) patients.deleteById(person.getIdPersona());
                people.deleteById(person.getIdPersona());
            }));
        }
    }
}

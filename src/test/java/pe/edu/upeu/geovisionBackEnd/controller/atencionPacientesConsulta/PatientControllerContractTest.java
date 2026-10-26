package pe.edu.upeu.geovisionBackEnd.controller.atencionPacientesConsulta;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pe.edu.upeu.geovisionBackEnd.exception.GlobalExceptionHandler;
import pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta.*;
import java.util.List;
import java.util.stream.Stream;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

class PatientControllerContractTest {
    final PacienteService patients = mock(PacienteService.class);
    final PersonaService persons = mock(PersonaService.class);
    final DocumentoService documents = mock(DocumentoService.class);
    final ExamenService exams = mock(ExamenService.class);
    final MedidaService measures = mock(MedidaService.class);
    final MedidaOjoService eyes = mock(MedidaOjoService.class);
    final RecetaService prescriptions = mock(RecetaService.class);
    final CatalogoPacienteService catalogs = mock(CatalogoPacienteService.class);
    final Object[] services = {patients, persons, documents, exams, measures, eyes, prescriptions, catalogs};
    MockMvc mvc;

    @BeforeEach void setup() {
        reset(services);
        mvc = MockMvcBuilders.standaloneSetup(
                new PacienteController(patients, exams, prescriptions), new PersonaController(persons),
                new DocumentoController(documents), new ExamenController(exams), new MedidaController(measures),
                new MedidaOjoController(eyes), new RecetaController(prescriptions), new CatalogoPacienteController(catalogs))
                .setControllerAdvice(new AtencionExceptionHandler(), new GlobalExceptionHandler()).build();
    }

    static final String PERSON = "{\"nombres\":\"Ana\",\"apellidos\":\"Perez\"}";
    static final String PATIENT = "{\"persona\":" + PERSON + "}";
    static final String DOC = "{\"numero\":\"ABC-123\",\"idPersona\":1,\"idTipoDocumento\":1}";
    static final String EXAM = "{\"idPaciente\":1}";
    static final String MEASURE = "{\"idExamen\":1,\"idTipoMedida\":1}";
    static final String EYE = "{\"idMedida\":1,\"idTipoOjo\":1,\"esfera\":-2.50,\"eje\":180}";
    static final String PRESCRIPTION = "{\"idExamen\":1}";
    static final String DELIVERY = "{\"fechaDeEntrega\":\"2026-10-26T15:00:00\"}";

    static Stream<Arguments> successfulOperations() {
        return Stream.of(
            Arguments.of("POST", "/api/v1/pacientes", PATIENT, 201),
            Arguments.of("POST", "/api/v1/pacientes/persona-existente", "{\"idPersona\":1}", 201),
            Arguments.of("GET", "/api/v1/pacientes/1", "", 200),
            Arguments.of("PUT", "/api/v1/pacientes/1", PERSON, 200),
            Arguments.of("GET", "/api/v1/pacientes", "", 200),
            Arguments.of("GET", "/api/v1/pacientes/1/historial", "", 200),
            Arguments.of("GET", "/api/v1/pacientes/1/recetas", "", 200),
            Arguments.of("GET", "/api/v1/pacientes/1/logs", "", 200),
            Arguments.of("POST", "/api/v1/personas", PERSON, 201),
            Arguments.of("PUT", "/api/v1/personas/1", PERSON, 200),
            Arguments.of("GET", "/api/v1/personas/1", "", 200),
            Arguments.of("POST", "/api/v1/documentos", DOC, 201),
            Arguments.of("PUT", "/api/v1/documentos/1", DOC, 200),
            Arguments.of("GET", "/api/v1/documentos/1", "", 200),
            Arguments.of("GET", "/api/v1/documentos?idPersona=1", "", 200),
            Arguments.of("POST", "/api/v1/examenes", EXAM, 201),
            Arguments.of("PUT", "/api/v1/examenes/1", EXAM, 200),
            Arguments.of("GET", "/api/v1/examenes/1", "", 200),
            Arguments.of("GET", "/api/v1/examenes?idPaciente=1", "", 200),
            Arguments.of("POST", "/api/v1/medidas", MEASURE, 201),
            Arguments.of("PUT", "/api/v1/medidas/1", MEASURE, 200),
            Arguments.of("GET", "/api/v1/medidas/1", "", 200),
            Arguments.of("GET", "/api/v1/medidas?idExamen=1", "", 200),
            Arguments.of("POST", "/api/v1/medidas-ojo", EYE, 201),
            Arguments.of("PUT", "/api/v1/medidas-ojo/1", EYE, 200),
            Arguments.of("GET", "/api/v1/medidas-ojo/1", "", 200),
            Arguments.of("GET", "/api/v1/medidas-ojo?idMedida=1", "", 200),
            Arguments.of("POST", "/api/v1/recetas", PRESCRIPTION, 201),
            Arguments.of("PATCH", "/api/v1/recetas/1/entrega", DELIVERY, 200),
            Arguments.of("GET", "/api/v1/recetas/1", "", 200),
            Arguments.of("GET", "/api/v1/recetas?idPaciente=1", "", 200),
            Arguments.of("GET", "/api/v1/atencion/catalogos/tipos-documento", "", 200),
            Arguments.of("GET", "/api/v1/atencion/catalogos/tipos-medida", "", 200),
            Arguments.of("GET", "/api/v1/atencion/catalogos/tipos-ojo", "", 200));
    }

    @ParameterizedTest @MethodSource("successfulOperations")
    void everyRouteDispatchesWithExpectedStatus(String method, String path, String body, int status) throws Exception {
        mvc.perform(request(org.springframework.http.HttpMethod.valueOf(method), path)
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().is(status));
        assertEquals(1, Stream.of(services).mapToLong(s -> mockingDetails(s).getInvocations().size()).sum());
    }

    static Stream<Arguments> invalidBodies() {
        return Stream.of(
            Arguments.of("/pacientes", "{}"),
            Arguments.of("/pacientes", "{\"persona\":null}"),
            Arguments.of("/pacientes", "{\"persona\":" + PERSON + ",\"documentos\":[null]}"),
            Arguments.of("/pacientes", "{\"persona\":" + PERSON + ",\"documentos\":null}"),
            Arguments.of("/pacientes", "{\"persona\":{\"nombres\":\"Ana\",\"apellidos\":\" \"}}"),
            Arguments.of("/pacientes/persona-existente", "{\"idPersona\":0}"),
            Arguments.of("/personas", "{\"nombres\":\" \",\"apellidos\":\"Perez\"}"),
            Arguments.of("/personas", "{\"nombres\":\"Ana\",\"apellidos\":\"Perez\",\"sexo\":2}"),
            Arguments.of("/personas", "{\"nombres\":\"Ana\",\"apellidos\":\"Perez\",\"fechaNacimiento\":\"not-a-date\"}"),
            Arguments.of("/documentos", "{\"numero\":\" \",\"idPersona\":1,\"idTipoDocumento\":1}"),
            Arguments.of("/documentos", "{\"numero\":\"123\",\"idPersona\":-1,\"idTipoDocumento\":1}"),
            Arguments.of("/documentos", "{\"numero\":\"123\",\"idPersona\":1}"),
            Arguments.of("/examenes", "{}"),
            Arguments.of("/examenes", "{\"idPaciente\":1.5}"),
            Arguments.of("/examenes", "{\"idPaciente\":\"1\"}"),
            Arguments.of("/examenes", "{\"idPaciente\":true}"),
            Arguments.of("/examenes", "{\"idPaciente\":9223372036854775808}"),
            Arguments.of("/examenes", "{\"idPaciente\":1,\"esExterno\":0.9}"),
            Arguments.of("/personas", "{\"nombres\":\"Ana\",\"apellidos\":\"Perez\",\"sexo\":0.9}"),
            Arguments.of("/documentos", "{\"numero\":\"123\",\"idPersona\":1.9,\"idTipoDocumento\":1}"),
            Arguments.of("/examenes", "{\"idPaciente\":1,\"esExterno\":null}"),
            Arguments.of("/examenes", "{\"idPaciente\":1,\"esExterno\":2}"),
            Arguments.of("/medidas", "{\"idExamen\":1}"),
            Arguments.of("/medidas", "{\"idExamen\":1,\"idTipoMedida\":0}"),
            Arguments.of("/medidas-ojo", "{\"idMedida\":1,\"idTipoOjo\":1,\"eje\":180.01}"),
            Arguments.of("/medidas-ojo", "{\"idMedida\":1,\"idTipoOjo\":1,\"esfera\":100000}"),
            Arguments.of("/medidas-ojo", "{\"idMedida\":1,\"idTipoOjo\":1,\"cilindro\":0.001}"),
            Arguments.of("/medidas-ojo", "{\"idMedida\":1,\"idTipoOjo\":1,\"distanciaPupilarMm\":0}"),
            Arguments.of("/medidas-ojo", "{\"idMedida\":1,\"idTipoOjo\":1,\"prisma\":-1}"),
            Arguments.of("/recetas", "{}"),
            Arguments.of("/recetas", "{\"idExamen\":1,\"fechaDeEntrega\":\"invalid\"}"));
    }

    @ParameterizedTest @MethodSource("invalidBodies")
    void invalidInputNeverReachesService(String path, String body) throws Exception {
        mvc.perform(post("/api/v1" + path).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        verifyNoInteractions(services);
    }

    @ParameterizedTest @ValueSource(strings = {
        "/api/v1/pacientes/0", "/api/v1/pacientes/-1", "/api/v1/personas/0", "/api/v1/documentos/0",
        "/api/v1/examenes/0", "/api/v1/medidas/0", "/api/v1/medidas-ojo/0", "/api/v1/recetas/0",
        "/api/v1/documentos", "/api/v1/examenes", "/api/v1/medidas", "/api/v1/medidas-ojo", "/api/v1/recetas",
        "/api/v1/pacientes?pagina=-1", "/api/v1/pacientes?tamanio=0", "/api/v1/pacientes?tamanio=101",
        "/api/v1/pacientes?estado=2", "/api/v1/examenes?idPaciente=-1",
        "/api/v1/medidas?idExamen=0", "/api/v1/medidas-ojo?idMedida=0",
        "/api/v1/recetas?idPaciente=0", "/api/v1/pacientes/1/historial?pagina=-1",
        "/api/v1/pacientes/1/recetas?tamanio=101", "/api/v1/pacientes/1/logs?tamanio=0",
        "/api/v1/pacientes/not-a-number", "/api/v1/pacientes?pagina=abc"})
    void invalidParametersNeverReachService(String url) throws Exception {
        mvc.perform(get(url)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        verifyNoInteractions(services);
    }

    @ParameterizedTest @ValueSource(strings = {"", "null", "{broken", "[]"})
    void unreadableBodiesHaveSanitizedErrors(String json) throws Exception {
        mvc.perform(post("/api/v1/personas").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        verifyNoInteractions(services);
    }

    @ParameterizedTest @ValueSource(strings = {"personas", "pacientes", "documentos", "examenes", "medidas", "medidas-ojo", "recetas"})
    void noPhysicalDeletionRouteExists(String resource) throws Exception {
        mvc.perform(delete("/api/v1/" + resource + "/1"))
                .andExpect(status().isMethodNotAllowed()).andExpect(header().exists("Allow"));
        verifyNoInteractions(services);
    }

    @Test void missingDeliveryDateIs400() throws Exception {
        mvc.perform(patch("/api/v1/recetas/1/entrega").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(services);
    }

    @Test void wrongMediaTypeIs415() throws Exception {
        mvc.perform(post("/api/v1/personas").contentType(MediaType.TEXT_PLAIN).content(PERSON))
                .andExpect(status().isUnsupportedMediaType()).andExpect(jsonPath("$.status").value(415));
        verifyNoInteractions(services);
    }

    @Test void databaseConflictDoesNotExposeSqlOrRequestValues() throws Exception {
        when(documents.create(any())).thenThrow(new org.springframework.dao.DataIntegrityViolationException("SQL secret_document"));
        var result = mvc.perform(post("/api/v1/documentos").contentType(MediaType.APPLICATION_JSON).content(DOC))
                .andExpect(status().isConflict()).andReturn();
        assertFalse(result.getResponse().getContentAsString().contains("secret_document"));
    }

    @Test void lockContentionIs409NotAnInternalError() throws Exception {
        when(prescriptions.create(any())).thenThrow(new org.springframework.dao.CannotAcquireLockException("SQL internal"));
        mvc.perform(post("/api/v1/recetas").contentType(MediaType.APPLICATION_JSON).content(PRESCRIPTION))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409));
    }

    @Test void unauthenticatedServiceCallReturns401() throws Exception {
        when(persons.read(1L)).thenThrow(new org.springframework.security.authentication.AuthenticationCredentialsNotFoundException("internal"));
        mvc.perform(get("/api/v1/personas/1")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}


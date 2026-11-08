package pe.edu.upeu.geovision_backend.controller.atencion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.dto.atencion.PacienteRequestDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.PersonaRequestDTO;
import pe.edu.upeu.geovision_backend.entity.atencion.PacienteLog;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.entity.security.Usuario;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoDocumentoEnum;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.repository.atencion.PacienteLogRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.PacienteRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.PersonaRepository;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioRepository;
import pe.edu.upeu.geovision_backend.service.atencion.PacienteService;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser(username = "operador.pacientes", authorities = {
        "PACIENTES_LEER", "PACIENTES_REGISTRAR", "PACIENTES_GESTIONAR"})
class PacienteControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired PacienteService service;
    @Autowired PacienteRepository patients;
    @Autowired PersonaRepository people;
    @Autowired UsuarioRepository users;
    @MockitoSpyBean PacienteLogRepository logs;
    Usuario actor;

    @BeforeEach
    void crearActor() {
        actor = users.saveAndFlush(Usuario.builder().nombre("operador.pacientes")
                .correo(UUID.randomUUID() + "@test.invalid").password("hash-de-prueba").build());
    }

    PacienteRequestDTO request(String document) {
        return PacienteRequestDTO.builder().persona(PersonaRequestDTO.builder()
                .nombres(" Ana ").apellidos(" Pérez ").telefono(" 999123456 ")
                .numeroDocumento(document).tipoDocumento(TipoDocumentoEnum.DNI).build()).build();
    }

    @Test
    void registraConsultaBuscaActualizaYDesactivaConAuditoria() throws Exception {
        String document = UUID.randomUUID().toString();
        String response = mvc.perform(post("/api/v1/pacientes").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(request(" " + document + " "))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.persona.nombres").value("Ana"))
                .andExpect(jsonPath("$.persona.nombreCompleto").value("Ana Pérez"))
                .andExpect(jsonPath("$.estado").value(1))
                .andExpect(jsonPath("$.idUsuario").value(actor.getIdUsuario()))
                .andReturn().getResponse().getContentAsString();
        long id = json.readTree(response).get("idPersona").asLong();
        var registered = patients.findById(id).orElseThrow().getFechaRegistro();
        mvc.perform(get("/api/v1/pacientes/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.persona.numeroDocumento").value(document));
        mvc.perform(get("/api/v1/pacientes/documento/{document}", document)).andExpect(status().isOk());
        mvc.perform(get("/api/v1/pacientes").param("termino", "ana pérez"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].idPersona").value(id));
        var update = request(document);
        update = update.toBuilder().persona(update.persona().toBuilder().telefono("988765432").build()).build();
        mvc.perform(put("/api/v1/pacientes/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(update)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.persona.telefono").value("988765432"));
        mvc.perform(patch("/api/v1/pacientes/{id}/estado", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":0}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.estado").value(0));
        // Repetir la misma transición es idempotente y no duplica el evento.
        mvc.perform(patch("/api/v1/pacientes/{id}/estado", id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":0}")).andExpect(status().isOk());
        assertThat(patients.findById(id).orElseThrow().getActivo()).isEqualTo(false);
        assertThat(people.existsById(id)).isTrue();
        assertThat(patients.findById(id).orElseThrow().getFechaRegistro()).isEqualTo(registered);
        assertThat(logs.findByPacienteIdPersonaOrderByFechaLogDesc(id)).hasSize(3)
                .allSatisfy(log -> {
                    assertThat(log.getUsuario().getIdUsuario()).isEqualTo(actor.getIdUsuario());
                    assertThat(log.getFechaLog()).isNotNull();
                });
        mvc.perform(get("/api/v1/pacientes").param("termino", document))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(0));
        mvc.perform(get("/api/v1/pacientes").param("termino", document).param("estado", "0"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(delete("/api/v1/pacientes/{id}", id)).andExpect(status().isMethodNotAllowed());
    }

    @Test
    void rechazaDuplicadosYPermiteRegistrarPersonaExistenteSinModificarla() throws Exception {
        String document = UUID.randomUUID().toString();
        Persona person = people.saveAndFlush(Persona.builder().nombres("Existente").apellidos("Prueba")
                .numeroDocumento(document).tipoDocumento(TipoDocumentoEnum.DNI).build());
        mvc.perform(post("/api/v1/pacientes").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(request(document)))).andExpect(status().isConflict());
        String existing = "{\"idPersona\":" + person.getIdPersona() + "}";
        mvc.perform(post("/api/v1/pacientes").contentType(MediaType.APPLICATION_JSON).content(existing))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.persona.nombres").value("Existente"));
        mvc.perform(post("/api/v1/pacientes").contentType(MediaType.APPLICATION_JSON).content(existing))
                .andExpect(status().isConflict());
        assertThat(logs.findByPacienteIdPersonaOrderByFechaLogDesc(person.getIdPersona())).hasSize(1);
    }

    @Test
    void rechazaIdentidadAmbiguaAusenteYAlterada() throws Exception {
        mvc.perform(post("/api/v1/pacientes").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isConflict());
        var ambiguous = request(UUID.randomUUID().toString());
        ambiguous = ambiguous.toBuilder().idPersona(999999L).build();
        mvc.perform(post("/api/v1/pacientes").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(ambiguous))).andExpect(status().isConflict());
        var created = service.registrarPaciente(request(UUID.randomUUID().toString()), actor.getNombre());
        mvc.perform(put("/api/v1/pacientes/{id}", created.idPersona()).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(ambiguous))).andExpect(status().isConflict());
    }

    @Test
    void documentoDuplicadoEnActualizacionNoModificaDatos() throws Exception {
        var first = service.registrarPaciente(request(UUID.randomUUID().toString()), actor.getNombre());
        var second = service.registrarPaciente(request(UUID.randomUUID().toString()), actor.getNombre());
        mvc.perform(put("/api/v1/pacientes/{id}", second.idPersona()).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(request(first.persona().numeroDocumento()))))
                .andExpect(status().isConflict());
        assertThat(service.obtenerPorId(second.idPersona()).persona().numeroDocumento())
                .isEqualTo(second.persona().numeroDocumento());
        assertThat(logs.findByPacienteIdPersonaOrderByFechaLogDesc(second.idPersona())).hasSize(1);
    }

    @Test
    void rechazaSuplantacionDeAuditoriaYActorInactivo() throws Exception {
        var request = request(UUID.randomUUID().toString());
        request = request.toBuilder().idUsuario(actor.getIdUsuario() + 9999).build();
        mvc.perform(post("/api/v1/pacientes").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(request))).andExpect(status().isForbidden());
        actor.setActivo(false);
        users.saveAndFlush(actor);
        request = request.toBuilder().idUsuario(null).build();
        mvc.perform(post("/api/v1/pacientes").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(request))).andExpect(status().isForbidden());
    }

    @Test
    void validaJsonParametrosYRecursosAusentes() throws Exception {
        mvc.perform(post("/api/v1/pacientes").contentType(MediaType.APPLICATION_JSON)
                .content("{\"persona\":{\"nombres\":\" \"}}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/pacientes").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/pacientes/-1")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/pacientes/99999999")).andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/pacientes/documento/NO-EXISTE")).andExpect(status().isNotFound());
        for (String[] invalid : new String[][]{{"page", "-1"}, {"size", "0"}, {"size", "101"}, {"estado", "2"}}) {
            mvc.perform(get("/api/v1/pacientes").param(invalid[0], invalid[1])).andExpect(status().isBadRequest());
        }
        mvc.perform(get("/api/v1/pacientes").param("page", "2147483647").param("size", "100"))
                .andExpect(status().isBadRequest());
        for (String invalid : new String[]{"{}", "{\"estado\":null}", "{\"estado\":2}"}) {
            mvc.perform(patch("/api/v1/pacientes/1/estado").contentType(MediaType.APPLICATION_JSON)
                    .content(invalid)).andExpect(status().isBadRequest());
        }
    }

    @Test
    void busquedaEscapaComodines() {
        String document = UUID.randomUUID().toString();
        var request = request(document);
        request = request.toBuilder().persona(request.persona().toBuilder().nombres("Porcentaje%_!").build()).build();
        var created = service.registrarPaciente(request, actor.getNombre());
        var page = service.listarPacientes("%_!", true,
                org.springframework.data.domain.PageRequest.of(0, 10));
        assertThat(page.getContent()).extracting("idPersona").containsExactly(created.idPersona());
    }

    @Test
    void actualizacionDeContactoNoReactivaPacienteNiCambiaCreador() {
        var created = service.registrarPaciente(request(UUID.randomUUID().toString()), actor.getNombre());
        service.cambiarEstadoPaciente(created.idPersona(), 0, actor.getNombre());
        var updated = service.actualizarPaciente(created.idPersona(),
                request(created.persona().numeroDocumento()), actor.getNombre());
        assertThat(updated.estado()).isZero();
        assertThat(updated.idPersona()).isEqualTo(created.idPersona());
        assertThat(updated.idUsuario()).isEqualTo(actor.getIdUsuario());
    }

    @Test
    @WithMockUser(username = "operador.pacientes", authorities = "PACIENTES_REGISTRAR")
    void permisoDeRegistroNoPermiteGestionNiLectura() throws Exception {
        mvc.perform(get("/api/v1/pacientes")).andExpect(status().isForbidden());
        mvc.perform(put("/api/v1/pacientes/1").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(request(UUID.randomUUID().toString()))))
                .andExpect(status().isForbidden());
        mvc.perform(patch("/api/v1/pacientes/1/estado").contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":0}")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "VENTAS_LEER")
    void deniegaUsuarioSinPermisosDePacientes() throws Exception {
        mvc.perform(get("/api/v1/pacientes")).andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/pacientes").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(request(UUID.randomUUID().toString()))))
                .andExpect(status().isForbidden());
        mvc.perform(put("/api/v1/pacientes/1").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(request(UUID.randomUUID().toString()))))
                .andExpect(status().isForbidden());
        mvc.perform(patch("/api/v1/pacientes/1/estado").contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":0}")).andExpect(status().isForbidden());
    }

    @Test
    @org.springframework.security.test.context.support.WithAnonymousUser
    void exigeAutenticacion() throws Exception {
        mvc.perform(get("/api/v1/pacientes")).andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void falloDeAuditoriaRevierteAltaActualizacionYEstado() {
        String document = UUID.randomUUID().toString();
        var existing = service.registrarPaciente(request(UUID.randomUUID().toString()), actor.getNombre());
        doThrow(new org.springframework.dao.DataAccessResourceFailureException("Auditoría no disponible"))
                .when(logs).saveAndFlush(any(PacienteLog.class));
        try {
            assertThatThrownBy(() -> service.registrarPaciente(request(document), actor.getNombre()))
                    .isInstanceOf(org.springframework.dao.DataAccessException.class);
            assertThat(people.findByNumeroDocumento(document)).isEmpty();
            assertThat(patients.findByPersonaNumeroDocumento(document)).isEmpty();
            var update = request(existing.persona().numeroDocumento());
            var invalidUpdate = update.toBuilder().persona(update.persona().toBuilder().nombres("Cambio que debe revertirse").build()).build();
            assertThatThrownBy(() -> service.actualizarPaciente(existing.idPersona(), invalidUpdate, actor.getNombre()))
                    .isInstanceOf(org.springframework.dao.DataAccessException.class);
            assertThat(service.obtenerPorId(existing.idPersona()).persona().nombres()).isEqualTo("Ana");
            assertThatThrownBy(() -> service.cambiarEstadoPaciente(existing.idPersona(), 0, actor.getNombre()))
                    .isInstanceOf(org.springframework.dao.DataAccessException.class);
            assertThat(service.obtenerPorId(existing.idPersona()).estado()).isEqualTo(1);
        } finally {
            logs.deleteAll(logs.findByPacienteIdPersonaOrderByFechaLogDesc(existing.idPersona()));
            patients.deleteById(existing.idPersona());
            people.deleteById(existing.idPersona());
            users.deleteById(actor.getIdUsuario());
        }
    }
}

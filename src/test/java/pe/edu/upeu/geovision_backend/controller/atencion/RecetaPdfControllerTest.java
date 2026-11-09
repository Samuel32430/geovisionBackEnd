package pe.edu.upeu.geovision_backend.controller.atencion;

import java.nio.charset.StandardCharsets;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.entity.security.Empleado;
import pe.edu.upeu.geovision_backend.entity.atencion.Paciente;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.entity.atencion.Receta;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoDocumentoEnum;
import pe.edu.upeu.geovision_backend.repository.security.EmpleadoRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.PacienteRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.PersonaRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.RecetaRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.jpa.open-in-view=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser(authorities = "PACIENTES_LEER")
@RequiredArgsConstructor
class RecetaPdfControllerTest {

    // >>>> FIXTURES
    private final MockMvc mvc;
    private final PersonaRepository people;
    private final PacienteRepository patients;
    private final EmpleadoRepository employees;
    private final RecetaRepository prescriptions;
    Receta recipe;

    // >>>> HELPERS
    Persona person(String name) {
        return people.saveAndFlush(new Persona(null, name, "Prueba", null, null, null, null, UUID.randomUUID().toString(), TipoDocumentoEnum.DNI));
    }

    // >>>> SETUP
    @BeforeEach
    void setup() {
        var patient = patients.saveAndFlush(new Paciente(null, person("Paciente PDF"), true, null, null));
        var employee = employees.saveAndFlush(new Empleado(null, person("Profesional PDF"), true, null, null));
        recipe = prescriptions.saveAndFlush(new Receta(null, null, 0, patient, employee));
    }

    // >>>> TESTS
    @Test
    void descargaPdfConNombreLongitudYCacheDeshabilitado() throws Exception {
        var response = mvc.perform(get("/api/v1/recetas/{id}/pdf", recipe.getId()).accept(MediaType.APPLICATION_PDF))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"receta-" + recipe.getId() + ".pdf\""))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andReturn().getResponse();
        byte[] bytes = response.getContentAsByteArray();
        assertThat(response.getHeader("Content-Length")).isEqualTo(Integer.toString(bytes.length));
        assertThat(bytes).startsWith("%PDF-".getBytes(StandardCharsets.US_ASCII));
        try (var document = Loader.loadPDF(bytes)) {
            assertThat(new PDFTextStripper().getText(document)).contains("Paciente PDF Prueba",
                    "Profesional PDF Prueba", "No hay medidas registradas."
            );
        }
    }

    @Test
    void validaIdYRecursoInexistenteInclusoConAcceptPdf() throws Exception {
        for (String invalid : List.of("0", "-1", "abc")) {
            mvc.perform(get("/api/v1/recetas/" + invalid + "/pdf").accept(MediaType.APPLICATION_PDF))
                    .andExpect(status().isBadRequest());
        }
        mvc.perform(get("/api/v1/recetas/99999999/pdf").accept(MediaType.APPLICATION_PDF))
                .andExpect(status().isNotFound()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser(authorities = "RECETAS_REGISTRAR")
    void permisoDeRegistroNoPermiteDescargarPdf() throws Exception {
        mvc.perform(get("/api/v1/recetas/{id}/pdf", recipe.getId()).accept(MediaType.APPLICATION_PDF))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void exigeAutenticacion() throws Exception {
        mvc.perform(get("/api/v1/recetas/{id}/pdf", recipe.getId()).accept(MediaType.APPLICATION_PDF))
                .andExpect(status().isUnauthorized());
    }
}

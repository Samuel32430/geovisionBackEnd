package pe.edu.upeu.geovision_backend.controller.atencion;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
class RecetaPdfControllerTest {

    @Autowired MockMvc mvc;
    @Autowired PersonaRepository people;
    @Autowired PacienteRepository patients;
    @Autowired EmpleadoRepository employees;
    @Autowired RecetaRepository prescriptions;
    Receta recipe;

    Persona person(String name) {
        return people.saveAndFlush(Persona.builder().nombres(name).apellidos("Prueba")
                .numeroDocumento(UUID.randomUUID().toString()).tipoDocumento(TipoDocumentoEnum.DNI).build());
    }

    @BeforeEach
    void setup() {
        var patient = patients.saveAndFlush(Paciente.builder().persona(person("Paciente PDF")).build());
        var employee = employees.saveAndFlush(Empleado.builder().persona(person("Profesional PDF")).build());
        recipe = prescriptions.saveAndFlush(Receta.builder().paciente(patient).empleado(employee).build());
    }

    @Test
    void descargaPdfConNombreLongitudYCacheDeshabilitado() throws Exception {
        var response = mvc.perform(get("/api/v1/recetas/{id}/pdf", recipe.getIdReceta()).accept(MediaType.APPLICATION_PDF))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"receta-" + recipe.getIdReceta() + ".pdf\""))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andReturn().getResponse();
        byte[] bytes = response.getContentAsByteArray();
        assertThat(response.getHeader("Content-Length")).isEqualTo(Integer.toString(bytes.length));
        assertThat(bytes).startsWith("%PDF-".getBytes(java.nio.charset.StandardCharsets.US_ASCII));
        try (var document = Loader.loadPDF(bytes)) {
            assertThat(new PDFTextStripper().getText(document)).contains("Paciente PDF Prueba",
                    "Profesional PDF Prueba", "No hay medidas registradas.");
        }
    }

    @Test
    void validaIdYRecursoInexistenteInclusoConAcceptPdf() throws Exception {
        for (String invalid : java.util.List.of("0", "-1", "abc")) {
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
        mvc.perform(get("/api/v1/recetas/{id}/pdf", recipe.getIdReceta()).accept(MediaType.APPLICATION_PDF))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void exigeAutenticacion() throws Exception {
        mvc.perform(get("/api/v1/recetas/{id}/pdf", recipe.getIdReceta()).accept(MediaType.APPLICATION_PDF))
                .andExpect(status().isUnauthorized());
    }
}

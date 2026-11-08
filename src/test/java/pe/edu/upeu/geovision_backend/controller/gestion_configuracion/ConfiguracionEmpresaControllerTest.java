package pe.edu.upeu.geovision_backend.controller.gestion_configuracion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.upeu.geovision_backend.entity.gestion_configuracion.ConfiguracionEmpresa;
import pe.edu.upeu.geovision_backend.repository.gestion_configuracion.ConfiguracionEmpresaLock;
import pe.edu.upeu.geovision_backend.repository.gestion_configuracion.ConfiguracionEmpresaRepository;
import pe.edu.upeu.geovision_backend.service.gestion_configuracion.ConfiguracionEmpresaService;
import pe.edu.upeu.geovision_backend.dto.gestion_configuracion.ConfiguracionEmpresaDTO;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(authorities = "MANTENIMIENTO_GESTIONAR")
class ConfiguracionEmpresaControllerTest {
    private static final String URL = "/api/v1/configuracion/empresa";
    private static final String BODY = """
            {"idConfiguracionEmpresa":999,"nombreLegal":" Óptica Geo Visión ",
             "nombreComercial":" Geo Visión ","ruc":"20123456789",
             "direccion":" Lima ","telefono":" 999123456 ",
             "correo":"contacto@example.com","logo":" /imagenes/logo.png "}
            """;

    @Autowired MockMvc mvc;
    @Autowired ConfiguracionEmpresaRepository repository;
    @Autowired ConfiguracionEmpresaService service;
    // H2 no implementa LOCK TABLE de Oracle. Su comportamiento no se simula como prueba Oracle.
    @MockitoBean ConfiguracionEmpresaLock lock;

    @BeforeEach
    void limpiar() {
        repository.deleteAll();
        clearInvocations(lock);
    }

    @Test
    void registraConsultaActualizaSinCambiarIdentidad() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated()).andExpect(header().string("Location", URL))
                .andExpect(jsonPath("$.nombreLegal").value("Óptica Geo Visión"))
                .andExpect(jsonPath("$.nombreComercial").value("Geo Visión"))
                .andExpect(jsonPath("$.direccion").value("Lima"))
                .andExpect(jsonPath("$.telefono").value("999123456"))
                .andExpect(jsonPath("$.logo").value("/imagenes/logo.png"));
        Long id = repository.findAll().getFirst().getId();
        assertThat(id).isNotEqualTo(999L);
        mvc.perform(get(URL)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
        mvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(BODY.replace("20123456789", "20987654321")
                                .replace("contacto@example.com", "nuevo@example.com")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.ruc").value("20987654321"))
                .andExpect(jsonPath("$.correo").value("nuevo@example.com"));
        assertThat(repository.count()).isEqualTo(1);
        verify(lock, times(2)).bloquear();
    }

    @Test
    void rechazaSegundaEmpresaAunqueTengaOtroRuc() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isCreated());
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(BODY.replace("20123456789", "20987654321")))
                .andExpect(status().isConflict());
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void consultaYActualizacionSinConfiguracionRetornan404() throws Exception {
        mvc.perform(get(URL)).andExpect(status().isNotFound());
        mvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isNotFound());
        assertThat(repository.count()).isZero();
    }

    @Test
    void rechazaDatosInvalidosAntesDePersistir() throws Exception {
        for (String invalid : new String[]{BODY.replace("20123456789", "123"),
                BODY.replace("contacto@example.com", "correo-invalido"),
                BODY.replace(" Óptica Geo Visión ", " "),
                BODY.replace(" Lima ", "x".repeat(201)),
                BODY.replace(" Geo Visión ", "x".repeat(201)),
                BODY.replace(" 999123456 ", "x".repeat(51)),
                BODY.replace(" /imagenes/logo.png ", "x".repeat(201))}) {
            mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(invalid)).andExpect(status().isBadRequest());
            mvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON).content(invalid)).andExpect(status().isBadRequest());
        }
        assertThat(repository.count()).isZero();
        verifyNoInteractions(lock);
    }

    @Test
    void validaTambienInvocacionesDirectasDelServicio() {
        assertThatThrownBy(() -> service.registrar(new ConfiguracionEmpresaDTO(null, "Empresa", null, "123", null, null, null, null)))
                .isInstanceOf(jakarta.validation.ConstraintViolationException.class);
        verifyNoInteractions(lock);
    }

    @Test
    void permiteOpcionalesNulosYActualizacionLosLimpia() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isCreated());
        mvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombreLegal\":\"Empresa\",\"ruc\":\"20123456789\",\"logo\":\" \"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombreLegal").value("Empresa"));
        var empresa = repository.findAll().getFirst();
        assertThat(empresa.getLogo()).isNull();
        assertThat(empresa.getNombreComercial()).isNull();
        assertThat(empresa.getCorreo()).isNull();
    }

    @Test
    void detectaDatosPreexistentesConMultiplesEmpresas() throws Exception {
        repository.saveAndFlush(new ConfiguracionEmpresa(null, "Uno", null, "20123456789", null, null, null, null));
        repository.saveAndFlush(new ConfiguracionEmpresa(null, "Dos", null, "20987654321", null, null, null, null));
        mvc.perform(get(URL)).andExpect(status().isConflict());
        mvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isConflict());
        assertThat(repository.count()).isEqualTo(2);
    }

    @Test
    @WithMockUser(authorities = "VENTAS_LEER")
    void rechazaUsuarioSinPermiso() throws Exception {
        mvc.perform(get(URL)).andExpect(status().isForbidden());
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isForbidden());
        mvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isForbidden());
        verifyNoInteractions(lock);
    }

    @Test
    @WithAnonymousUser
    void exigeAutenticacion() throws Exception {
        mvc.perform(get(URL)).andExpect(status().isUnauthorized());
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isUnauthorized());
        mvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isUnauthorized());
    }

    @Test
    void rechazaJsonInvalidoYNoPermiteEliminar() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("{" )).andExpect(status().isBadRequest());
        mvc.perform(delete(URL)).andExpect(status().isMethodNotAllowed());
    }
}

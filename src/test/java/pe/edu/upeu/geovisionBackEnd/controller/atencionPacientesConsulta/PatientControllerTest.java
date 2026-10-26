package pe.edu.upeu.geovisionBackEnd.controller.atencionPacientesConsulta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.exception.*;
import pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta.*;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PatientControllerTest {
    final PacienteService patients = mock(PacienteService.class);
    MockMvc mvc;

    @BeforeEach void setup() {
        mvc = MockMvcBuilders.standaloneSetup(new PacienteController(patients,
                mock(ExamenService.class), mock(RecetaService.class)))
                .setControllerAdvice(new GlobalExceptionHandler(), new AtencionExceptionHandler()).build();
    }

    @Test void invalidNestedPersonReturns400WithoutServiceCall() throws Exception {
        mvc.perform(post("/api/v1/pacientes").contentType(MediaType.APPLICATION_JSON)
                .content("{\"persona\":{\"nombres\":\" \",\"apellidos\":\"Perez\"}}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(patients);
    }

    @Test void validRegistrationReturns201() throws Exception {
        when(patients.registrar(any())).thenReturn(new PacienteDetalleResponseDTO());
        mvc.perform(post("/api/v1/pacientes").contentType(MediaType.APPLICATION_JSON)
                .content("{\"persona\":{\"nombres\":\"Ana\",\"apellidos\":\"Perez\"}}"))
                .andExpect(status().isCreated());
        verify(patients).registrar(any());
    }

    @Test void listUsesStablePaginationContract() throws Exception {
        when(patients.listar(isNull(), isNull(), eq(0), eq(10), eq("persona.apellidos"), eq("asc")))
                .thenReturn(new PaginaResponse<>(List.of(), 0, 10, 0, 0, true));
        mvc.perform(get("/api/v1/pacientes")).andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido").isArray())
                .andExpect(jsonPath("$.pagina").value(0))
                .andExpect(jsonPath("$.totalElementos").value(0));
    }

    @Test void missingPatientReturns404() throws Exception {
        when(patients.read(42L)).thenThrow(new RecursosNoEncontradosException("Paciente no encontrado"));
        mvc.perform(get("/api/v1/pacientes/42")).andExpect(status().isNotFound());
    }

    @Test void clinicalHistoryCannotBeDeleted() throws Exception {
        mvc.perform(delete("/api/v1/pacientes/42")).andExpect(status().isMethodNotAllowed());
        verifyNoInteractions(patients);
    }

    @Test void permissionFailureReturns403() throws Exception {
        when(patients.read(42L)).thenThrow(new org.springframework.security.access.AccessDeniedException("denied"));
        mvc.perform(get("/api/v1/pacientes/42")).andExpect(status().isForbidden());
    }
}


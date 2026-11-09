package pe.edu.upeu.geovision_backend.exception.comun;

import pe.edu.upeu.geovision_backend.exception.GlobalExceptionHandler;

import jakarta.validation.constraints.Min;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    // >>>> FIXTURES
    private MockMvc mvc;

    // >>>> SETUP
    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new ExampleController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // >>>> TESTS
    @Test
    void invalidParametersReturnCustomJsonAndFieldErrors() throws Exception {
        mvc.perform(get("/test/validated").param("page", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/test/validated"))
                .andExpect(jsonPath("$.validationErrors.page").isNotEmpty());
    }

    @Test
    void unsupportedMethodsKeepAllowHeaderAndCustomJson() throws Exception {
        mvc.perform(delete("/test/validated"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string(HttpHeaders.ALLOW, containsString("GET")))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.path").value("/test/validated"));
    }

    @Test
    void unexpectedExceptionsStillUseCatchAllAndCustomJson() throws Exception {
        mvc.perform(get("/test/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Unexpected failure"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/test/unexpected"));
    }

    @Test
    void invalidReturnValuesRemainServerErrors() throws Exception {
        mvc.perform(get("/test/invalid-response"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500));
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 409, 429})
    void explicitHttpErrorsKeepTheirStatusAndCustomJson(int statusCode) throws Exception {
        mvc.perform(get("/test/explicit-status").param("status", String.valueOf(statusCode)))
                .andExpect(status().is(statusCode))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.message").value("Request rejected"));
    }

    @RestController
    class ExampleController {

        @GetMapping("/test/validated")
        public String validated(@RequestParam("page") @Min(0) int page) {
            return "ok";
        }

        @GetMapping("/test/unexpected")
        public String unexpected() {
            throw new IllegalStateException("Unexpected failure");
        }

        @GetMapping("/test/invalid-response")
        @Min(0)
        public int invalidResponse() {
            return -1;
        }

        @GetMapping("/test/explicit-status")
        public String explicitStatus(@RequestParam("status") int statusCode) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(statusCode), "Request rejected");
        }
    }
}

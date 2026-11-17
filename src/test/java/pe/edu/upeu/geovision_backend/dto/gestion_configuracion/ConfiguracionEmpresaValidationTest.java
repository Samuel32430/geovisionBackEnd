package pe.edu.upeu.geovision_backend.dto.gestion_configuracion;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfiguracionEmpresaValidationTest {

    // >>>> FIXTURES
    private static ValidatorFactory factory;
    private static Validator validator;

    // >>>> SETUP
    @BeforeAll
    static void setup() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // >>>> CLEANUP
    @AfterAll
    static void close() {
        factory.close();
    }

    // >>>> TESTS
    @ParameterizedTest
    @ValueSource(strings = {"20123456789", "00123456789"})
    void acceptsExactlyElevenDigits(String ruc) {
        assertTrue(validator.validate(company(ruc)).isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "2012345678", "201234567890", "2012345678A",
            " 20123456789", "20123456789 ", "２０１２３４５６７８９"}
    )
    void rejectsInvalidRuc(String ruc) {
        assertFalse(validator.validate(company(ruc)).isEmpty());
    }

    // >>>> HELPERS
    private ConfiguracionEmpresaDTO company(String ruc) {
        return new ConfiguracionEmpresaDTO(null, "Óptica Geo Visión", null, ruc, null, null, null, null);
    }
}

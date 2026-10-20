package pe.edu.upeu.geovisionBackEnd.dto.gestionMantenimiento;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CatalogosOrdenTrabajoDTOTests {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void preparar() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void cerrar() {
        factory.close();
    }

    @Test
    void laboratorioAceptaDatosMinimosYContactoOpcional() {
        LaboratorioRequestDTO dto = laboratorioValido();
        assertEquals(Boolean.TRUE, dto.getEstado());
        assertTrue(validator.validate(dto).isEmpty());
        dto.setCorreo("laboratorio@example.com");
        dto.setEstado(false);
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void laboratorioRechazaObligatoriosVacios() {
        LaboratorioRequestDTO dto = laboratorioValido();
        dto.setNombre("   ");
        dto.setRuc(" ");
        dto.setEstado(null);
        assertEquals(3, validator.validate(dto).size());
    }

    @Test
    void laboratorioRespetaLimitesDelDdlYFormatoDeCorreo() {
        LaboratorioRequestDTO dto = laboratorioValido();
        dto.setRuc("1".repeat(20));
        dto.setNombre("x".repeat(200));
        dto.setTelefono("1".repeat(50));
        dto.setDireccion("x".repeat(500));
        assertTrue(validator.validate(dto).isEmpty());
        dto.setRuc("1".repeat(21));
        dto.setTelefono("1".repeat(51));
        dto.setCorreo("correo-invalido");
        assertEquals(3, validator.validate(dto).size());
    }

    @Test
    void estadoOrdenTrabajoEsUnCatalogoAbiertoConNombreObligatorio() {
        EstadoOrdenTrabajoRequestDTO dto = new EstadoOrdenTrabajoRequestDTO();
        dto.setNombre("NUEVO_ESTADO");
        assertTrue(validator.validate(dto).isEmpty());
        dto.setNombre(" ");
        assertFalse(validator.validate(dto).isEmpty());
        dto.setNombre("x".repeat(201));
        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void parametroAdmiteToleranciaYPeriodoNulosYValorEsperadoNegativo() {
        TipoParametroCalidadRequestDTO dto = parametroValido();
        dto.setValorEsperado(new BigDecimal("-2.50"));
        assertTrue(validator.validate(dto).isEmpty());
        dto.setValorEsperado(null);
        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void parametroRespetaElLimiteDeMedioPeriodo() {
        TipoParametroCalidadRequestDTO dto = parametroValido();
        dto.setPeriodo(new BigDecimal("180"));
        dto.setTolerancia(new BigDecimal("90"));
        assertTrue(validator.validate(dto).isEmpty());
        dto.setTolerancia(new BigDecimal("90.0001"));
        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void parametroRechazaPeriodoNoPositivoYToleranciaNegativa() {
        TipoParametroCalidadRequestDTO dto = parametroValido();
        dto.setPeriodo(BigDecimal.ZERO);
        assertFalse(validator.validate(dto).isEmpty());
        dto.setPeriodo(new BigDecimal("-180"));
        assertFalse(validator.validate(dto).isEmpty());
        dto.setPeriodo(null);
        dto.setTolerancia(new BigDecimal("-0.0001"));
        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void parametroRespetaLaPrecisionDeOracle() {
        TipoParametroCalidadRequestDTO dto = parametroValido();
        dto.setValorEsperado(new BigDecimal("999999.9999"));
        assertTrue(validator.validate(dto).isEmpty());
        dto.setValorEsperado(new BigDecimal("1000000"));
        assertFalse(validator.validate(dto).isEmpty());
        dto.setValorEsperado(new BigDecimal("0.00001"));
        assertFalse(validator.validate(dto).isEmpty());
    }

    private LaboratorioRequestDTO laboratorioValido() {
        LaboratorioRequestDTO dto = new LaboratorioRequestDTO();
        dto.setNombre("Laboratorio Central");
        dto.setRuc("20123456789");
        return dto;
    }

    private TipoParametroCalidadRequestDTO parametroValido() {
        TipoParametroCalidadRequestDTO dto = new TipoParametroCalidadRequestDTO();
        dto.setNombre("Esfera");
        dto.setUnidad("Dioptrias");
        dto.setValorEsperado(BigDecimal.ZERO);
        return dto;
    }
}

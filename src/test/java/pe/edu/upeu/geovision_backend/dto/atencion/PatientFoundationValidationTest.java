package pe.edu.upeu.geovision_backend.dto.atencion;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoMedidaEnum;
import java.math.BigDecimal;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class PatientFoundationValidationTest {

    // >>>> FIXTURES
    static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    // >>>> CLEANUP
    @AfterAll static void close() { factory.close(); }

    // >>>> HELPERS
    MedidaRequestDTO validMeasure() {
        return new MedidaRequestDTO(TipoMedidaEnum.LEJOS, new BigDecimal("62.50"), null, null, null, new MedidaOjoDTO(null, null, new BigDecimal("-1.25"), null, 90, null), new MedidaOjoDTO(null, null, new BigDecimal("1.50"), null, 85, null), null);
    }

    // >>>> TESTS
    @Test void validClinicalInputUsesLejosByDefaultAndAllowsTextVisualAcuity() {
        var request = validMeasure();
        request = new MedidaRequestDTO(request.tipoMedida(), request.distanciaPupilar(), BigDecimal.ZERO, request.observaciones(), request.idReceta(), new MedidaOjoDTO(request.ojoDerecho().id(), "20/20", request.ojoDerecho().esfera(), request.ojoDerecho().cilindro(), request.ojoDerecho().eje(), request.ojoDerecho().adicion()), request.ojoIzquierdo(), request.idEnvioLaboratorio());
        assertEquals(TipoMedidaEnum.LEJOS, request.tipoMedida());
        assertTrue(factory.getValidator().validate(request).isEmpty());
    }

    @ParameterizedTest
    @CsvSource({"0,0", "-1,0", "1000,0", "62.501,0", "62.50,-0.01", "62.50,1000", "62.50,0.001"})
    void rejectsNonpositiveDistanceAndValuesOutsideDdlPrecision(String distance, String prism) {
        var request = validMeasure();
        request = new MedidaRequestDTO(request.tipoMedida(), new BigDecimal(distance), new BigDecimal(prism), request.observaciones(), request.idReceta(), request.ojoDerecho(), request.ojoIzquierdo(), request.idEnvioLaboratorio());
        assertFalse(factory.getValidator().validate(request).isEmpty());
    }

    @Test void rejectsNullTypeAndMissingEyeWithoutPuttingValidationOnEntities() {
        var request = validMeasure();
        request = new MedidaRequestDTO(null, request.distanciaPupilar(), request.prisma(), request.observaciones(), request.idReceta(), request.ojoDerecho(), null, request.idEnvioLaboratorio());
        assertEquals(2, factory.getValidator().validate(request).size());
    }

    @ParameterizedTest
    @CsvSource({"1000,0,0", "-1000,0,0", "0,0.001,0", "0,0,100", "0,0,0.001"})
    void rejectsOpticalValuesOutsideNumericPrecision(String sphere, String cylinder, String addition) {
        var eye = new MedidaOjoDTO(null, null, new BigDecimal(sphere), new BigDecimal(cylinder), null, new BigDecimal(addition));
        assertFalse(factory.getValidator().validate(eye).isEmpty());
    }

    @Test void validatesNullItemsInPrescriptionAndLensLists() {
        var prescription = new RecetaRequestDTO(1L, 2L, 0, null, Arrays.asList((MedidaRequestDTO) null));
        assertFalse(factory.getValidator().validate(prescription).isEmpty());
        var lens = new LenteRequestDTO(1L, 1L, null, BigDecimal.ZERO, Arrays.asList(1L, null), null);
        assertFalse(factory.getValidator().validate(lens).isEmpty());
    }

    @Test void permitsOwnFrameAndRejectsInvalidIdentifiersAndSubtotal() {
        var lens = new LenteRequestDTO(1L, 1L, null, new BigDecimal("380.00"), null, null);
        assertTrue(factory.getValidator().validate(lens).isEmpty());
        lens = new LenteRequestDTO(lens.idReceta(), lens.idMaterialCristal(), 0L, new BigDecimal("10000000000.00"), lens.tratamientosCristalIds(), lens.medidasIds());
        assertEquals(2, factory.getValidator().validate(lens).size());
    }

    @Test void validatesPatientIdentityAndState() {
        var request = new PacienteRequestDTO(0L, null, 2, -1L);
        assertEquals(3, factory.getValidator().validate(request).size());
    }
}


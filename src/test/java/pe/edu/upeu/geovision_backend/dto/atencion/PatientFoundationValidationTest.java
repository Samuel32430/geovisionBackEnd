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
    static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    @AfterAll static void close() { factory.close(); }

    MedidaRequestDTO validMeasure() {
        return MedidaRequestDTO.builder()
                .distanciaPupilar(new BigDecimal("62.50"))
                .ojoDerecho(MedidaOjoDTO.builder().esfera(new BigDecimal("-1.25")).eje(90).build())
                .ojoIzquierdo(MedidaOjoDTO.builder().esfera(new BigDecimal("1.50")).eje(85).build())
                .build();
    }

    @Test void validClinicalInputUsesLejosByDefaultAndAllowsTextVisualAcuity() {
        var request = validMeasure();
        request.getOjoDerecho().setAgudezaVisual("20/20");
        request.setPrisma(BigDecimal.ZERO);
        assertEquals(TipoMedidaEnum.LEJOS, request.getTipoMedida());
        assertTrue(factory.getValidator().validate(request).isEmpty());
    }

    @ParameterizedTest
    @CsvSource({"0,0", "-1,0", "1000,0", "62.501,0", "62.50,-0.01", "62.50,1000", "62.50,0.001"})
    void rejectsNonpositiveDistanceAndValuesOutsideDdlPrecision(String distance, String prism) {
        var request = validMeasure();
        request.setDistanciaPupilar(new BigDecimal(distance));
        request.setPrisma(new BigDecimal(prism));
        assertFalse(factory.getValidator().validate(request).isEmpty());
    }

    @Test void rejectsNullTypeAndMissingEyeWithoutPuttingValidationOnEntities() {
        var request = validMeasure();
        request.setTipoMedida(null);
        request.setOjoIzquierdo(null);
        assertEquals(2, factory.getValidator().validate(request).size());
    }

    @ParameterizedTest
    @CsvSource({"1000,0,0", "-1000,0,0", "0,0.001,0", "0,0,100", "0,0,0.001"})
    void rejectsOpticalValuesOutsideNumericPrecision(String sphere, String cylinder, String addition) {
        var eye = MedidaOjoDTO.builder().esfera(new BigDecimal(sphere))
                .cilindro(new BigDecimal(cylinder)).adicion(new BigDecimal(addition)).build();
        assertFalse(factory.getValidator().validate(eye).isEmpty());
    }

    @Test void validatesNullItemsInPrescriptionAndLensLists() {
        var prescription = RecetaRequestDTO.builder().idPaciente(1L).idEmpleado(2L)
                .medidas(Arrays.asList((MedidaRequestDTO) null)).build();
        assertFalse(factory.getValidator().validate(prescription).isEmpty());
        var lens = LenteRequestDTO.builder().idReceta(1L).idMaterialCristal(1L)
                .subTotal(BigDecimal.ZERO).tratamientosCristalIds(Arrays.asList(1L, null)).build();
        assertFalse(factory.getValidator().validate(lens).isEmpty());
    }

    @Test void permitsOwnFrameAndRejectsInvalidIdentifiersAndSubtotal() {
        var lens = LenteRequestDTO.builder().idReceta(1L).idMaterialCristal(1L)
                .subTotal(new BigDecimal("380.00")).build();
        assertTrue(factory.getValidator().validate(lens).isEmpty());
        lens.setIdProducto(0L);
        lens.setSubTotal(new BigDecimal("10000000000.00"));
        assertEquals(2, factory.getValidator().validate(lens).size());
    }

    @Test void validatesPatientIdentityAndState() {
        var request = PacienteRequestDTO.builder().idPersona(0L).idUsuario(-1L).estado(2).build();
        assertEquals(3, factory.getValidator().validate(request).size());
    }
}


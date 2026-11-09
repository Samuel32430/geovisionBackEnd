package pe.edu.upeu.geovision_backend.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pe.edu.upeu.geovision_backend.dto.atencion.MedidaRequestDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.PersonaResponseDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.RecetaResponseDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.CategoriaDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.MonturaDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoResponseDTO;
import pe.edu.upeu.geovision_backend.dto.security.PersonaUsuarioResumenDTO;
import pe.edu.upeu.geovision_backend.dto.security.UsuarioRequestDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.ProductoVendidoDTO;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoDocumentoEnum;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DtoRecordJsonContractTest {

    @FunctionalInterface
    interface Reader {
        <T> T read(String json, Class<T> type) throws Exception;
    }

    @FunctionalInterface
    interface Writer {
        String write(Object value) throws Exception;
    }

    record Codec(String name, Reader reader, Writer writer) {
        Map<?, ?> properties(Object value) throws Exception {
            return reader.read(writer.write(value), Map.class);
        }

        @Override public String toString() { return name; }
    }

    // >>>> HELPERS
    static Stream<Codec> codecs() {
        var jackson2 = new ObjectMapper();
        var jackson3 = JsonMapper.builder().build();
        return Stream.of(
                new Codec("Jackson 2", jackson2::readValue, jackson2::writeValueAsString),
                new Codec("Jackson 3", jackson3::readValue, jackson3::writeValueAsString)
        );
    }

    // >>>> TESTS
    @ParameterizedTest
    @MethodSource("codecs")
    void omittedAndExplicitNullFieldsUseStandardRecordDeserialization(Codec codec) throws Exception {
        var category = codec.reader.read("{\"nombre\":\"Categoria\"}", CategoriaDTO.class);
        var explicitNull = codec.reader.read("{\"nombre\":\"Categoria\",\"estado\":null}", CategoriaDTO.class);
        assertThat(category.estado()).isNull();
        assertThat(explicitNull.estado()).isNull();
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            assertThat(factory.getValidator().validate(category))
                    .anyMatch(error -> error.getPropertyPath().toString().equals("estado"));
            assertThat(factory.getValidator().validate(explicitNull))
                    .anyMatch(error -> error.getPropertyPath().toString().equals("estado"));
        }

        assertThat(codec.reader.read("{}", MedidaRequestDTO.class).tipoMedida()).isNull();
        assertThat(codec.reader.read("{\"tipoMedida\":null}", MedidaRequestDTO.class).tipoMedida()).isNull();
    }

    @ParameterizedTest
    @MethodSource("codecs")
    void computedPropertiesKeepTheirJsonNamesAndValues(Codec codec) throws Exception {
        assertThat(codec.properties(new PersonaResponseDTO(null, "Ana", "Perez", null, null, null, null, null, null, null, null, null))
                .get("nombreCompleto")
        ).isEqualTo("Ana Perez");
        assertThat(codec.properties(new RecetaResponseDTO(null, null, 1, null, null, null, null, null, null, null, null))
                .get("esRecetaExterna")
        ).isEqualTo(true);
        assertThat(codec.properties(new ProductoResponseDTO(null, null, null, null, null, null, null, null, null, null, null, null, new MonturaDTO(null, null, null, null, null, null, null, null, null, null), null))
                .get("esMontura")
        ).isEqualTo(true);
        var item = new ProductoVendidoDTO(null, null, null, null, 2, new BigDecimal("7.50"), null);
        assertThat(new BigDecimal(codec.properties(item).get("subTotal").toString()))
                .isEqualByComparingTo("15.00");
    }

    @ParameterizedTest
    @MethodSource("codecs")
    void employeeSelectionProjectionPreservesNumericAndComputedFields(Codec codec) throws Exception {
        var projection = new PersonaUsuarioResumenDTO(1L, "Ana", "Perez", TipoDocumentoEnum.DNI,
                "12345678", 2L, "ana", 1, true, true
        );
        var properties = codec.properties(projection);
        assertThat(properties.get("estadoUsuario")).isEqualTo(1);
        assertThat(properties.get("nombreCompleto")).isEqualTo("Ana Perez");
        assertThat(properties.get("tieneUsuario")).isEqualTo(true);
        assertThat(properties.get("esEmpleado")).isEqualTo(true);
    }

    @Test
    void passwordExclusionIsPreservedByRecordToString() {
        var request = new UsuarioRequestDTO("ana", "test-secret-not-for-logs", null, 1, null, null, null);
        assertThat(request.toString()).contains("<redacted>").doesNotContain("test-secret-not-for-logs");
    }

    @ParameterizedTest
    @MethodSource("codecs")
    void mainIdentityUsesIdAndReferencesKeepTheirNames(Codec codec) throws Exception {
        var category = codec.reader.read("{\"id\":7,\"nombre\":\"Categoria\"}", CategoriaDTO.class);
        assertThat(category.id()).isEqualTo(7L);
        assertThat(codec.properties(category).containsKey("id")).isTrue();
        assertThat(codec.properties(category).containsKey("idCategoria")).isFalse();
        var item = new ProductoVendidoDTO(3L, 9L, null, null, 2, BigDecimal.TEN, null);
        var fields = codec.properties(item);
        assertThat(((Number) fields.get("id")).longValue()).isEqualTo(3L);
        assertThat(((Number) fields.get("idProducto")).longValue()).isEqualTo(9L);
        assertThat(fields.containsKey("idProductoVendido")).isFalse();
    }
}

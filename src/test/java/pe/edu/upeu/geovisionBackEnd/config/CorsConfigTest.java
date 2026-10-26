package pe.edu.upeu.geovisionBackEnd.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - CorsConfig")
class CorsConfigTest {

    private CorsConfig corsConfig;

    @BeforeEach
    void setUp() {
        String[] origenesPorDefecto = new String[]{
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost:4200",
                "http://localhost:3000"
        };
        corsConfig = new CorsConfig(origenesPorDefecto);
    }

    @Test
    @DisplayName("Debe registrar y retornar configuración CORS para rutas /api/**")
    void corsConfigurationSource_DebeRetornarConfiguracionParaRutaApi() {
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();
        assertNotNull(source, "El CorsConfigurationSource no debe ser nulo");

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/ventas/pagos-pendientes");
        CorsConfiguration config = source.getCorsConfiguration(request);

        assertNotNull(config, "Debe existir configuración CORS para endpoints bajo /api/**");
    }

    @Test
    @DisplayName("Debe retornar null para rutas que no coincidan con /api/**")
    void corsConfigurationSource_DebeRetornarNullParaRutaNoApi() {
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/no-api/endpoint");
        CorsConfiguration config = source.getCorsConfiguration(request);

        assertNull(config, "No debe aplicar configuración CORS para rutas fuera de /api/**");
    }

    @Test
    @DisplayName("Debe permitir los orígenes configurados por defecto")
    void corsConfiguration_DebeContenerOrigenesPorDefecto() {
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/test");
        CorsConfiguration config = source.getCorsConfiguration(request);

        assertNotNull(config);
        List<String> allowedOrigins = config.getAllowedOrigins();
        assertNotNull(allowedOrigins);
        assertTrue(allowedOrigins.contains("http://localhost:5173"));
        assertTrue(allowedOrigins.contains("http://127.0.0.1:5173"));
        assertTrue(allowedOrigins.contains("http://localhost:4200"));
        assertTrue(allowedOrigins.contains("http://localhost:3000"));
    }

    @Test
    @DisplayName("Debe permitir los métodos HTTP estándar (GET, POST, PUT, PATCH, DELETE, OPTIONS)")
    void corsConfiguration_DebePermitirMetodosHttpEsperados() {
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/api/v1/test");
        CorsConfiguration config = source.getCorsConfiguration(request);

        assertNotNull(config);
        List<String> allowedMethods = config.getAllowedMethods();
        assertNotNull(allowedMethods);
        assertTrue(allowedMethods.containsAll(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")));
    }

    @Test
    @DisplayName("Debe permitir las cabeceras requeridas (Content-Type, Accept, Authorization, Origin)")
    void corsConfiguration_DebePermitirCabecerasEsperadas() {
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/test");
        CorsConfiguration config = source.getCorsConfiguration(request);

        assertNotNull(config);
        List<String> allowedHeaders = config.getAllowedHeaders();
        assertNotNull(allowedHeaders);
        assertTrue(allowedHeaders.containsAll(List.of("Content-Type", "Accept", "Authorization", "Origin")));
    }

    @Test
    @DisplayName("Debe exponer la cabecera Location en la respuesta")
    void corsConfiguration_DebeExponerCabeceraLocation() {
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/test");
        CorsConfiguration config = source.getCorsConfiguration(request);

        assertNotNull(config);
        List<String> exposedHeaders = config.getExposedHeaders();
        assertNotNull(exposedHeaders);
        assertTrue(exposedHeaders.contains("Location"));
    }

    @Test
    @DisplayName("Debe habilitar allowCredentials y definir maxAge en 3600 segundos")
    void corsConfiguration_DebePermitirCredencialesYMaxAge() {
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/test");
        CorsConfiguration config = source.getCorsConfiguration(request);

        assertNotNull(config);
        assertEquals(Boolean.TRUE, config.getAllowCredentials());
        assertEquals(3600L, config.getMaxAge());
    }

    @Test
    @DisplayName("Debe soportar orígenes personalizados en el constructor")
    void corsConfiguration_DebeSoportarOrigenesPersonalizados() {
        CorsConfig customConfig = new CorsConfig(new String[]{"https://mi-dominio.com", "https://app.geovision.pe"});
        assertEquals(2, customConfig.getOrigenesPermitidos().size());
        assertTrue(customConfig.getOrigenesPermitidos().contains("https://mi-dominio.com"));
        assertTrue(customConfig.getOrigenesPermitidos().contains("https://app.geovision.pe"));

        CorsConfigurationSource source = customConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/test");
        CorsConfiguration config = source.getCorsConfiguration(request);

        assertNotNull(config);
        assertTrue(config.getAllowedOrigins().contains("https://mi-dominio.com"));
        assertTrue(config.getAllowedOrigins().contains("https://app.geovision.pe"));
        assertFalse(config.getAllowedOrigins().contains("http://localhost:4200"));
    }
}

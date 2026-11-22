package pe.edu.upeu.geovision_backend.controller.gestion_mantenimiento;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.MantenimientoLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:categoriasredis;MODE=Oracle;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
@ActiveProfiles({"test", "redis"})
@EnabledIfEnvironmentVariable(named = "RUN_REDIS_TESTS", matches = "true")
class CategoriasRedisIntegrationTest {
    private static final String API = "/api/v1/mantenimiento/categorias";
    private static final String PREFIJO = "geovision:test:" + UUID.randomUUID() + ":";
    private static final String CLAVE = PREFIJO + "categorias::todos";

    @Autowired MockMvc mvc;
    @Autowired StringRedisTemplate redis;
    @Autowired RedisCacheManager cacheManager;
    // El bloqueo del catálogo usa sintaxis Oracle. Los datos de esta prueba viven en H2.
    @MockitoBean MantenimientoLock lock;

    @DynamicPropertySource
    static void aislarClaves(DynamicPropertyRegistry propiedades) {
        propiedades.add("app.cache.prefijo", () -> PREFIJO);
    }

    @AfterEach
    void limpiarCache() {
        cacheManager.getCache("categorias").clear();
    }

    @Test
    @WithMockUser(authorities = "MANTENIMIENTO_GESTIONAR")
    void reutilizaLaPaginaYLaInvalidaAlCrearYActualizar() throws Exception {
        mvc.perform(get(API).param("termino", "")).andExpect(status().isOk())
                .andExpect(jsonPath("$.empty").value(true));
        mvc.perform(get(API)).andExpect(status().isOk()).andExpect(jsonPath("$.empty").value(true));
        var cache = (RedisCache) cacheManager.getCache("categorias");
        assertThat(cache.getStatistics().getHits()).isEqualTo(1);
        assertThat(redis.getExpire(CLAVE, TimeUnit.SECONDS)).isBetween(1L, 600L);
        mvc.perform(get(API).with(user("sin-permiso"))).andExpect(status().isForbidden());
        mvc.perform(get("/actuator/metrics/cache.gets")).andExpect(status().isForbidden());
        mvc.perform(get("/actuator/metrics/cache.gets").param("tag", "name:categorias", "result:hit")
                        .with(user("admin-prueba").roles("ADMINISTRADOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.measurements[0].value").value(1.0));

        mvc.perform(post(API).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        assertThat(redis.hasKey(CLAVE)).isTrue();

        var creada = mvc.perform(post(API).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Monturas de prueba","estado":1}
                                """))
                .andExpect(status().isCreated()).andReturn().getResponse();
        assertThat(redis.hasKey(CLAVE)).isFalse();
        mvc.perform(get(API)).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombre").value("Monturas de prueba"));

        mvc.perform(put(creada.getHeader("Location")).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Monturas actualizadas","estado":0}
                                """))
                .andExpect(status().isOk());
        assertThat(redis.hasKey(CLAVE)).isFalse();
        mvc.perform(get(API).param("estado", "1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.empty").value(true));
        mvc.perform(get(API)).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombre").value("Monturas actualizadas"));
    }
}

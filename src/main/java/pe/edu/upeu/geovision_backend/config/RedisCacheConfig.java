package pe.edu.upeu.geovision_backend.config;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import pe.edu.upeu.geovision_backend.dto.PaginaResponseDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.CategoriaDTO;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@Profile("redis")
@EnableCaching
public class RedisCacheConfig implements CachingConfigurer {

    public static final String CATEGORIAS = "categorias";
    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheConfig.class);

    @Bean
    RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, JsonMapper mapper,
                                   @Value("${app.cache.categorias-ttl}") Duration ttl,
                                   @Value("${app.cache.prefijo}") String prefijo) {
        var tipo = mapper.getTypeFactory().constructParametricType(PaginaResponseDTO.class, CategoriaDTO.class);
        var serializer = new JacksonJsonRedisSerializer<PaginaResponseDTO<CategoriaDTO>>(mapper, tipo);
        var categorias = RedisCacheConfiguration.defaultCacheConfig()
                .prefixCacheNameWith(prefijo)
                .entryTtl(ttl)
                .disableCachingNullValues()
                .serializeValuesWith(SerializationPair.fromSerializer(serializer));
        var writer = RedisCacheWriter.create(connectionFactory,
                config -> config.immediateWrites()
                        .batchStrategy(BatchStrategies.scan(100))
                        .enableLocking(lock -> lock.sleepTime(Duration.ofMillis(25))
                                .lockTimeout(RedisCacheWriter.TtlFunction.just(Duration.ofSeconds(10)))));
        return RedisCacheManager.builder(writer)
                .withCacheConfiguration(CATEGORIAS, categorias)
                .disableCreateOnMissingCache()
                .enableStatistics()
                .build();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException error, Cache cache, Object key) {
                registrarFallo("leer", cache, error);
            }

            @Override
            public void handleCachePutError(RuntimeException error, Cache cache, Object key, Object value) {
                registrarFallo("guardar", cache, error);
            }

            @Override
            public void handleCacheEvictError(RuntimeException error, Cache cache, Object key) {
                registrarFallo("invalidar", cache, error);
            }

            @Override
            public void handleCacheClearError(RuntimeException error, Cache cache) {
                registrarFallo("invalidar", cache, error);
            }
        };
    }

    private static void registrarFallo(String operacion, Cache cache, RuntimeException error) {
        LOG.warn("No se pudo {} la caché {} ({})", operacion, cache.getName(), error.getClass().getSimpleName());
    }
}

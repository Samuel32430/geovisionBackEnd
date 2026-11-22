# Caché Redis de categorías

El perfil `redis` guarda en Redis la primera página de diez categorías sin término de búsqueda. El filtro de estado tiene entradas separadas para todas, activas e inactivas. Las búsquedas, las demás páginas, los detalles de categoría, los precios y el stock consultan el origen directamente.

Las altas y ediciones mediante la API invalidan las páginas después de que el servicio confirme la escritura. Redis guarda JSON con un TTL inicial de diez minutos. Si Redis falla, la lectura pasa a la base de datos y el backend registra un aviso. Si una invalidación falla durante una interrupción y Redis conserva las entradas, estas pueden seguir vigentes hasta que venza su TTL.

## Ejecución local

Con Oracle y su esquema de desarrollo ya configurados:

```powershell
docker compose -f compose.redis.yaml up -d --wait
$env:SPRING_PROFILES_ACTIVE = 'dev,local,redis'
.\mvnw.cmd spring-boot:run
```

`REDIS_HOST` y `REDIS_PORT` apuntan a `127.0.0.1:6379` por defecto. Se pueden configurar `REDIS_PASSWORD`, `REDIS_SSL_ENABLED`, `REDIS_CACHE_TTL` y `REDIS_CACHE_PREFIX`. Usa un prefijo distinto para cada entorno que comparta una instancia de Redis.

## Verificación

1. Con un usuario autorizado para mantenimiento, llama dos veces a `GET /api/v1/mantenimiento/categorias?page=0&size=10`. Consulta `GET /actuator/metrics/cache.gets?tag=name:categorias&tag=result:hit` con una cuenta de administración.
2. Comprueba la vigencia con `docker compose -f compose.redis.yaml exec redis redis-cli TTL geovision:v1:categorias::todos`. Puedes repetir la prueba con `REDIS_CACHE_TTL=1m` y un prefijo de prueba para observar la expiración.
3. En un entorno de prueba, crea y edita una categoría desde Mantenimiento y vuelve a listar. El nombre y el estado nuevos deben verse en la siguiente consulta.
4. Detén el Redis local con `docker compose -f compose.redis.yaml stop redis` y repite el listado. Oracle debe responder si está disponible. Restablece Redis con `docker compose -f compose.redis.yaml start redis`.

La prueba automatizada utiliza Redis real y una base H2 aislada. Comprueba aciertos, TTL, permisos e invalidación tras crear y editar.

```powershell
$env:RUN_REDIS_TESTS = 'true'
.\mvnw.cmd '-Dtest=CategoriasRedisIntegrationTest' test
```

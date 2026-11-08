package pe.edu.upeu.geovision_backend.repository.gestion_configuracion;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Serializa escrituras incluso cuando la tabla todavía está vacía. */
@Repository
@RequiredArgsConstructor
public class ConfiguracionEmpresaLock {
    private final JdbcTemplate jdbcTemplate;

    public void bloquear() {
        // Oracle mantiene el bloqueo hasta commit/rollback de la transacción del servicio.
        jdbcTemplate.execute("LOCK TABLE CONFIGURACION_EMPRESA IN EXCLUSIVE MODE WAIT 10");
    }
}

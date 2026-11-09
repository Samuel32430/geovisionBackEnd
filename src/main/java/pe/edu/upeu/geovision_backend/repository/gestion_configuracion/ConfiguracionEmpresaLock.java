package pe.edu.upeu.geovision_backend.repository.gestion_configuracion;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConfiguracionEmpresaLock {
    private final JdbcTemplate jdbcTemplate;

    public void bloquear() {

        jdbcTemplate.execute("LOCK TABLE CONFIGURACION_EMPRESA IN EXCLUSIVE MODE WAIT 10");
    }
}

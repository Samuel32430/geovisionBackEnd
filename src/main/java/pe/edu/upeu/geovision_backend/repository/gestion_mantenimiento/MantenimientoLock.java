package pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Bloqueos Oracle transaccionales; nunca se interpolan nombres recibidos del cliente. */
@Repository
@RequiredArgsConstructor
public class MantenimientoLock {
    public enum Catalogo { CATEGORIA, MATERIAL_CRISTAL, TRATAMIENTO_CRISTAL, SERVICIO, LABORATORIO }

    private final JdbcTemplate jdbcTemplate;

    public void bloquear(Catalogo catalogo, boolean desactivando) {
        // Bloquear dependencias antes de comprobarlas evita que cambien durante la decisión.
        if (desactivando) {
            switch (catalogo) {
                case CATEGORIA -> bloquearTabla("PRODUCTO", "SHARE");
                case SERVICIO -> {
                    bloquearTabla("VENTA", "SHARE");
                    bloquearTabla("SERVICIO_VENDIDO", "SHARE");
                }
                case LABORATORIO -> bloquearTabla("ENVIO_LABORATORIO", "SHARE");
                default -> { }
            }
        }
        bloquearTabla(catalogo.name(), "EXCLUSIVE");
    }

    private void bloquearTabla(String tabla, String modo) {
        jdbcTemplate.execute("LOCK TABLE " + tabla + " IN " + modo + " MODE WAIT 10");
    }
}

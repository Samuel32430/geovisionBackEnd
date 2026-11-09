package pe.edu.upeu.geovision_backend.repository.inventario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.inventario.MovimientoStockLog;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoStockLogRepository extends JpaRepository<MovimientoStockLog, Long> {

    // >>>> QUERIES
    Page<MovimientoStockLog> findByProductoIdOrderByFechaRegistroDesc(Long idProducto, Pageable pageable);

    List<MovimientoStockLog> findByProductoIdOrderByFechaRegistroDesc(Long idProducto);

    Page<MovimientoStockLog> findByFechaRegistroBetweenOrderByFechaRegistroDesc(
        LocalDateTime desde,
        LocalDateTime hasta,
        Pageable pageable
    );

}

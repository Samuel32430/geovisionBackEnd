package pe.edu.upeu.geovisionBackEnd.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.MovimientoStockLog;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoStockLogRepository extends JpaRepository<MovimientoStockLog, Long> {

    Page<MovimientoStockLog> findByProductoIdProductoOrderByFechaRegistroDesc(Long idProducto, Pageable pageable);

    List<MovimientoStockLog> findByProductoIdProductoOrderByFechaRegistroDesc(Long idProducto);

    Page<MovimientoStockLog> findByFechaRegistroBetweenOrderByFechaRegistroDesc(
            LocalDateTime desde,
            LocalDateTime hasta,
            Pageable pageable
    );
}

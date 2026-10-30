package pe.edu.upeu.geovisionBackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.Pago;
import pe.edu.upeu.geovisionBackEnd.enums.MetodoPagoEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByVentaIdVenta(Long idVenta);

    List<Pago> findByMetodoPago(MetodoPagoEnum metodoPago);

    Optional<Pago> findByNumeroOperacion(String numeroOperacion);

    boolean existsByNumeroOperacion(String numeroOperacion);

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE p.venta.idVenta = :idVenta")
    BigDecimal sumMontoPagadoByVentaId(@Param("idVenta") Long idVenta);

    @Query("SELECT p FROM Pago p WHERE p.fechaRegistro BETWEEN :desde AND :hasta ORDER BY p.fechaRegistro DESC")
    List<Pago> findPagosPorRangoFecha(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}

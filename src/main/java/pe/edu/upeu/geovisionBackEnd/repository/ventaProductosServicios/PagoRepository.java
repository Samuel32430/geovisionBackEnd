package pe.edu.upeu.geovisionBackEnd.repository.ventaProductosServicios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.Pago;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByComprobante_IdComprobante(Long idComprobante);

    List<Pago> findByComprobante_IdComprobanteAndAnulado(Long idComprobante, Integer anulado);

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p " +
           "WHERE p.comprobante.idComprobante = :idComprobante AND p.anulado = 0")
    BigDecimal sumMontoPagadoByComprobanteId(@Param("idComprobante") Long idComprobante);
}

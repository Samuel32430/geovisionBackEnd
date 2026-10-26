package pe.edu.upeu.geovisionBackEnd.repository.ventaProductosServicios;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.Comprobante;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {

    Optional<Comprobante> findByVenta_IdVenta(Long idVenta);

    /** Serializa calculo de saldo y registro de cobros dentro de una transaccion. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Comprobante c where c.idComprobante = :id")
    Optional<Comprobante> findByIdForUpdate(@Param("id") Long id);


    boolean existsByTipoComprobante_IdTipoComprobanteAndSerieAndNumero(Long idTipo, String serie, String numero);

    List<Comprobante> findByEstadoCobro(String estadoCobro);

    @Query("SELECT c FROM Comprobante c " +
           "JOIN FETCH c.venta v " +
           "WHERE c.estadoCobro IN ('PENDIENTE', 'CON_ADELANTO') " +
           "AND c.estadoDocumento = 'EMITIDO' " +
           "ORDER BY c.fechaEmision ASC")
    List<Comprobante> findComprobantesConSaldoPendiente();
}

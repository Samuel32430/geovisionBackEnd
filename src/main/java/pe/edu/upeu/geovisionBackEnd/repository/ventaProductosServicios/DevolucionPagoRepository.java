package pe.edu.upeu.geovisionBackEnd.repository.ventaProductosServicios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.DevolucionPago;

import java.util.List;

@Repository
public interface DevolucionPagoRepository extends JpaRepository<DevolucionPago, Long> {

    List<DevolucionPago> findByPago_IdPago(Long idPago);
}

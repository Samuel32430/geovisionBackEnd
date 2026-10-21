package pe.edu.upeu.geovisionBackEnd.repository.ventaProductosServicios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.ServicioVendido;

import java.util.List;

@Repository
public interface ServicioVendidoRepository extends JpaRepository<ServicioVendido, Long> {

    List<ServicioVendido> findByVenta_IdVenta(Long idVenta);
}

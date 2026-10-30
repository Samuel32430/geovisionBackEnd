package pe.edu.upeu.geovisionBackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.ServicioVendido;

import java.util.List;

@Repository
public interface ServicioVendidoRepository extends JpaRepository<ServicioVendido, Long> {

    List<ServicioVendido> findByVentaIdVenta(Long idVenta);

    List<ServicioVendido> findByServicioIdServicio(Long idServicio);
}

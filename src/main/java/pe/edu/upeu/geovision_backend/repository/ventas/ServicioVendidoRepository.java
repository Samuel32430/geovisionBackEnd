package pe.edu.upeu.geovision_backend.repository.ventas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.ventas.ServicioVendido;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;

import java.util.List;

@Repository
public interface ServicioVendidoRepository extends JpaRepository<ServicioVendido, Long> {

    List<ServicioVendido> findByVentaIdVenta(Long idVenta);

    List<ServicioVendido> findByServicioIdServicio(Long idServicio);

    boolean existsByServicioIdServicioAndVentaEstado(Long idServicio, EstadoVentaEnum estado);
}

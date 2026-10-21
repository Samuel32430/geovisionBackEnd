package pe.edu.upeu.geovisionBackEnd.repository.ventaProductosServicios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.ItemVendido;

import java.util.List;

@Repository
public interface ItemVendidoRepository extends JpaRepository<ItemVendido, Long> {

    List<ItemVendido> findByVenta_IdVenta(Long idVenta);
}

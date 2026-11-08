package pe.edu.upeu.geovision_backend.repository.ventas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.ventas.ProductoVendido;

import java.util.List;

@Repository
public interface ProductoVendidoRepository extends JpaRepository<ProductoVendido, Long> {

    List<ProductoVendido> findByVentaId(Long idVenta);

    List<ProductoVendido> findByProductoId(Long idProducto);

    @Query("SELECT COALESCE(SUM(pv.cantidad), 0) FROM ProductoVendido pv WHERE pv.producto.id = :idProducto")
    Long sumCantidadVendidaByProductoId(@Param("idProducto") Long idProducto);
}

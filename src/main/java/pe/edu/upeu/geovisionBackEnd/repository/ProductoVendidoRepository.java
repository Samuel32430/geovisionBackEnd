package pe.edu.upeu.geovisionBackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.ProductoVendido;

import java.util.List;

@Repository
public interface ProductoVendidoRepository extends JpaRepository<ProductoVendido, Long> {

    List<ProductoVendido> findByVentaIdVenta(Long idVenta);

    List<ProductoVendido> findByProductoIdProducto(Long idProducto);

    @Query("SELECT COALESCE(SUM(pv.cantidad), 0) FROM ProductoVendido pv WHERE pv.producto.idProducto = :idProducto")
    Long sumCantidadVendidaByProductoId(@Param("idProducto") Long idProducto);
}

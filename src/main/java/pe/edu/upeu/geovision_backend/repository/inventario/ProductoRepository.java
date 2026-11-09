package pe.edu.upeu.geovision_backend.repository.inventario;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.inventario.Producto;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {

    // >>>> QUERIES
    Optional<Producto> findByCodigo(String codigo);

    List<Producto> findByCategoriaId(Long idCategoria);

    List<Producto> findByActivo(Boolean estado);

    Page<Producto> findByActivoAndStockGreaterThan(Boolean estado, Integer stock, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.stock <= :stockMinimo AND p.activo = true")
    List<Producto> findProductosBajoStock(@Param("stockMinimo") Integer stockMinimo);

    @Query("SELECT p FROM Producto p WHERE (:termino IS NULL " +
           "OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR LOWER(p.marca) LIKE LOWER(CONCAT('%', :termino, '%'))) " +
           "AND (:idCategoria IS NULL OR p.categoria.id = :idCategoria) " +
           "AND (:estado IS NULL OR p.activo = :estado)"
    )
    Page<Producto> buscarProductosPaginado(
        @Param("termino") String termino,
        @Param("idCategoria") Long idCategoria,
        @Param("estado") Boolean estado,
        Pageable pageable
    );

    // >>>> EXISTENCE CHECKS
    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long idProducto);

    boolean existsByCategoriaIdAndActivo(Long idCategoria, Boolean estado);

    // >>>> LOCKING
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.id = :id")
    Optional<Producto> findByIdForUpdate(@Param("id") Long id);

}

package pe.edu.upeu.geovisionBackEnd.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.Producto;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {

    Optional<Producto> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdProductoNot(String codigo, Long idProducto);

    List<Producto> findByCategoriaIdCategoria(Long idCategoria);

    List<Producto> findByEstado(EstadoGeneralEnum estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.idProducto = :id")
    Optional<Producto> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT p FROM Producto p WHERE p.stock <= :stockMinimo AND p.estado = pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum.ACTIVO")
    List<Producto> findProductosBajoStock(@Param("stockMinimo") Integer stockMinimo);

    @Query("SELECT p FROM Producto p WHERE (:termino IS NULL " +
           "OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR LOWER(p.marca) LIKE LOWER(CONCAT('%', :termino, '%'))) " +
           "AND (:idCategoria IS NULL OR p.categoria.idCategoria = :idCategoria) " +
           "AND (:estado IS NULL OR p.estado = :estado)")
    Page<Producto> buscarProductosPaginado(
            @Param("termino") String termino,
            @Param("idCategoria") Long idCategoria,
            @Param("estado") EstadoGeneralEnum estado,
            Pageable pageable
    );
}

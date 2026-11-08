package pe.edu.upeu.geovision_backend.repository.inventario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.inventario.Montura;
import pe.edu.upeu.geovision_backend.enums.inventario.MaterialMonturaEnum;
import pe.edu.upeu.geovision_backend.enums.inventario.ModeloMonturaEnum;
import pe.edu.upeu.geovision_backend.enums.EstadoGeneralEnum;

import java.util.List;

@Repository
public interface MonturaRepository extends JpaRepository<Montura, Long> {

    @EntityGraph(attributePaths = "producto")
    Page<Montura> findByProductoEstadoAndProductoStockGreaterThan(
            EstadoGeneralEnum estado, Integer stockMinimo, Pageable pageable);

    List<Montura> findByModelo(ModeloMonturaEnum modelo);

    List<Montura> findByMaterial(MaterialMonturaEnum material);

    @Query("SELECT m FROM Montura m JOIN FETCH m.producto p WHERE p.estado = pe.edu.upeu.geovision_backend.enums.EstadoGeneralEnum.ACTIVO")
    List<Montura> findAllActivas();

    @Query("SELECT m FROM Montura m JOIN FETCH m.producto p " +
           "WHERE (:modelo IS NULL OR m.modelo = :modelo) " +
           "AND (:material IS NULL OR m.material = :material) " +
           "AND p.estado = pe.edu.upeu.geovision_backend.enums.EstadoGeneralEnum.ACTIVO")
    Page<Montura> buscarMonturasFiltro(
            @Param("modelo") ModeloMonturaEnum modelo,
            @Param("material") MaterialMonturaEnum material,
            Pageable pageable
    );
}

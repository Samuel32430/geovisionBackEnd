package pe.edu.upeu.geovision_backend.repository.atencion;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.atencion.Lente;

import java.util.List;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface LenteRepository extends JpaRepository<Lente, Long> {

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM Lente l WHERE l.idLente = :id")
    Optional<Lente> findByIdForUpdate(@Param("id") Long id);

    List<Lente> findByRecetaIdReceta(Long idReceta);

    @EntityGraph(attributePaths = {"materialCristal", "montura.producto"})
    List<Lente> findByRecetaIdRecetaOrderByIdLenteAsc(Long idReceta);

    @EntityGraph(attributePaths = {"materialCristal", "montura.producto"})
    List<Lente> findByRecetaIdRecetaInOrderByIdLenteAsc(Collection<Long> idsReceta);

    List<Lente> findByMaterialCristalIdMaterialCristal(Long idMaterialCristal);

    List<Lente> findByMonturaIdProducto(Long idProducto);

    @EntityGraph(attributePaths = {"receta", "materialCristal", "montura.producto"})
    @Query("SELECT l FROM Lente l WHERE l.idLente = :id")
    Optional<Lente> findByIdWithDetails(@Param("id") Long id);
}

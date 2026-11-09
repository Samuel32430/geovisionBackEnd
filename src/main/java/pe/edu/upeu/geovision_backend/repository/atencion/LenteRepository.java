package pe.edu.upeu.geovision_backend.repository.atencion;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

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

    // >>>> QUERIES
    List<Lente> findByRecetaId(Long idReceta);

    @EntityGraph(attributePaths = {"materialCristal", "tratamientosCristal", "montura.producto", "medidas.medidaOjoDerecha", "medidas.medidaOjoIzquierda"})
    List<Lente> findByRecetaIdOrderByIdAsc(Long idReceta);

    @EntityGraph(attributePaths = {"materialCristal", "tratamientosCristal", "montura.producto", "medidas.medidaOjoDerecha", "medidas.medidaOjoIzquierda"})
    List<Lente> findByRecetaIdInOrderByIdAsc(Collection<Long> idsReceta);

    List<Lente> findByMaterialCristalId(Long idMaterialCristal);

    List<Lente> findByMonturaId(Long idProducto);

    @EntityGraph(attributePaths = {"receta", "materialCristal", "tratamientosCristal", "montura.producto", "medidas.medidaOjoDerecha", "medidas.medidaOjoIzquierda"})
    @Query("SELECT l FROM Lente l WHERE l.id = :id")
    Optional<Lente> findByIdWithDetails(@Param("id") Long id);

    // >>>> LOCKING
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM Lente l WHERE l.id = :id")
    Optional<Lente> findByIdForUpdate(@Param("id") Long id);

}

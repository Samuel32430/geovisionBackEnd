package pe.edu.upeu.geovisionBackEnd.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.Lente;

import java.util.List;
import java.util.Optional;

@Repository
public interface LenteRepository extends JpaRepository<Lente, Long> {

    List<Lente> findByRecetaIdReceta(Long idReceta);

    List<Lente> findByMaterialCristalIdMaterialCristal(Long idMaterialCristal);

    List<Lente> findByMonturaIdProducto(Long idProducto);

    @EntityGraph(attributePaths = {"receta", "materialCristal", "montura.producto"})
    @Query("SELECT l FROM Lente l WHERE l.idLente = :id")
    Optional<Lente> findByIdWithDetails(@Param("id") Long id);
}

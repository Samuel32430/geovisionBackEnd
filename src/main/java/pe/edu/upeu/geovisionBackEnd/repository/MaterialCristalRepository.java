package pe.edu.upeu.geovisionBackEnd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.MaterialCristal;

import java.util.Optional;

@Repository
public interface MaterialCristalRepository extends JpaRepository<MaterialCristal, Long> {

    Optional<MaterialCristal> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdMaterialCristalNot(String nombre, Long idMaterialCristal);
}

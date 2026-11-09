package pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.MaterialCristal;

import java.util.Optional;

@Repository
public interface MaterialCristalRepository extends JpaRepository<MaterialCristal, Long>, JpaSpecificationExecutor<MaterialCristal> {

    // >>>> QUERIES
    Optional<MaterialCristal> findByNombreIgnoreCase(String nombre);

    // >>>> EXISTENCE CHECKS
    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long idMaterialCristal);

}

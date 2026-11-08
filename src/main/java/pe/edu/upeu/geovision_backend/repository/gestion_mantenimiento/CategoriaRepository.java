package pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.Categoria;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long>, JpaSpecificationExecutor<Categoria> {

    Optional<Categoria> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long idCategoria);

    List<Categoria> findByActivo(Boolean estado);

    Page<Categoria> findByActivo(Boolean estado, Pageable pageable);

    Page<Categoria> findByNombreContainingIgnoreCase(String termino, Pageable pageable);
}

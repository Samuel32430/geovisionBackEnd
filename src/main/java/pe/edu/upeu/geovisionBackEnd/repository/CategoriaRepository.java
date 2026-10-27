package pe.edu.upeu.geovisionBackEnd.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.Categoria;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdCategoriaNot(String nombre, Long idCategoria);

    List<Categoria> findByEstado(EstadoGeneralEnum estado);

    Page<Categoria> findByEstado(EstadoGeneralEnum estado, Pageable pageable);

    Page<Categoria> findByNombreContainingIgnoreCase(String termino, Pageable pageable);
}

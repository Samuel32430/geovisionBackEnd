package pe.edu.upeu.geovisionBackEnd.repository.controlAccesos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Permiso;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    Optional<Permiso> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<Permiso> findByModuloIdModulo(Long idModulo);
}

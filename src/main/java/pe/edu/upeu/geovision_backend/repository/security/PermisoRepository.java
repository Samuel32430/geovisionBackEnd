package pe.edu.upeu.geovision_backend.repository.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.security.Permiso;
import pe.edu.upeu.geovision_backend.enums.security.ModuloEnum;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    // >>>> QUERIES
    Optional<Permiso> findByNombreIgnoreCase(String nombre);

    List<Permiso> findByModulo(ModuloEnum modulo);

    // >>>> EXISTENCE CHECKS
    boolean existsByNombreIgnoreCase(String nombre);

}

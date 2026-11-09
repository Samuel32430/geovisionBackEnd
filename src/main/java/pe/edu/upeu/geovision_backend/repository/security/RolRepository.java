package pe.edu.upeu.geovision_backend.repository.security;

import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.security.Rol;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    // >>>> QUERIES
    Optional<Rol> findByNombreIgnoreCase(String nombre);

    // >>>> EXISTENCE CHECKS
    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long idRol);

    // >>>> LOCKING
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Rol e WHERE e.id = :id")
    Optional<Rol> findByIdForUpdate(@Param("id") Long id);

}

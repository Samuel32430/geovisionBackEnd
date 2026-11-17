package pe.edu.upeu.geovision_backend.repository.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.security.RolPermiso;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolPermisoRepository extends JpaRepository<RolPermiso, Long> {

    // >>>> QUERIES
    List<RolPermiso> findByRolId(Long idRol);

    List<RolPermiso> findByPermisoId(Long idPermiso);

    Optional<RolPermiso> findByRolIdAndPermisoId(Long idRol, Long idPermiso);

    @Query("SELECT rp.permiso.nombre FROM RolPermiso rp WHERE rp.rol.id = :idRol")
    List<String> findNombresPermisosByRolId(@Param("idRol") Long idRol);

    // >>>> EXISTENCE CHECKS
    boolean existsByRolIdAndPermisoId(Long idRol, Long idPermiso);

    // >>>> DELETIONS
    void deleteByRolId(Long idRol);

}

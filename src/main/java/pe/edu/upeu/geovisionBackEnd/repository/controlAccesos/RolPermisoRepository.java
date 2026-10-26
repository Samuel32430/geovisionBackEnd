package pe.edu.upeu.geovisionBackEnd.repository.controlAccesos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.RolPermiso;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolPermisoRepository extends JpaRepository<RolPermiso, Long> {

    List<RolPermiso> findByRolIdRol(Long idRol);

    List<RolPermiso> findByPermisoIdPermiso(Long idPermiso);

    Optional<RolPermiso> findByRolIdRolAndPermisoIdPermiso(Long idRol, Long idPermiso);

    boolean existsByRolIdRolAndPermisoIdPermiso(Long idRol, Long idPermiso);

    void deleteByRolIdRol(Long idRol);

    @Query("SELECT rp.permiso.nombre FROM RolPermiso rp WHERE rp.rol.idRol = :idRol")
    List<String> findNombresPermisosByRolId(@Param("idRol") Long idRol);
}

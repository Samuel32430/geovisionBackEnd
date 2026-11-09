package pe.edu.upeu.geovision_backend.repository.security;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.security.Empleado;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    // >>>> QUERIES
    Optional<Empleado> findByIdAndActivo(Long idPersona, Boolean estado);

    Optional<Empleado> findByUsuarioId(Long idUsuario);

    List<Empleado> findByActivo(Boolean estado);

    @Query("SELECT e FROM Empleado e JOIN FETCH e.persona p WHERE (:estado IS NULL OR e.activo = :estado)")
    List<Empleado> findAllWithPersona(@Param("estado") Boolean estado);

    @Query("SELECT e FROM Empleado e JOIN e.persona p WHERE (:termino IS NULL " +
           "OR LOWER(p.nombres) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR p.numeroDocumento LIKE CONCAT('%', :termino, '%')) " +
           "AND (:estado IS NULL OR e.activo = :estado)"
    )
    Page<Empleado> buscarPaginado(@Param("termino") String termino, @Param("estado") Boolean estado, Pageable pageable);

    // >>>> EXISTENCE CHECKS
    boolean existsByUsuarioId(Long idUsuario);

}

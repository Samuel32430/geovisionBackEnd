package pe.edu.upeu.geovisionBackEnd.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.Empleado;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    Optional<Empleado> findByIdPersonaAndEstado(Long idPersona, EstadoGeneralEnum estado);

    Optional<Empleado> findByUsuarioIdUsuario(Long idUsuario);

    boolean existsByUsuarioIdUsuario(Long idUsuario);

    List<Empleado> findByEstado(EstadoGeneralEnum estado);

    @Query("SELECT e FROM Empleado e JOIN FETCH e.persona p WHERE (:estado IS NULL OR e.estado = :estado)")
    List<Empleado> findAllWithPersona(@Param("estado") EstadoGeneralEnum estado);

    @Query("SELECT e FROM Empleado e JOIN e.persona p WHERE (:termino IS NULL " +
           "OR LOWER(p.nombres) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR p.numeroDocumento LIKE CONCAT('%', :termino, '%')) " +
           "AND (:estado IS NULL OR e.estado = :estado)")
    Page<Empleado> buscarPaginado(@Param("termino") String termino, @Param("estado") EstadoGeneralEnum estado, Pageable pageable);
}

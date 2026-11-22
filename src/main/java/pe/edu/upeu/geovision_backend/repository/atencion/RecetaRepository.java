package pe.edu.upeu.geovision_backend.repository.atencion;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.atencion.Receta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long>, JpaSpecificationExecutor<Receta> {
    @Query("""
            SELECT COUNT(DISTINCT r.paciente.id) FROM Receta r
            WHERE r.esExterno = 0 AND r.fechaRegistro >= :desde AND r.fechaRegistro < :hasta
            """)
    long contarPacientesAtendidos(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("""
            SELECT COUNT(DISTINCT r.paciente.id) FROM Receta r
            WHERE r.esExterno = 0 AND r.fechaRegistro >= :desde AND r.fechaRegistro < :hasta
              AND NOT EXISTS (
                  SELECT 1 FROM Receta anterior
                  WHERE anterior.paciente.id = r.paciente.id AND anterior.esExterno = 0
                    AND anterior.fechaRegistro < :desde
              )
            """)
    long contarPacientesPorPrimeraVez(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @EntityGraph(attributePaths = {"paciente.persona"})
    Page<Receta> findByEsExternoAndFechaRegistroGreaterThanEqualAndFechaRegistroLessThan(
            Integer esExterno, LocalDateTime desde, LocalDateTime hasta, Pageable pageable);

    // >>>> QUERIES
    List<Receta> findByPacienteIdOrderByFechaRegistroDesc(Long idPaciente);

    @EntityGraph(attributePaths = {"paciente.persona", "empleado.persona"})
    Page<Receta> findByPacienteId(Long idPaciente, Pageable pageable);

    @EntityGraph(attributePaths = {"paciente.persona", "empleado.persona"})
    List<Receta> findByPacienteIdOrderByFechaRegistroDescIdDesc(Long idPaciente);

    List<Receta> findByEmpleadoId(Long idEmpleado);

    Page<Receta> findByEsExterno(Integer esExterno, Pageable pageable);

    @EntityGraph(attributePaths = {"paciente.persona", "empleado.persona"})
    @Query("SELECT r FROM Receta r WHERE r.id = :id")
    Optional<Receta> findByIdWithDetails(@Param("id") Long id);

    // >>>> LOCKING
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Receta r WHERE r.id = :id")
    Optional<Receta> findByIdForUpdate(@Param("id") Long id);

}

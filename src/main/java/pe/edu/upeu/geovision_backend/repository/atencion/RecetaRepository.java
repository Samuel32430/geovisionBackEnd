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

import java.util.List;
import java.util.Optional;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long>, JpaSpecificationExecutor<Receta> {

    List<Receta> findByPacienteIdOrderByFechaRegistroDesc(Long idPaciente);

    @EntityGraph(attributePaths = {"paciente.persona", "empleado.persona"})
    Page<Receta> findByPacienteId(Long idPaciente, Pageable pageable);

    @EntityGraph(attributePaths = {"paciente.persona", "empleado.persona"})
    List<Receta> findByPacienteIdOrderByFechaRegistroDescIdDesc(Long idPaciente);

    List<Receta> findByEmpleadoId(Long idEmpleado);

    Page<Receta> findByEsExterno(Integer esExterno, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Receta r WHERE r.id = :id")
    Optional<Receta> findByIdForUpdate(@Param("id") Long id);

    @EntityGraph(attributePaths = {"paciente.persona", "empleado.persona"})
    @Query("SELECT r FROM Receta r WHERE r.id = :id")
    Optional<Receta> findByIdWithDetails(@Param("id") Long id);
}

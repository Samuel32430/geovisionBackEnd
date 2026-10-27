package pe.edu.upeu.geovisionBackEnd.repository;

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
import pe.edu.upeu.geovisionBackEnd.entity.Receta;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long>, JpaSpecificationExecutor<Receta> {

    List<Receta> findByPacienteIdPersonaOrderByFechaRegistroDesc(Long idPaciente);

    Page<Receta> findByPacienteIdPersona(Long idPaciente, Pageable pageable);

    List<Receta> findByEmpleadoIdPersona(Long idEmpleado);

    Page<Receta> findByEsExterno(Integer esExterno, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Receta r WHERE r.idReceta = :id")
    Optional<Receta> findByIdForUpdate(@Param("id") Long id);

    @EntityGraph(attributePaths = {"paciente.persona", "empleado.persona"})
    @Query("SELECT r FROM Receta r WHERE r.idReceta = :id")
    Optional<Receta> findByIdWithDetails(@Param("id") Long id);
}

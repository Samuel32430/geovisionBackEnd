package pe.edu.upeu.geovisionBackEnd.repository.atencionPacientesConsulta;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Receta;
import java.util.List;
import java.util.Optional;

public interface RecetaRepository extends JpaRepository<Receta, Long> {
    Optional<Receta> findByExamenIdExamen(Long examen);
    boolean existsByExamenIdExamen(Long examen);
    Page<Receta> findByExamenPacienteIdPersona(Long paciente, Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Receta r where r.idReceta = :id")
    Optional<Receta> bloquear(@Param("id") Long id);
}


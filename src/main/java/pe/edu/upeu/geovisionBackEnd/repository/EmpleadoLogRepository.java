package pe.edu.upeu.geovisionBackEnd.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.EmpleadoLog;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmpleadoLogRepository extends JpaRepository<EmpleadoLog, Long> {

    List<EmpleadoLog> findByEmpleadoIdPersonaOrderByFechaLogDesc(Long idPersona);

    Page<EmpleadoLog> findByEmpleadoIdPersonaOrderByFechaLogDesc(Long idPersona, Pageable pageable);

    Page<EmpleadoLog> findByFechaLogBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);
}

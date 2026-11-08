package pe.edu.upeu.geovision_backend.repository.security;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.security.EmpleadoLog;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmpleadoLogRepository extends JpaRepository<EmpleadoLog, Long> {

    List<EmpleadoLog> findByEmpleadoIdOrderByFechaLogDesc(Long idPersona);

    Page<EmpleadoLog> findByEmpleadoIdOrderByFechaLogDesc(Long idPersona, Pageable pageable);

    Page<EmpleadoLog> findByFechaLogBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);
}

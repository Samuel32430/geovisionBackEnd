package pe.edu.upeu.geovisionBackEnd.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.PacienteLog;

import java.util.List;

@Repository
public interface PacienteLogRepository extends JpaRepository<PacienteLog, Long> {

    List<PacienteLog> findByPacienteIdPersonaOrderByFechaLogDesc(Long idPersona);

    Page<PacienteLog> findByPacienteIdPersonaOrderByFechaLogDesc(Long idPersona, Pageable pageable);
}

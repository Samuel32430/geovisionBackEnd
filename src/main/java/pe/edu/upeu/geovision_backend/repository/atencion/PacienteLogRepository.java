package pe.edu.upeu.geovision_backend.repository.atencion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.atencion.PacienteLog;

import java.util.List;

@Repository
public interface PacienteLogRepository extends JpaRepository<PacienteLog, Long> {

    // >>>> QUERIES
    List<PacienteLog> findByPacienteIdOrderByFechaLogDesc(Long idPersona);

    Page<PacienteLog> findByPacienteIdOrderByFechaLogDesc(Long idPersona, Pageable pageable);

}

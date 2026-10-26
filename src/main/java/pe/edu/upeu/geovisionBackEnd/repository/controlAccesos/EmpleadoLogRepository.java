package pe.edu.upeu.geovisionBackEnd.repository.controlAccesos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.EmpleadoLog;

import java.util.List;

@Repository
public interface EmpleadoLogRepository extends JpaRepository<EmpleadoLog, Long> {

    List<EmpleadoLog> findByEmpleadoIdPersona(Long idPersona);

    List<EmpleadoLog> findByUsuarioIdUsuario(Long idUsuario);
}

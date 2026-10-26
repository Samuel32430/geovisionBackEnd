package pe.edu.upeu.geovisionBackEnd.repository.controlAccesos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Auditoria;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByEntidadAndIdEntidad(String entidad, Long idEntidad);

    List<Auditoria> findByUsuarioIdUsuario(Long idUsuario);

    List<Auditoria> findByAccion(String accion);

    List<Auditoria> findByFechaLogBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}

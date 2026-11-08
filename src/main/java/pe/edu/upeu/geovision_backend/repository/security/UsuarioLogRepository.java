package pe.edu.upeu.geovision_backend.repository.security;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovision_backend.entity.security.UsuarioLog;
import pe.edu.upeu.geovision_backend.enums.TipoLogEnum;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UsuarioLogRepository extends JpaRepository<UsuarioLog, Long> {

    List<UsuarioLog> findByUsuarioIdOrderByFechaLogDesc(Long idUsuario);

    Page<UsuarioLog> findByUsuarioIdOrderByFechaLogDesc(Long idUsuario, Pageable pageable);

    Page<UsuarioLog> findByTipoLogOrderByFechaLogDesc(TipoLogEnum tipoLog, Pageable pageable);

    Page<UsuarioLog> findByFechaLogBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);
}

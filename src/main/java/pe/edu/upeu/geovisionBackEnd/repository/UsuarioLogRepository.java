package pe.edu.upeu.geovisionBackEnd.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.geovisionBackEnd.entity.UsuarioLog;
import pe.edu.upeu.geovisionBackEnd.enums.TipoLogEnum;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UsuarioLogRepository extends JpaRepository<UsuarioLog, Long> {

    List<UsuarioLog> findByUsuarioIdUsuarioOrderByFechaLogDesc(Long idUsuario);

    Page<UsuarioLog> findByUsuarioIdUsuarioOrderByFechaLogDesc(Long idUsuario, Pageable pageable);

    Page<UsuarioLog> findByTipoLogOrderByFechaLogDesc(TipoLogEnum tipoLog, Pageable pageable);

    Page<UsuarioLog> findByFechaLogBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);
}

package pe.edu.upeu.geovision_backend.service.security.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.entity.security.Usuario;
import pe.edu.upeu.geovision_backend.entity.security.UsuarioLog;
import pe.edu.upeu.geovision_backend.enums.TipoLogEnum;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioLogRepository;
import pe.edu.upeu.geovision_backend.service.security.UsuarioLogService;

import java.time.LocalDateTime;

@Service
public class UsuarioLogServiceImpl implements UsuarioLogService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioLogServiceImpl.class);

    private final UsuarioLogRepository usuarioLogRepository;

    public UsuarioLogServiceImpl(UsuarioLogRepository usuarioLogRepository) {
        this.usuarioLogRepository = usuarioLogRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(Usuario usuario, TipoLogEnum tipoLog) {
        if (usuario == null || tipoLog == null) {
            return;
        }

        try {
            UsuarioLog usuarioLog = new UsuarioLog(null, LocalDateTime.now(), tipoLog, usuario);
            usuarioLogRepository.save(usuarioLog);
            log.debug("Evento de auditoría registrado: usuario={} evento={}", usuario.getNombre(), tipoLog.getDescripcion());
        } catch (Exception ex) {
            log.error("Error al registrar evento de auditoría para usuario {}: {}", usuario.getNombre(), ex.getMessage());
        }
    }
}

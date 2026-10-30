package pe.edu.upeu.geovisionBackEnd.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovisionBackEnd.entity.Usuario;
import pe.edu.upeu.geovisionBackEnd.entity.UsuarioLog;
import pe.edu.upeu.geovisionBackEnd.enums.TipoLogEnum;
import pe.edu.upeu.geovisionBackEnd.repository.UsuarioLogRepository;
import pe.edu.upeu.geovisionBackEnd.service.service.UsuarioLogService;

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
            UsuarioLog usuarioLog = UsuarioLog.builder()
                    .usuario(usuario)
                    .tipoLog(tipoLog)
                    .fechaLog(LocalDateTime.now())
                    .build();
            usuarioLogRepository.save(usuarioLog);
            log.debug("Evento de auditoría registrado: usuario={} evento={}", usuario.getNombre(), tipoLog.getDescripcion());
        } catch (Exception ex) {
            log.error("Error al registrar evento de auditoría para usuario {}: {}", usuario.getNombre(), ex.getMessage());
        }
    }
}

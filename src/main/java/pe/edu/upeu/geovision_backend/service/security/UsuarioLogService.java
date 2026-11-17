package pe.edu.upeu.geovision_backend.service.security;

import pe.edu.upeu.geovision_backend.entity.security.Usuario;
import pe.edu.upeu.geovision_backend.enums.TipoLogEnum;

public interface UsuarioLogService {

    void registrar(Usuario usuario, TipoLogEnum tipoLog);
}

package pe.edu.upeu.geovisionBackEnd.service.service;

import pe.edu.upeu.geovisionBackEnd.entity.Usuario;
import pe.edu.upeu.geovisionBackEnd.enums.TipoLogEnum;

public interface UsuarioLogService {

    void registrar(Usuario usuario, TipoLogEnum tipoLog);
}

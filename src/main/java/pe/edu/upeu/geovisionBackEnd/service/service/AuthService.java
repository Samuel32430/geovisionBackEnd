package pe.edu.upeu.geovisionBackEnd.service.service;

import pe.edu.upeu.geovisionBackEnd.dto.CambioPasswordRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.LoginRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.LoginResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.LogoutResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.RecuperarPasswordRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ResetPasswordRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.UsuarioResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO request);

    LogoutResponseDTO logout(String username);

    UsuarioResponseDTO usuarioActual(String username);

    void cambiarPassword(String username, CambioPasswordRequestDTO request);

    void solicitarRecuperacionPassword(RecuperarPasswordRequestDTO request);

    boolean validarTokenRecuperacion(String token);

    void restablecerPassword(ResetPasswordRequestDTO request);
}

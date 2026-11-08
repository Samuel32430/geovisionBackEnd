package pe.edu.upeu.geovision_backend.service.security;

import pe.edu.upeu.geovision_backend.dto.security.CambioPasswordRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.LoginRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.LoginResponseDTO;
import pe.edu.upeu.geovision_backend.dto.security.LogoutResponseDTO;
import pe.edu.upeu.geovision_backend.dto.security.RecuperarPasswordRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.ResetPasswordRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.UsuarioResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO request);

    LogoutResponseDTO logout(String username);

    UsuarioResponseDTO usuarioActual(String username);

    void cambiarPassword(String username, CambioPasswordRequestDTO request);

    void solicitarRecuperacionPassword(RecuperarPasswordRequestDTO request);

    boolean validarTokenRecuperacion(String token);

    void restablecerPassword(ResetPasswordRequestDTO request);
}

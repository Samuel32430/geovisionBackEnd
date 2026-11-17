package pe.edu.upeu.geovision_backend.service.security;

import pe.edu.upeu.geovision_backend.dto.security.CambioPasswordRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.LoginRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.LoginResponseDTO;
import pe.edu.upeu.geovision_backend.dto.security.LogoutResponseDTO;
import pe.edu.upeu.geovision_backend.dto.security.RecuperarPasswordRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.ResetPasswordRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.UsuarioResponseDTO;

public interface AuthService {

    // >>>> QUERIES
    UsuarioResponseDTO usuarioActual(String username);

    // >>>> AUTHENTICATION
    LoginResponseDTO login(LoginRequestDTO request);

    LogoutResponseDTO logout(String username);

    // >>>> PASSWORD CHANGES
    void cambiarPassword(String username, CambioPasswordRequestDTO request);

    // >>>> PASSWORD RECOVERY
    void solicitarRecuperacionPassword(RecuperarPasswordRequestDTO request);

    boolean validarTokenRecuperacion(String token);

    void restablecerPassword(ResetPasswordRequestDTO request);

}

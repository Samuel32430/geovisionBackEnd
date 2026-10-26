package pe.edu.upeu.geovisionBackEnd.service.service.controlAccesos;

import org.springframework.security.core.Authentication;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.LoginRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.LoginResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.LogoutResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.UsuarioResponseDTO;

/**
 * Contrato de servicio para autenticación y control de accesos (CU-SEG15 y CU-SEG16).
 */
public interface AuthService {

    /**
     * Autentica a un usuario mediante nombre de usuario o correo y contraseña (CU-SEG15).
     * Genera un token JWT y registra el evento de inicio de sesión en auditoría.
     */
    LoginResponseDTO login(LoginRequestDTO request);

    /**
     * Obtiene el perfil y datos del usuario actualmente autenticado.
     */
    UsuarioResponseDTO usuarioActual(String usernameOrEmail);

    /**
     * Cierra la sesión activa del usuario y registra el evento en auditoría (CU-SEG16).
     */
    LogoutResponseDTO logout(Authentication authentication);
}

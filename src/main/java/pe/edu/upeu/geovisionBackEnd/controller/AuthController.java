package pe.edu.upeu.geovisionBackEnd.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.geovisionBackEnd.dto.CambioPasswordRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.LoginRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.LoginResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.LogoutResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.RecuperarPasswordRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ResetPasswordRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.UsuarioResponseDTO;
import pe.edu.upeu.geovisionBackEnd.service.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * CUS15: Iniciar Sesión (RF-84).
     * Endpoint público. Credenciales válidas -> 200 con JWT, roles y permisos.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * CUS16: Cerrar Sesión (RF-85).
     * Endpoint protegido. Registra salida en auditoría de usuario.
     */
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDTO> logout(Authentication authentication) {
        return ResponseEntity.ok(authService.logout(authentication.getName()));
    }

    /**
     * Obtener el perfil del usuario autenticado actual.
     */
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> me(Authentication authentication) {
        return ResponseEntity.ok(authService.usuarioActual(authentication.getName()));
    }

    /**
     * Cambio de contraseña para el usuario autenticado.
     */
    @PutMapping("/password")
    public ResponseEntity<Void> cambiarPassword(
            @Valid @RequestBody CambioPasswordRequestDTO request,
            Authentication authentication) {
        authService.cambiarPassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }

    /**
     * CUS19: Solicitar Recuperación de Contraseña (RF-91, RF-92).
     * Endpoint público. Valida cuenta y despacha token temporal de recuperación.
     */
    @PostMapping("/recuperar-password")
    public ResponseEntity<Map<String, String>> recuperarPassword(
            @Valid @RequestBody RecuperarPasswordRequestDTO request) {
        authService.solicitarRecuperacionPassword(request);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Si el correo corresponde a una cuenta activa, se ha enviado un enlace de recuperación"));
    }

    /**
     * CUS19: Validar Token de Recuperación (RF-93).
     * Endpoint público. Verifica validez y vigencia del token JWT firmado.
     */
    @GetMapping("/validar-token")
    public ResponseEntity<Map<String, Object>> validarToken(@RequestParam String token) {
        boolean valido = authService.validarTokenRecuperacion(token);
        return ResponseEntity.ok(Map.of("valido", valido));
    }

    /**
     * CUS19: Restablecer Contraseña (RF-94, RF-95).
     * Endpoint público. Valida token y actualiza la contraseña del usuario.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO request) {
        authService.restablecerPassword(request);
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña restablecida exitosamente"));
    }
}

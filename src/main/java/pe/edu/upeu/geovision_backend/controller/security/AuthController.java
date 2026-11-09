package pe.edu.upeu.geovision_backend.controller.security;

import lombok.RequiredArgsConstructor;
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
import pe.edu.upeu.geovision_backend.dto.security.CambioPasswordRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.LoginRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.LoginResponseDTO;
import pe.edu.upeu.geovision_backend.dto.security.LogoutResponseDTO;
import pe.edu.upeu.geovision_backend.dto.security.RecuperarPasswordRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.ResetPasswordRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.UsuarioResponseDTO;
import pe.edu.upeu.geovision_backend.service.security.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    // >>>> DEPENDENCIES
    private final AuthService authService;

    // >>>> QUERIES
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> me(Authentication authentication) {
        return ResponseEntity.ok(authService.usuarioActual(authentication.getName()));
    }

    // >>>> AUTHENTICATION
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDTO> logout(Authentication authentication) {
        return ResponseEntity.ok(authService.logout(authentication.getName()));
    }

    // >>>> PASSWORD CHANGES
    @PutMapping("/password")
    public ResponseEntity<Void> cambiarPassword(
        @Valid @RequestBody CambioPasswordRequestDTO request,
        Authentication authentication
    ) {
        authService.cambiarPassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }

    // >>>> PASSWORD RECOVERY
    @PostMapping("/recuperar-password")
    public ResponseEntity<Map<String, String>> recuperarPassword(
        @Valid @RequestBody RecuperarPasswordRequestDTO request
    ) {
        authService.solicitarRecuperacionPassword(request);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Si el correo corresponde a una cuenta activa, se ha enviado un enlace de recuperación"
            )
        );
    }

    @GetMapping("/validar-token")
    public ResponseEntity<Map<String, Object>> validarToken(@RequestParam String token) {
        boolean valido = authService.validarTokenRecuperacion(token);
        return ResponseEntity.ok(Map.of("valido", valido));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
        @Valid @RequestBody ResetPasswordRequestDTO request
    ) {
        authService.restablecerPassword(request);
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña restablecida exitosamente"));
    }

}

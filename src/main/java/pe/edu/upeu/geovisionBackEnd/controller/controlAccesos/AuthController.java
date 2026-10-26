package pe.edu.upeu.geovisionBackEnd.controller.controlAccesos;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.LoginRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.LoginResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.UsuarioResponseDTO;
import pe.edu.upeu.geovisionBackEnd.service.service.controlAccesos.AuthService;

/**
 * Controlador REST para el proceso de autenticación y sesiones (CU-SEG15).
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Endpoints para el inicio y consulta de sesión de usuarios")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint público para iniciar sesión.
     * Retorna token JWT y datos de perfil con roles y permisos asignados.
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario mediante username o correo y contraseña")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Endpoint protegido para consultar el perfil del usuario autenticado.
     */
    @GetMapping("/me")
    @Operation(summary = "Perfil actual", description = "Retorna los datos del usuario dueño del token Bearer enviado")
    public ResponseEntity<UsuarioResponseDTO> me(Authentication authentication) {
        return ResponseEntity.ok(authService.usuarioActual(authentication.getName()));
    }
}

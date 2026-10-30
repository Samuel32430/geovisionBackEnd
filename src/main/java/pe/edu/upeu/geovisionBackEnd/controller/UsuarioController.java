package pe.edu.upeu.geovisionBackEnd.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.geovisionBackEnd.dto.UsuarioRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.UsuarioResponseDTO;
import pe.edu.upeu.geovisionBackEnd.service.service.UsuarioService;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    /**
     * CUS17: Registrar nuevo usuario (RF-86).
     */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(request));
    }

    /**
     * CUS17: Actualizar datos de usuario (RF-87).
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO request) {
        return ResponseEntity.ok(usuarioService.actualizar(id, request));
    }

    /**
     * CUS17: Desactivar o reactivar usuario (RF-88).
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body,
            Authentication authentication) {
        Integer nuevoEstado = body.get("estado");
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El campo 'estado' es obligatorio (1=Activo, 0=Inactivo)");
        }
        return ResponseEntity.ok(usuarioService.cambiarEstado(id, nuevoEstado, authentication.getName()));
    }

    /**
     * CUS18: Asignar roles a un usuario (RF-89).
     */
    @PutMapping("/{id}/roles")
    public ResponseEntity<UsuarioResponseDTO> asignarRoles(
            @PathVariable Long id,
            @RequestBody Set<Long> roleIds,
            Authentication authentication) {
        return ResponseEntity.ok(usuarioService.asignarRoles(id, roleIds, authentication.getName()));
    }
}

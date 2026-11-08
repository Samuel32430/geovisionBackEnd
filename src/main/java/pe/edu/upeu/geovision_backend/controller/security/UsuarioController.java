package pe.edu.upeu.geovision_backend.controller.security;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upeu.geovision_backend.dto.PaginaResponseDTO;
import pe.edu.upeu.geovision_backend.dto.security.PersonaUsuarioResumenDTO;
import pe.edu.upeu.geovision_backend.dto.security.RegistroPersonaUsuarioRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.UsuarioRequestDTO;
import pe.edu.upeu.geovision_backend.dto.security.UsuarioResponseDTO;
import pe.edu.upeu.geovision_backend.service.security.UsuarioService;

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

    @GetMapping("/personas")
    public ResponseEntity<PaginaResponseDTO<PersonaUsuarioResumenDTO>> buscar(
            @RequestParam(defaultValue = "") @Size(max = 150) String termino,
            @RequestParam(defaultValue = "false") boolean sinUsuario,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        if ((long) page * size > Integer.MAX_VALUE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La página solicitada excede el límite permitido");
        }
        var resultado = usuarioService.buscarPersonasParaUsuario(termino, sinUsuario,
                PageRequest.of(page, size, Sort.by("apellidos", "nombres", "id")));
        return ResponseEntity.ok(new PaginaResponseDTO<>(resultado.getContent(), resultado.getNumber(), resultado.getSize(),
                resultado.getTotalElements(), resultado.getTotalPages(), resultado.isFirst(), resultado.isLast(), resultado.isEmpty()));
    }

    @PostMapping("/con-persona")
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody RegistroPersonaUsuarioRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrarConPersona(request));
    }
}

package pe.edu.upeu.geovision_backend.controller.security;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.geovision_backend.dto.security.RolDTO;
import pe.edu.upeu.geovision_backend.service.security.RolService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RolController {

    // >>>> DEPENDENCIES
    private final RolService rolService;

    // >>>> QUERIES
    @GetMapping
    public ResponseEntity<List<RolDTO>> listar() {
        return ResponseEntity.ok(rolService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(rolService.obtenerPorId(id));
    }

    // >>>> CREATION
    @PostMapping
    public ResponseEntity<RolDTO> crear(@Valid @RequestBody RolDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rolService.crear(request));
    }

    // >>>> UPDATES
    @PutMapping("/{id}/permisos")
    public ResponseEntity<RolDTO> asignarPermisos(
        @PathVariable Long id,
        @RequestBody Set<Long> permisoIds
    ) {
        return ResponseEntity.ok(rolService.asignarPermisos(id, permisoIds));
    }

}

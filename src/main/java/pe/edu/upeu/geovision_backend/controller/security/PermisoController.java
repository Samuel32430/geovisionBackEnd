package pe.edu.upeu.geovision_backend.controller.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.geovision_backend.dto.security.PermisoDTO;
import pe.edu.upeu.geovision_backend.enums.security.ModuloEnum;
import pe.edu.upeu.geovision_backend.service.security.PermisoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permisos")
@RequiredArgsConstructor
public class PermisoController {

    // >>>> DEPENDENCIES
    private final PermisoService permisoService;

    // >>>> QUERIES
    @GetMapping
    public ResponseEntity<List<PermisoDTO>> listar() {
        return ResponseEntity.ok(permisoService.listar());
    }

    @GetMapping("/modulo/{modulo}")
    public ResponseEntity<List<PermisoDTO>> listarPorModulo(@PathVariable ModuloEnum modulo) {
        return ResponseEntity.ok(permisoService.listarPorModulo(modulo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermisoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(permisoService.obtenerPorId(id));
    }

}

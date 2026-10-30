package pe.edu.upeu.geovisionBackEnd.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.geovisionBackEnd.dto.PermisoDTO;
import pe.edu.upeu.geovisionBackEnd.enums.ModuloEnum;
import pe.edu.upeu.geovisionBackEnd.service.service.PermisoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permisos")
public class PermisoController {

    private final PermisoService permisoService;

    public PermisoController(PermisoService permisoService) {
        this.permisoService = permisoService;
    }

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

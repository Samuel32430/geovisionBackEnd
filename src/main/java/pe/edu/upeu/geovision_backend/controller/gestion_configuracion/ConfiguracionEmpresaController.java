package pe.edu.upeu.geovision_backend.controller.gestion_configuracion;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.geovision_backend.dto.gestion_configuracion.ConfiguracionEmpresaDTO;
import pe.edu.upeu.geovision_backend.service.gestion_configuracion.ConfiguracionEmpresaService;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/configuracion/empresa")
@PreAuthorize("hasAuthority('MANTENIMIENTO_GESTIONAR')")
@RequiredArgsConstructor
public class ConfiguracionEmpresaController {
    private final ConfiguracionEmpresaService service;

    @GetMapping
    public ResponseEntity<ConfiguracionEmpresaDTO> obtener() {
        return ResponseEntity.ok(service.obtener());
    }

    @PostMapping
    public ResponseEntity<ConfiguracionEmpresaDTO> registrar(@Valid @RequestBody ConfiguracionEmpresaDTO request) {
        return ResponseEntity.created(URI.create("/api/v1/configuracion/empresa")).body(service.registrar(request));
    }

    @PutMapping
    public ResponseEntity<ConfiguracionEmpresaDTO> actualizar(@Valid @RequestBody ConfiguracionEmpresaDTO request) {
        return ResponseEntity.ok(service.actualizar(request));
    }
}

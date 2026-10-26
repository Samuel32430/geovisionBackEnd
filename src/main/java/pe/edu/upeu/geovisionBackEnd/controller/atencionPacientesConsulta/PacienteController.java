package pe.edu.upeu.geovisionBackEnd.controller.atencionPacientesConsulta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta.PacienteService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pacientes")
public class PacienteController {
    private final PacienteService service;
    private final pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta.ExamenService examenes;
    private final pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta.RecetaService recetas;
    @PostMapping
    public ResponseEntity<PacienteDetalleResponseDTO> registrar(@Valid @RequestBody PacienteRegistroRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }

    @PostMapping("/persona-existente")
    public ResponseEntity<PacienteResponseDTO> create(@Valid @RequestBody PacienteRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteDetalleResponseDTO> read(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(service.read(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteDetalleResponseDTO> update(@PathVariable @Positive Long id, @Valid @RequestBody PersonaRequestDTO request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping
    public ResponseEntity<PaginaResponse<PacienteResponseDTO>> listar(
            @RequestParam(required = false) String termino,
            @RequestParam(required = false) @Min(0) @Max(1) Integer estado,
            @RequestParam(defaultValue = "0") @Min(0) int pagina,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int tamanio,
            @RequestParam(defaultValue = "persona.apellidos") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion) {
        return ResponseEntity.ok(service.listar(termino, estado, pagina, tamanio, ordenarPor, direccion));
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<PaginaResponse<ExamenResponseDTO>> historial(
            @PathVariable @Positive Long id, @RequestParam(defaultValue = "0") @Min(0) int pagina,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int tamanio) {
        return ResponseEntity.ok(examenes.listarPorPaciente(id, pagina, tamanio));
    }

    @GetMapping("/{id}/recetas")
    public ResponseEntity<PaginaResponse<RecetaResponseDTO>> recetas(
            @PathVariable @Positive Long id, @RequestParam(defaultValue = "0") @Min(0) int pagina,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int tamanio) {
        return ResponseEntity.ok(recetas.listarPorPaciente(id, pagina, tamanio));
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<PaginaResponse<PacienteLogResponseDTO>> logs(
            @PathVariable @Positive Long id, @RequestParam(defaultValue = "0") @Min(0) int pagina,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int tamanio) {
        return ResponseEntity.ok(service.listarLogs(id, pagina, tamanio));
    }
}


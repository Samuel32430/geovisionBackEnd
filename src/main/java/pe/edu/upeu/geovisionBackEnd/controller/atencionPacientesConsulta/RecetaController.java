package pe.edu.upeu.geovisionBackEnd.controller.atencionPacientesConsulta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta.RecetaService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recetas")
public class RecetaController {
    private final RecetaService service;

    @PostMapping
    public ResponseEntity<RecetaResponseDTO> create(@Valid @RequestBody RecetaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecetaResponseDTO> read(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(service.read(id));
    }

    @GetMapping
    public ResponseEntity<PaginaResponse<RecetaResponseDTO>> listar(
            @RequestParam @Positive Long idPaciente, @RequestParam(defaultValue = "0") @Min(0) int pagina,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int tamanio) {
        return ResponseEntity.ok(service.listarPorPaciente(idPaciente, pagina, tamanio));
    }

    @PatchMapping("/{id}/entrega")
    public ResponseEntity<RecetaResponseDTO> registrarEntrega(
            @PathVariable @Positive Long id, @Valid @RequestBody RecetaEntregaRequestDTO request) {
        return ResponseEntity.ok(service.registrarEntrega(id, request));
    }
}


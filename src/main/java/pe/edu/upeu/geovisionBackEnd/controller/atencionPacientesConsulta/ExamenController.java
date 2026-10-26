package pe.edu.upeu.geovisionBackEnd.controller.atencionPacientesConsulta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta.ExamenService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/examenes")
public class ExamenController {
    private final ExamenService service;

    @PostMapping
    public ResponseEntity<ExamenResponseDTO> create(@Valid @RequestBody ExamenRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamenResponseDTO> update(@PathVariable @Positive Long id, @Valid @RequestBody ExamenRequestDTO request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamenDetalleResponseDTO> read(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(service.read(id));
    }

    @GetMapping
    public ResponseEntity<PaginaResponse<ExamenResponseDTO>> listar(
            @RequestParam @Positive Long idPaciente,
            @RequestParam(defaultValue = "0") @Min(0) int pagina,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int tamanio) {
        return ResponseEntity.ok(service.listarPorPaciente(idPaciente, pagina, tamanio));
    }
}


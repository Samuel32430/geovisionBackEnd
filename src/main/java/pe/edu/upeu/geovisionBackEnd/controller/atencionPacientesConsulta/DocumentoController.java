package pe.edu.upeu.geovisionBackEnd.controller.atencionPacientesConsulta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta.DocumentoService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/documentos")
public class DocumentoController {
    private final DocumentoService service;

    @PostMapping
    public ResponseEntity<DocumentoResponseDTO> create(@Valid @RequestBody DocumentoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentoResponseDTO> update(@PathVariable @Positive Long id, @Valid @RequestBody DocumentoRequestDTO request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentoResponseDTO> read(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(service.read(id));
    }

    @GetMapping
    public ResponseEntity<List<DocumentoResponseDTO>> listar(@RequestParam @Positive Long idPersona) {
        return ResponseEntity.ok(service.listarPorPersona(idPersona));
    }
}


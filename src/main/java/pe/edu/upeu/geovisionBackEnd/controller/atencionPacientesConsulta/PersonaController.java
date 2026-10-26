package pe.edu.upeu.geovisionBackEnd.controller.atencionPacientesConsulta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta.PersonaService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/personas")
public class PersonaController {
    private final PersonaService service;

    @PostMapping
    public ResponseEntity<PersonaResponseDTO> create(@Valid @RequestBody PersonaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaResponseDTO> read(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(service.read(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonaResponseDTO> update(@PathVariable @Positive Long id, @Valid @RequestBody PersonaRequestDTO request) {
        return ResponseEntity.ok(service.update(id, request));
    }
}


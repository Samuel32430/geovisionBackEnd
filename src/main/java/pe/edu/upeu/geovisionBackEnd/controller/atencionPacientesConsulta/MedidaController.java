package pe.edu.upeu.geovisionBackEnd.controller.atencionPacientesConsulta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta.MedidaService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/medidas")
public class MedidaController {
    private final MedidaService service;

    @PostMapping
    public ResponseEntity<MedidaResponseDTO> create(@Valid @RequestBody MedidaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedidaResponseDTO> update(@PathVariable @Positive Long id, @Valid @RequestBody MedidaRequestDTO request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedidaDetalleResponseDTO> read(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(service.read(id));
    }

    @GetMapping
    public ResponseEntity<List<MedidaResponseDTO>> listar(
            @RequestParam @Positive Long idExamen) {
        return ResponseEntity.ok(service.listarPorExamen(idExamen));
    }
}


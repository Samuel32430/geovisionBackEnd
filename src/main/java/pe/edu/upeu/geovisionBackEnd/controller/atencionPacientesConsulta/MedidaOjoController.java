package pe.edu.upeu.geovisionBackEnd.controller.atencionPacientesConsulta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta.MedidaOjoService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/medidas-ojo")
public class MedidaOjoController {
    private final MedidaOjoService service;

    @PostMapping
    public ResponseEntity<MedidaOjoResponseDTO> create(@Valid @RequestBody MedidaOjoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedidaOjoResponseDTO> update(@PathVariable @Positive Long id, @Valid @RequestBody MedidaOjoRequestDTO request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedidaOjoResponseDTO> read(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(service.read(id));
    }

    @GetMapping
    public ResponseEntity<List<MedidaOjoResponseDTO>> listar(
            @RequestParam @Positive Long idMedida) {
        return ResponseEntity.ok(service.listarPorMedida(idMedida));
    }
}


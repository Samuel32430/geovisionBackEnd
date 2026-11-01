package pe.edu.upeu.geovisionBackEnd.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upeu.geovisionBackEnd.dto.LenteRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.LenteResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.RecetaRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.RecetaResponseDTO;
import pe.edu.upeu.geovisionBackEnd.service.service.RecetaService;

@RestController
@RequestMapping("/api/v1/recetas")
public class RecetaController {

    private final RecetaService recetaService;

    public RecetaController(RecetaService recetaService) {
        this.recetaService = recetaService;
    }

    @GetMapping("/{idReceta}")
    @PreAuthorize("hasAuthority('PACIENTES_LEER')")
    public ResponseEntity<RecetaResponseDTO> obtenerRecetaPorId(@PathVariable @Positive Long idReceta) {
        return ResponseEntity.ok(recetaService.obtenerRecetaPorId(idReceta));
    }

    @GetMapping("/paciente/{idPaciente}/historial")
    @PreAuthorize("hasAuthority('PACIENTES_LEER')")
    public ResponseEntity<Page<RecetaResponseDTO>> listarHistorialPorPaciente(
            @PathVariable @Positive Long idPaciente,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        if ((long) page * size > Integer.MAX_VALUE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La página solicitada excede el límite permitido");
        }
        return ResponseEntity.ok(recetaService.listarRecetasPaginadas(idPaciente, PageRequest.of(page, size)));
    }

    @PostMapping("/{idReceta}/lentes")
    @PreAuthorize("hasAuthority('RECETAS_REGISTRAR')")
    public ResponseEntity<LenteResponseDTO> configurarLente(
            @PathVariable @Positive Long idReceta, @Valid @RequestBody LenteRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recetaService.configurarLente(idReceta, request));
    }

    @PostMapping("/examen-visual")
    @PreAuthorize("hasAuthority('EXAMENES_REGISTRAR')")
    public ResponseEntity<RecetaResponseDTO> registrarExamenVisual(
            @Valid @RequestBody RecetaRequestDTO request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recetaService.registrarExamenVisual(request, authentication.getName()));
    }

    @PostMapping("/externa")
    @PreAuthorize("hasAuthority('RECETAS_REGISTRAR')")
    public ResponseEntity<RecetaResponseDTO> registrarRecetaExterna(
            @Valid @RequestBody RecetaRequestDTO request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recetaService.registrarRecetaExterna(request, authentication.getName()));
    }
}

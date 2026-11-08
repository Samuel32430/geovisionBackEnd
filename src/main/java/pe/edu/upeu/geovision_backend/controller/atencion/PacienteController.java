package pe.edu.upeu.geovision_backend.controller.atencion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upeu.geovision_backend.dto.atencion.PacienteRequestDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.PacienteResponseDTO;
import pe.edu.upeu.geovision_backend.service.atencion.PacienteService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PACIENTES_LEER')")
    public ResponseEntity<Page<PacienteResponseDTO>> listarPacientes(
            @RequestParam(required = false) String termino,
            @RequestParam(defaultValue = "1") @Min(0) @Max(1) Integer estado,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        if ((long) page * size > Integer.MAX_VALUE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La página solicitada excede el límite permitido");
        }
        return ResponseEntity.ok(pacienteService.listarPacientes(termino,
                (estado == null ? null : estado == 1), PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PACIENTES_LEER')")
    public ResponseEntity<PacienteResponseDTO> obtenerPorId(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(pacienteService.obtenerPorId(id));
    }

    @GetMapping("/documento/{numeroDocumento}")
    @PreAuthorize("hasAuthority('PACIENTES_LEER')")
    public ResponseEntity<PacienteResponseDTO> obtenerPorNumeroDocumento(@PathVariable String numeroDocumento) {
        return ResponseEntity.ok(pacienteService.obtenerPorNumeroDocumento(numeroDocumento));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PACIENTES_REGISTRAR')")
    public ResponseEntity<PacienteResponseDTO> registrarPaciente(
            @Valid @RequestBody PacienteRequestDTO request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pacienteService.registrarPaciente(request, authentication.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PACIENTES_GESTIONAR')")
    public ResponseEntity<PacienteResponseDTO> actualizarPaciente(
            @PathVariable @Positive Long id, @Valid @RequestBody PacienteRequestDTO request,
            Authentication authentication) {
        return ResponseEntity.ok(pacienteService.actualizarPaciente(id, request, authentication.getName()));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('PACIENTES_GESTIONAR')")
    public ResponseEntity<PacienteResponseDTO> cambiarEstadoPaciente(
            @PathVariable @Positive Long id, @RequestBody Map<String, Integer> body,
            Authentication authentication) {
        Integer estado = body.get("estado");
        if (estado == null || (estado != 0 && estado != 1)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado debe ser 0 o 1");
        }
        return ResponseEntity.ok(pacienteService.cambiarEstadoPaciente(id, estado, authentication.getName()));
    }
}

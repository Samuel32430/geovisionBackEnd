package pe.edu.upeu.geovision_backend.controller.atencion;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ContentDisposition;
import org.springframework.http.CacheControl;
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
import pe.edu.upeu.geovision_backend.dto.atencion.LenteRequestDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.LenteResponseDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.RecetaRequestDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.RecetaResponseDTO;
import pe.edu.upeu.geovision_backend.service.atencion.RecetaService;
import pe.edu.upeu.geovision_backend.service.atencion.RecetaPdfService;

@RestController
@RequestMapping("/api/v1/recetas")
@RequiredArgsConstructor
public class RecetaController {

    private final RecetaService recetaService;
    private final RecetaPdfService recetaPdfService;

    @GetMapping(value = "/{idReceta}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAuthority('PACIENTES_LEER')")
    public ResponseEntity<byte[]> generarRecetaPdf(@PathVariable @Positive Long idReceta) {
        byte[] pdf = recetaPdfService.generarRecetaPdf(idReceta);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length).cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename("receta-" + idReceta + ".pdf").build().toString())
                .body(pdf);
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

package pe.edu.upeu.geovision_backend.controller.orden_trabajo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.*;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.ControlCalidadRequestDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.EnvioLaboratorioResponseDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.OrdenTrabajoResponseDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.PoliticaCalidadDTO;
import pe.edu.upeu.geovision_backend.service.orden_trabajo.ControlCalidadService;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/control-calidad")
@PreAuthorize("hasAuthority('ORDENES_TRABAJO_CALIDAD')")
public class ControlCalidadController {
    private final ControlCalidadService servicio;

    @GetMapping("/pendientes")
    public Page<OrdenTrabajoResponseDTO> pendientes(@RequestParam(defaultValue = "0") @Min(0) int page,
                                                    @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        if ((long) page * size > Integer.MAX_VALUE)
            throw new ReglaNegocioException("La pagina excede el limite permitido");
        return servicio.pendientes(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaRegistro", "id")));
    }

    @GetMapping("/politica")
    public PoliticaCalidadDTO politica() {
        return servicio.politica();
    }

    @PostMapping
    public EnvioLaboratorioResponseDTO registrar(@Valid @RequestBody ControlCalidadRequestDTO request, Authentication auth) {
        return servicio.registrar(request, auth.getName());
    }
}

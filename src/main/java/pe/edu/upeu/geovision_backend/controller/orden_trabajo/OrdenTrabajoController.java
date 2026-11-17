package pe.edu.upeu.geovision_backend.controller.orden_trabajo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.*;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.EntregaPedidoRequestDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.EnvioLaboratorioRequestDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.EnvioLaboratorioResponseDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.LaboratorioDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.OrdenTrabajoRequestDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.OrdenTrabajoResponseDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.PendienteOrdenTrabajoDTO;
import pe.edu.upeu.geovision_backend.dto.orden_trabajo.RecepcionLaboratorioRequestDTO;
import pe.edu.upeu.geovision_backend.enums.orden_trabajo.EstadoOrdenTrabajoEnum;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.service.orden_trabajo.OrdenTrabajoService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ordenes-trabajo")
public class OrdenTrabajoController {

    // >>>> DEPENDENCIES
    private final OrdenTrabajoService servicio;

    // >>>> QUERIES
    @GetMapping("/ventas-pendientes")
    @PreAuthorize("hasAuthority('ORDENES_TRABAJO_LEER')")
    public Page<PendienteOrdenTrabajoDTO> pendientes(
        @RequestParam(required = false) @Size(max = 100) String termino,
        @RequestParam(defaultValue = "0") @Min(0) int page, @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        return servicio.pendientes(termino, pagina(page, size, Sort.unsorted()));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ORDENES_TRABAJO_LEER')")
    public Page<OrdenTrabajoResponseDTO> listar(
        @RequestParam(required = false) EstadoOrdenTrabajoEnum estado,
        @RequestParam(required = false) @Size(max = 100) String termino, @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        return servicio.listar(estado, termino, pagina(page, size, Sort.by(Sort.Direction.DESC, "fechaRegistro", "id")));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ORDENES_TRABAJO_LEER')")
    public OrdenTrabajoResponseDTO obtener(@PathVariable @Positive Long id) {
        return servicio.obtener(id);
    }

    @GetMapping("/laboratorios")
    @PreAuthorize("hasAuthority('ORDENES_TRABAJO_LEER')")
    public List<LaboratorioDTO> laboratorios() {
        return servicio.laboratorios();
    }

    // >>>> CREATION
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ORDENES_TRABAJO_GESTIONAR')")
    public OrdenTrabajoResponseDTO generar(@Valid @RequestBody OrdenTrabajoRequestDTO request, Authentication auth) {
        return servicio.generar(request, auth.getName());
    }

    @PostMapping("/envios")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ORDENES_TRABAJO_GESTIONAR')")
    public EnvioLaboratorioResponseDTO enviar(@Valid @RequestBody EnvioLaboratorioRequestDTO request, Authentication auth) {
        return servicio.enviar(request, auth.getName());
    }

    @PostMapping("/entrega")
    @PreAuthorize("hasAuthority('ORDENES_TRABAJO_GESTIONAR')")
    public OrdenTrabajoResponseDTO entregar(@Valid @RequestBody EntregaPedidoRequestDTO request, Authentication auth) {
        return servicio.entregar(request, auth.getName());
    }

    // >>>> UPDATES
    @PutMapping("/envios/{idEnvio}/recepcion")
    @PreAuthorize("hasAuthority('ORDENES_TRABAJO_GESTIONAR')")
    public EnvioLaboratorioResponseDTO recibir(
        @PathVariable @Positive Long idEnvio,
        @Valid @RequestBody(required = false) RecepcionLaboratorioRequestDTO request, Authentication auth
    ) {
        return servicio.recibir(idEnvio, request, auth.getName());
    }

    // >>>> HELPERS
    private Pageable pagina(int page, int size, Sort sort) {
        if ((long) page * size > Integer.MAX_VALUE)
            throw new ReglaNegocioException("La pagina excede el limite permitido");
        return PageRequest.of(page, size, sort);
    }

}

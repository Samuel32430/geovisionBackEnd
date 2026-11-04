package pe.edu.upeu.geovisionBackEnd.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.*;
import pe.edu.upeu.geovisionBackEnd.dto.*;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoOrdenTrabajoEnum;
import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;
import pe.edu.upeu.geovisionBackEnd.service.service.OrdenTrabajoService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ordenes-trabajo")
public class OrdenTrabajoController {
    private final OrdenTrabajoService servicio;

    @GetMapping("/ventas-pendientes")
    @PreAuthorize("hasAuthority('ORDENES_TRABAJO_LEER')")
    public Page<PendienteOrdenTrabajoDTO> pendientes(@RequestParam(required = false) @Size(max = 100) String termino,
                                                     @RequestParam(defaultValue = "0") @Min(0) int page, @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return servicio.pendientes(termino, pagina(page, size, Sort.unsorted()));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ORDENES_TRABAJO_LEER')")
    public Page<OrdenTrabajoResponseDTO> listar(@RequestParam(required = false) EstadoOrdenTrabajoEnum estado,
                                                @RequestParam(required = false) @Size(max = 100) String termino, @RequestParam(defaultValue = "0") @Min(0) int page,
                                                @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return servicio.listar(estado, termino, pagina(page, size, Sort.by(Sort.Direction.DESC, "fechaRegistro", "idOrdenTrabajo")));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ORDENES_TRABAJO_LEER')")
    public OrdenTrabajoResponseDTO obtener(@PathVariable @Positive Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ORDENES_TRABAJO_GESTIONAR')")
    public OrdenTrabajoResponseDTO generar(@Valid @RequestBody OrdenTrabajoRequestDTO request, Authentication auth) {
        return servicio.generar(request, auth.getName());
    }

    @GetMapping("/laboratorios")
    @PreAuthorize("hasAuthority('ORDENES_TRABAJO_LEER')")
    public List<LaboratorioDTO> laboratorios() {
        return servicio.laboratorios();
    }

    @PostMapping("/envios")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ORDENES_TRABAJO_GESTIONAR')")
    public EnvioLaboratorioResponseDTO enviar(@Valid @RequestBody EnvioLaboratorioRequestDTO request, Authentication auth) {
        return servicio.enviar(request, auth.getName());
    }

    @PutMapping("/envios/{idEnvio}/recepcion")
    @PreAuthorize("hasAuthority('ORDENES_TRABAJO_GESTIONAR')")
    public EnvioLaboratorioResponseDTO recibir(@PathVariable @Positive Long idEnvio,
                                               @Valid @RequestBody(required = false) RecepcionLaboratorioRequestDTO request, Authentication auth) {
        return servicio.recibir(idEnvio, request, auth.getName());
    }

    @PostMapping("/entrega")
    @PreAuthorize("hasAuthority('ORDENES_TRABAJO_GESTIONAR')")
    public OrdenTrabajoResponseDTO entregar(@Valid @RequestBody EntregaPedidoRequestDTO request, Authentication auth) {
        return servicio.entregar(request, auth.getName());
    }

    private Pageable pagina(int page, int size, Sort sort) {
        if ((long) page * size > Integer.MAX_VALUE)
            throw new ReglaNegocioException("La pagina excede el limite permitido");
        return PageRequest.of(page, size, sort);
    }
}

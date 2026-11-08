package pe.edu.upeu.geovision_backend.controller.ventas;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.geovision_backend.dto.ventas.VentaHistorialResponseDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.VentaResponseDTO;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.service.ventas.VentaHistorialService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/ventas/historial")
@RequiredArgsConstructor
@Slf4j
public class VentaHistorialController {

    private final VentaHistorialService ventaHistorialService;

    /**
     * CUS08: Consultar Historial de Ventas
     * Permite listar, buscar y filtrar transacciones comerciales con paginación y filtros dinámicos.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('VENTAS_LEER')")
    public ResponseEntity<Page<VentaHistorialResponseDTO>> buscarHistorialVentas(
            @RequestParam(value = "termino", required = false) String termino,
            @RequestParam(value = "estado", required = false) Integer estadoCodigo,
            @RequestParam(value = "fechaInicio", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(value = "fechaFin", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @PageableDefault(size = 10, sort = "fechaRegistro", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("Petición REST para buscar historial de ventas");
        EstadoVentaEnum estado = (estadoCodigo != null) ? EstadoVentaEnum.fromCodigo(estadoCodigo) : null;
        Page<VentaHistorialResponseDTO> pagina = ventaHistorialService.buscarHistorialVentas(
                termino,
                estado,
                fechaInicio,
                fechaFin,
                pageable
        );
        return ResponseEntity.ok(pagina);
    }

    /**
     * CUS08: Revisar detalle exhaustivo de la venta en modal superpuesto.
     */
    @GetMapping("/{idVenta}/detalle")
    @PreAuthorize("hasAuthority('VENTAS_LEER')")
    public ResponseEntity<VentaResponseDTO> obtenerDetalleModalVenta(
            @PathVariable("idVenta") Long idVenta) {
        log.info("Petición REST para obtener detalle modal de la venta ID: {}", idVenta);
        VentaResponseDTO detalle = ventaHistorialService.obtenerDetalleModalVenta(idVenta);
        return ResponseEntity.ok(detalle);
    }
}

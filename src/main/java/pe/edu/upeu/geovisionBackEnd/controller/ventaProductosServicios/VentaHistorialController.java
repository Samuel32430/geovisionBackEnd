package pe.edu.upeu.geovisionBackEnd.controller.ventaProductosServicios;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.geovisionBackEnd.dto.generic.PaginaResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.FiltroVentaDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.VentaDetalleResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.VentaResumenDTO;
import pe.edu.upeu.geovisionBackEnd.service.service.ventaProductosServicios.VentaHistorialService;

@RestController
@RequestMapping("/api/v1/ventas/historial")
@RequiredArgsConstructor
@Slf4j
public class VentaHistorialController {

    private final VentaHistorialService ventaHistorialService;

    @GetMapping
    public ResponseEntity<PaginaResponseDTO<VentaResumenDTO>> listarHistorial(
            @ModelAttribute FiltroVentaDTO filtro,
            @PageableDefault(size = 10) Pageable pageable) {
        log.info("REST request para consultar historial de ventas. Filtro: termino='{}', idEstado={}",
                filtro.getTermino(), filtro.getIdEstado());
        return ResponseEntity.ok(ventaHistorialService.listarHistorialVentas(filtro, pageable));
    }

    @GetMapping("/{idVenta}")
    public ResponseEntity<VentaDetalleResponseDTO> obtenerDetalle(@PathVariable Long idVenta) {
        log.info("REST request para obtener detalle de venta con ID: {}", idVenta);
        return ResponseEntity.ok(ventaHistorialService.obtenerDetalleVenta(idVenta));
    }
}

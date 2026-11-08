package pe.edu.upeu.geovision_backend.controller.inventario;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.geovision_backend.dto.inventario.AjusteStockRequestDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.EntradaStockRequestDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.MovimientoStockResponseDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoResponseDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoStockResponseDTO;
import pe.edu.upeu.geovision_backend.service.inventario.InventarioStockService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
@Slf4j
public class InventarioStockController {

    private final InventarioStockService inventarioStockService;

    /**
     * RF-72, RF-73 / CU513: Consultar el stock actual, precio y disponibilidad de un producto.
     */
    @GetMapping("/stock/{idProducto}")
    @PreAuthorize("hasAnyAuthority('INVENTARIO_LEER', 'PRODUCTOS_LEER', 'ROLE_ADMINISTRADOR')")
    public ResponseEntity<ProductoStockResponseDTO> consultarStockActual(
            @PathVariable("idProducto") @Positive Long idProducto,
            @RequestParam(value = "umbralStockMinimo", required = false, defaultValue = "5") Integer umbralStockMinimo) {

        log.debug("REST consultar stock actual producto ID: {}", idProducto);
        ProductoStockResponseDTO response = inventarioStockService.consultarStockActual(idProducto, umbralStockMinimo);
        return ResponseEntity.ok(response);
    }

    /**
     * RF-74, RF-76, RF-78, RF-79 / CU513: Registrar entrada de productos al stock (Kardex).
     */
    @PostMapping("/entradas")
    @PreAuthorize("hasAnyAuthority('INVENTARIO_REGISTRAR', 'INVENTARIO_GESTIONAR', 'ROLE_ADMINISTRADOR')")
    public ResponseEntity<MovimientoStockResponseDTO> registrarEntradaStock(
            @Valid @RequestBody EntradaStockRequestDTO request,
            Authentication authentication) {

        String username = authentication != null ? authentication.getName() : "SISTEMA";
        log.info("REST registrar entrada de stock para producto ID: {} por usuario: {}", request.getIdProducto(), username);

        MovimientoStockResponseDTO response = inventarioStockService.registrarEntradaStock(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * RF-75, RF-76, RF-77, RF-78, RF-79 / CU513: Registrar ajuste de stock por diferencia, daño o pérdida (Kardex).
     */
    @PostMapping("/ajustes")
    @PreAuthorize("hasAnyAuthority('INVENTARIO_AJUSTAR', 'INVENTARIO_GESTIONAR', 'ROLE_ADMINISTRADOR')")
    public ResponseEntity<MovimientoStockResponseDTO> registrarAjusteStock(
            @Valid @RequestBody AjusteStockRequestDTO request,
            Authentication authentication) {

        String username = authentication != null ? authentication.getName() : "SISTEMA";
        log.info("REST registrar ajuste de stock para producto ID: {} por usuario: {}", request.getIdProducto(), username);

        MovimientoStockResponseDTO response = inventarioStockService.registrarAjusteStock(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * RF-82 / CU514: Consultar productos con stock bajo o agotado según umbral referencial.
     */
    @GetMapping("/alertas/bajo-stock")
    @PreAuthorize("hasAnyAuthority('INVENTARIO_LEER', 'PRODUCTOS_LEER', 'ROLE_ADMINISTRADOR')")
    public ResponseEntity<List<ProductoResponseDTO>> listarAlertasBajoStock(
            @RequestParam(value = "umbral", required = false, defaultValue = "5") Integer umbral) {

        log.debug("REST consultar alertas de bajo stock con umbral: {}", umbral);
        List<ProductoResponseDTO> response = inventarioStockService.listarAlertasBajoStock(umbral);
        return ResponseEntity.ok(response);
    }

    /**
     * RF-83 / CU514: Consultar el historial de movimientos (Kardex) de un producto.
     */
    @GetMapping("/movimientos/{idProducto}")
    @PreAuthorize("hasAnyAuthority('INVENTARIO_LEER', 'PRODUCTOS_LEER', 'ROLE_ADMINISTRADOR')")
    public ResponseEntity<Page<MovimientoStockResponseDTO>> listarMovimientosKardex(
            @PathVariable("idProducto") @Positive Long idProducto,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {

        log.debug("REST consultar historial Kardex producto ID: {}", idProducto);
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaRegistro").descending());
        Page<MovimientoStockResponseDTO> response = inventarioStockService.listarMovimientosKardex(idProducto, pageable);
        return ResponseEntity.ok(response);
    }
}

package pe.edu.upeu.geovision_backend.controller.ventas;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.geovision_backend.dto.ventas.VentaRegistroRequestDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.VentaResponseDTO;
import pe.edu.upeu.geovision_backend.service.ventas.VentaService;

@RestController
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
@Slf4j
public class VentaController {

    private final VentaService ventaService;

    /**
     * CUS06: Registrar Venta
     * Permite registrar de forma unificada una venta de productos, servicios o lentes con receta médica,
     * calculando importes, procesando pagos completos o adelantos y deduciendo stock de catálogo.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('VENTAS_REGISTRAR')")
    public ResponseEntity<VentaResponseDTO> registrarVenta(@Valid @RequestBody VentaRegistroRequestDTO request) {
        log.info("Petición REST para registrar venta recibida");
        VentaResponseDTO response = ventaService.registrarVenta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Consulta una venta por su identificador primario.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VENTAS_LEER')")
    public ResponseEntity<VentaResponseDTO> obtenerVentaPorId(@PathVariable("id") Long id) {
        log.info("Petición REST para obtener venta con ID: {}", id);
        VentaResponseDTO response = ventaService.obtenerVentaPorId(id);
        return ResponseEntity.ok(response);
    }
}

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoPendienteResponseDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoRegistroRequestDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoResponseDTO;
import pe.edu.upeu.geovision_backend.service.ventas.PagoPendienteService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ventas/pagos-pendientes")
@RequiredArgsConstructor
@Slf4j
public class PagoPendienteController {

    private final PagoPendienteService pagoPendienteService;

    /**
     * CUS07: Listar ventas con saldo pendiente de pago.
     * Permite filtrar opcionalmente por comprobante, cliente o número de documento.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('VENTAS_LEER', 'PAGOS_GESTIONAR')")
    public ResponseEntity<List<PagoPendienteResponseDTO>> listarVentasConSaldoPendiente(
            @RequestParam(value = "termino", required = false) String termino) {
        log.info("Petición REST para listar ventas con saldo pendiente, filtro: '{}'", termino);
        List<PagoPendienteResponseDTO> lista = pagoPendienteService.listarVentasConPagosPendientes(termino);
        return ResponseEntity.ok(lista);
    }

    /**
     * CUS07: Consultar saldo y desglose de deuda de una venta específica.
     */
    @GetMapping("/{idVenta}")
    @PreAuthorize("hasAnyAuthority('VENTAS_LEER', 'PAGOS_GESTIONAR')")
    public ResponseEntity<PagoPendienteResponseDTO> obtenerSaldoPendientePorVenta(
            @PathVariable("idVenta") Long idVenta) {
        log.info("Petición REST para consultar balance de saldo de la venta ID: {}", idVenta);
        PagoPendienteResponseDTO response = pagoPendienteService.obtenerSaldoPendientePorVenta(idVenta);
        return ResponseEntity.ok(response);
    }

    /**
     * CUS07: Registrar abono o liquidación total del saldo adeudado.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('PAGOS_GESTIONAR')")
    public ResponseEntity<PagoResponseDTO> registrarPagoPendiente(
            @Valid @RequestBody PagoRegistroRequestDTO request) {
        log.info("Petición REST para registrar abono/liquidación a venta ID: {}", request.idVenta());
        PagoResponseDTO response = pagoPendienteService.registrarPagoPendiente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

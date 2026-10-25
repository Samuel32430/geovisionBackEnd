package pe.edu.upeu.geovisionBackEnd.controller.ventaProductosServicios;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.PagoPendienteRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.PagoResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.SaldoPendienteDTO;
import pe.edu.upeu.geovisionBackEnd.service.service.ventaProductosServicios.PagoPendienteService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ventas/pagos-pendientes")
@RequiredArgsConstructor
@Slf4j
public class PagoPendienteController {

    private final PagoPendienteService pagoPendienteService;

    @GetMapping
    public ResponseEntity<List<SaldoPendienteDTO>> listarVentasConSaldoPendiente(
            @RequestParam(required = false) String termino) {
        log.info("REST request para listar ventas con saldo pendiente. Filtro termino: {}", termino);
        return ResponseEntity.ok(pagoPendienteService.listarVentasConSaldoPendiente(termino));
    }

    @GetMapping("/{idComprobante}")
    public ResponseEntity<SaldoPendienteDTO> obtenerSaldoPendiente(@PathVariable Long idComprobante) {
        log.info("REST request para obtener saldo pendiente del comprobante con id: {}", idComprobante);
        return ResponseEntity.ok(pagoPendienteService.obtenerSaldoPendientePorComprobante(idComprobante));
    }

    @PostMapping
    public ResponseEntity<PagoResponseDTO> registrarPago(@Valid @RequestBody PagoPendienteRequestDTO request) {
        log.info("REST request para registrar pago pendiente para el comprobante id: {}", request.getComprobanteId());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoPendienteService.registrarPagoPendiente(request));
    }
}

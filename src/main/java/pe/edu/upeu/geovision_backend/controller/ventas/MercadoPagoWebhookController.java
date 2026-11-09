package pe.edu.upeu.geovision_backend.controller.ventas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.geovision_backend.dto.ventas.MercadoPagoQrRequestDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.MercadoPagoQrResponseDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoResponseDTO;
import pe.edu.upeu.geovision_backend.service.ventas.MercadoPagoService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ventas/pagos")
@RequiredArgsConstructor
public class MercadoPagoWebhookController {

    // >>>> CONSTANTS
    private static final Logger log = LoggerFactory.getLogger(MercadoPagoWebhookController.class);

    // >>>> DEPENDENCIES
    private final MercadoPagoService mercadoPagoService;

    // >>>> COMMANDS
    /**
     * Genera la orden con código QR dinámico interoperable para cobrar con Yape, Plin o Mercado Pago.
     */
    @PostMapping("/qr")
    @PreAuthorize("hasAnyAuthority('VENTAS_REGISTRAR', 'PAGOS_GESTIONAR')")
    public ResponseEntity<MercadoPagoQrResponseDTO> generarOrdenQr(
            @Valid @RequestBody MercadoPagoQrRequestDTO request) {
        log.info("Petición REST para generar orden QR para venta ID: {}", request.idVenta());
        MercadoPagoQrResponseDTO response = mercadoPagoService.generarOrdenCobroQr(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint público para recepción de notificaciones Webhook de Mercado Pago.
     * No requiere token JWT y siempre devuelve HTTP 200 OK para confirmar recepción a la pasarela.
     */
    @PostMapping("/webhook/mercadopago")
    public ResponseEntity<Void> recibirWebhookMercadoPago(@RequestBody(required = false) Map<String, Object> payload) {
        log.info("Webhook recibido de Mercado Pago: {}", payload);
        mercadoPagoService.procesarWebhook(payload);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para simulación / confirmación de prueba de un pago digital.
     */
    @PostMapping("/confirmar-simulado")
    @PreAuthorize("hasAuthority('PAGOS_GESTIONAR')")
    public ResponseEntity<PagoResponseDTO> confirmarPagoDigital(
            @RequestParam("idVenta") Long idVenta,
            @RequestParam("paymentId") String paymentId,
            @RequestParam("monto") BigDecimal monto) {
        log.info("Confirmando pago digital simulado para venta ID: {}", idVenta);
        PagoResponseDTO response = mercadoPagoService.confirmarPagoDigital(idVenta, paymentId, monto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

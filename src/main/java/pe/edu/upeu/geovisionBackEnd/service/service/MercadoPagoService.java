package pe.edu.upeu.geovisionBackEnd.service.service;

import pe.edu.upeu.geovisionBackEnd.dto.MercadoPagoQrRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.MercadoPagoQrResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.PagoResponseDTO;

import java.math.BigDecimal;
import java.util.Map;

public interface MercadoPagoService {

    /**
     * Genera una orden de cobro en Mercado Pago y retorna el código QR dinámico interoperable (Yape, Plin, MP).
     */
    MercadoPagoQrResponseDTO generarOrdenCobroQr(MercadoPagoQrRequestDTO request);

    /**
     * Procesa la notificación Webhook emitida por Mercado Pago cuando se aprueba una transacción digital.
     */
    void procesarWebhook(Map<String, Object> payload);

    /**
     * Confirma un pago digital verificado, asentando el registro en BD y actualizando el estado de la venta.
     */
    PagoResponseDTO confirmarPagoDigital(Long idVenta, String paymentId, BigDecimal monto);
}

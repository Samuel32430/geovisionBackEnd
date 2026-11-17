package pe.edu.upeu.geovision_backend.dto.ventas;

import java.math.BigDecimal;

public record MercadoPagoQrResponseDTO(
    Long idVenta,
    String externalReference,
    BigDecimal monto,
    String qrData,
    String qrImageUrl,
    String inStoreOrderId,
    String ticketUrl,
    String mensaje
) {
}

package pe.edu.upeu.geovision_backend.dto.ventas;

import java.util.Map;

public record MercadoPagoWebhookDTO(
    String id,
    Boolean liveMode,
    String type,
    String dateCreated,
    Long userId,
    String apiVersion,
    String action,
    Map<String, Object> data
) {
}

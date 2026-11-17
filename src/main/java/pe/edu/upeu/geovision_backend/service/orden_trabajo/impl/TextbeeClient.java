package pe.edu.upeu.geovision_backend.service.orden_trabajo.impl;

import lombok.RequiredArgsConstructor;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;

@Component
@RequiredArgsConstructor
public class TextbeeClient {
    public record Resultado(String estado, String referencia, String detalle) {
    }

    // >>>> CONFIGURATION
    @Value("${app.textbee.enabled:false}")
    private final boolean habilitado;
    @Value("${app.textbee.base-url:https://api.textbee.dev/api/v1}")
    private final String baseUrl;
    @Value("${app.textbee.api-key:}")
    private final String apiKey;
    @Value("${app.textbee.device-id:}")
    private final String deviceId;

    // >>>> FIELDS
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final ObjectMapper json = new ObjectMapper();

    // >>>> SMS
    public Resultado enviar(String telefono, String mensaje) {
        validarConfiguracion();
        try {
            String body = json.writeValueAsString(Map.of("recipients", List.of(telefono), "message", mensaje, "deviceId", deviceId));
            var request = HttpRequest.newBuilder(URI.create(baseUrl.replaceAll("/+$", "") + "/gateway/send-sms"))
                    .timeout(Duration.ofSeconds(15)).header("x-api-key", apiKey).header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body)).build();
            var response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400 && response.statusCode() < 500 && response.statusCode() != 408)
                return new Resultado("FALLIDO", null, response.statusCode() == 429 ? "Limite de Textbee alcanzado; no se envio el SMS." : "Textbee rechazo la solicitud. Revise configuracion y dispositivo.");
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                var data = json.readTree(response.body()).path("data");
                String batch = data.path("smsBatchId").asString("");
                if (data.path("success").asBoolean(false) && !batch.isBlank())
                    return new Resultado("ACEPTADO", batch, "SMS aceptado en cola; entrega al paciente no confirmada.");
            }
            return incierto();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return incierto();
        } catch (Exception e) {
            // Provider responses may contain sensitive information.
            return incierto();
        }
    }

    // >>>> VALIDATION
    public void validarConfiguracion() {
        if (!habilitado || apiKey.isBlank() || deviceId.isBlank())
            throw new ReglaNegocioException("SMS no disponible. Configure Textbee en el backend.");
    }

    // >>>> HELPERS
    private Resultado incierto() {
        return new Resultado("INCIERTO", null, "No se pudo confirmar el resultado. Revise Textbee antes de volver a enviar.");
    }

}

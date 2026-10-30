package pe.edu.upeu.geovisionBackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MercadoPagoWebhookDTO {

    private String id;
    private Boolean liveMode;
    private String type;
    private String dateCreated;
    private Long userId;
    private String apiVersion;
    private String action;
    private Map<String, Object> data;
}

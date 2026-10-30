package pe.edu.upeu.geovisionBackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MercadoPagoQrResponseDTO {

    private Long idVenta;
    private String externalReference;
    private BigDecimal monto;
    private String qrData;
    private String inStoreOrderId;
    private String ticketUrl;
    private String mensaje;
}

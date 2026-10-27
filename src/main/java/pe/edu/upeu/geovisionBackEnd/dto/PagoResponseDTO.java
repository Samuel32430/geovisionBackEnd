package pe.edu.upeu.geovisionBackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.geovisionBackEnd.enums.MetodoPagoEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDTO {

    private Long idPago;
    private Long idVenta;
    private BigDecimal monto;
    private MetodoPagoEnum metodoPago;
    private String metodoPagoDescripcion;
    private String numeroOperacion;
    private LocalDateTime fechaRegistro;
}

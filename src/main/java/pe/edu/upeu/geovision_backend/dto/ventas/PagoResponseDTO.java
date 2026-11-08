package pe.edu.upeu.geovision_backend.dto.ventas;

import lombok.Builder;
import pe.edu.upeu.geovision_backend.enums.ventas.MetodoPagoEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record PagoResponseDTO(
    Long idPago,
    Long idVenta,
    BigDecimal monto,
    MetodoPagoEnum metodoPago,
    String metodoPagoDescripcion,
    String numeroOperacion,
    LocalDateTime fechaRegistro
) {
}

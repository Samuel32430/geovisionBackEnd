package pe.edu.upeu.geovision_backend.dto.ventas;

import pe.edu.upeu.geovision_backend.enums.ventas.MetodoPagoEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponseDTO(
    Long id,
    Long idVenta,
    BigDecimal monto,
    MetodoPagoEnum metodoPago,
    String metodoPagoDescripcion,
    String numeroOperacion,
    LocalDateTime fechaRegistro
) {
}

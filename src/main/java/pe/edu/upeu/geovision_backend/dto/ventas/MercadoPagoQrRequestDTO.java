package pe.edu.upeu.geovision_backend.dto.ventas;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record MercadoPagoQrRequestDTO(
    Long idVenta,
    @NotNull(message = "El monto a cobrar con QR es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    BigDecimal monto
) {
}

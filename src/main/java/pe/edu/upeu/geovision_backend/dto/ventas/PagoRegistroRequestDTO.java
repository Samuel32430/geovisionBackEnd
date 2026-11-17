package pe.edu.upeu.geovision_backend.dto.ventas;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import pe.edu.upeu.geovision_backend.enums.ventas.MetodoPagoEnum;

import java.math.BigDecimal;

public record PagoRegistroRequestDTO(
    Long idVenta,
    @NotNull(message = "El monto del pago es obligatorio")
    @Positive(message = "El monto del pago debe ser mayor a cero")
    BigDecimal monto,
    @NotNull(message = "El método de pago es obligatorio")
    MetodoPagoEnum metodoPago,
    @Size(max = 100, message = "El número de operación no debe superar los 100 caracteres")
    String numeroOperacion
) {
}

package pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagoInicialRequestDTO {

    @NotNull(message = "El método de pago es obligatorio")
    private Long metodoPagoId;

    @NotNull(message = "El monto abonado es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
    private BigDecimal monto;

    private String numeroOperacion;
}

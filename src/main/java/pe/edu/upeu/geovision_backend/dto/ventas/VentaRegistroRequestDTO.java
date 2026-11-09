package pe.edu.upeu.geovision_backend.dto.ventas;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import pe.edu.upeu.geovision_backend.enums.ventas.TipoComprobanteEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record VentaRegistroRequestDTO(

    Long idEmpleado,
    Long idPaciente,
    Long idReceta,
    @NotNull(message = "El tipo de comprobante es obligatorio")
    TipoComprobanteEnum tipoComprobante,
    String numeroComprobante,
    LocalDate fechaEntregaEstimada,
    @Valid
    List<ProductoVendidoDTO> productos,
    @Valid
    List<ServicioVendidoDTO> servicios,
    @Valid
    @NotEmpty(message = "Debe registrar al menos un pago inicial o completo")
    List<PagoRegistroRequestDTO> pagos,
    @NotNull(message = "El total de la venta es obligatorio")
    @Positive(message = "El total de la venta debe ser mayor a cero")
    BigDecimal total
) {
}

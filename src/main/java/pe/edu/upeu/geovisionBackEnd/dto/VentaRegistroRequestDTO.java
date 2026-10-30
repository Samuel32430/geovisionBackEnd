package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.geovisionBackEnd.enums.TipoComprobanteEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaRegistroRequestDTO {

    @NotNull(message = "El ID del vendedor/empleado es obligatorio")
    private Long idEmpleado;

    private Long idPaciente;

    private Long idReceta;

    @NotNull(message = "El tipo de comprobante es obligatorio")
    private TipoComprobanteEnum tipoComprobante;

    private String numeroComprobante;

    private LocalDate fechaEntregaEstimada;

    @Valid
    private List<ProductoVendidoDTO> productos;

    @Valid
    private List<ServicioVendidoDTO> servicios;

    @Valid
    @NotEmpty(message = "Debe registrar al menos un pago inicial o completo")
    private List<PagoRegistroRequestDTO> pagos;

    @NotNull(message = "El total de la venta es obligatorio")
    @Positive(message = "El total de la venta debe ser mayor a cero")
    private BigDecimal total;
}

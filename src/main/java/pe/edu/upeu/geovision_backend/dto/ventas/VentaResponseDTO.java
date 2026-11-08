package pe.edu.upeu.geovision_backend.dto.ventas;

import lombok.Builder;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.TipoComprobanteEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
public record VentaResponseDTO(
    Long idVenta,
    LocalDateTime fechaRegistro,
    BigDecimal total,
    BigDecimal totalPagado,
    BigDecimal saldoPendiente,
    EstadoVentaEnum estado,
    String estadoDescripcion,
    TipoComprobanteEnum tipoComprobante,
    String tipoComprobanteDescripcion,
    String numeroComprobante,
    Long idEmpleado,
    String nombreCompletoEmpleado,
    Long idPaciente,
    String nombreCompletoPaciente,
    Long idReceta,
    List<ProductoVendidoDTO> productos,
    List<ServicioVendidoDTO> servicios,
    List<PagoResponseDTO> pagos
) {
}

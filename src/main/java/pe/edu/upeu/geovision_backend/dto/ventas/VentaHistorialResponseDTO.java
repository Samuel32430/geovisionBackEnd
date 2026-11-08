package pe.edu.upeu.geovision_backend.dto.ventas;

import lombok.Builder;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.TipoComprobanteEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record VentaHistorialResponseDTO(
    Long idVenta,
    LocalDateTime fechaRegistro,
    String numeroComprobante,
    TipoComprobanteEnum tipoComprobante,
    String tipoComprobanteDescripcion,
    String clienteNombre,
    String clienteDocumento,
    String vendedorNombre,
    BigDecimal total,
    BigDecimal totalPagado,
    BigDecimal saldoPendiente,
    EstadoVentaEnum estado,
    String estadoDescripcion,
    Boolean tieneReceta,
    Integer cantidadItems
) {
}

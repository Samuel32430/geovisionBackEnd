package pe.edu.upeu.geovision_backend.dto.ventas;

import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.TipoComprobanteEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VentaHistorialResponseDTO(
    Long id,
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

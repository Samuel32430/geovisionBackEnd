package pe.edu.upeu.geovision_backend.dto.reports;

import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.TipoComprobanteEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ItemVentaReporteDTO(
    Long idVenta,
    LocalDateTime fechaRegistro,
    String numeroComprobante,
    TipoComprobanteEnum tipoComprobante,
    EstadoVentaEnum estado,
    BigDecimal total,
    BigDecimal totalPagado,
    BigDecimal saldoPendiente,
    String condicionPago,
    String docCliente,
    String nombreCliente,
    String nombreVendedor,
    int cantidadItems
) {
}

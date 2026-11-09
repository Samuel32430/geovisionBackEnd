package pe.edu.upeu.geovision_backend.dto.reports;

import org.springframework.format.annotation.DateTimeFormat;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.TipoComprobanteEnum;

import java.time.LocalDate;

public record FiltroReporteVentasDTO(
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate fechaInicio,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate fechaFin,

    Long idEmpleado,
    TipoComprobanteEnum tipoComprobante,
    EstadoVentaEnum estado
) {
}

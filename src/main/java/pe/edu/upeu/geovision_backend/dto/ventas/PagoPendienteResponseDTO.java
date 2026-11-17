package pe.edu.upeu.geovision_backend.dto.ventas;

import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PagoPendienteResponseDTO(
    Long id,
    String numeroComprobante,
    LocalDateTime fechaVenta,
    Long idPaciente,
    String pacienteNombre,
    String pacienteTelefono,
    String pacienteDocumento,
    BigDecimal totalVenta,
    BigDecimal totalPagado,
    BigDecimal saldoPendiente,
    EstadoVentaEnum estado,
    List<PagoResponseDTO> historialPagos
) {
}

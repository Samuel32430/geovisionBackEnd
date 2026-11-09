package pe.edu.upeu.geovision_backend.dto.orden_trabajo;
import java.math.BigDecimal;
public record PendienteOrdenTrabajoDTO(Long idVenta, String numeroComprobante, Long idLente,
        Long idReceta, Long idPaciente, String nombrePaciente, BigDecimal totalVenta
) {}

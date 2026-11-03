package pe.edu.upeu.geovisionBackEnd.dto;
import java.math.BigDecimal;
public record PendienteOrdenTrabajoDTO(Long idVenta, String numeroComprobante, Long idLente,
        Long idReceta, Long idPaciente, String nombrePaciente, BigDecimal totalVenta) {}

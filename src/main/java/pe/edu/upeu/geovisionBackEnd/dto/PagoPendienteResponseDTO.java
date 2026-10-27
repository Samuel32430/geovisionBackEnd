package pe.edu.upeu.geovisionBackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoVentaEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoPendienteResponseDTO {

    private Long idVenta;
    private String numeroComprobante;
    private LocalDateTime fechaVenta;
    private Long idPaciente;
    private String pacienteNombre;
    private String pacienteTelefono;
    private String pacienteDocumento;
    private BigDecimal totalVenta;
    private BigDecimal totalPagado;
    private BigDecimal saldoPendiente;
    private EstadoVentaEnum estado;
    private List<PagoResponseDTO> historialPagos;
}

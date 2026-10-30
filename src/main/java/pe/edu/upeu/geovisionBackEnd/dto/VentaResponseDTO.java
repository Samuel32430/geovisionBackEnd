package pe.edu.upeu.geovisionBackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoVentaEnum;
import pe.edu.upeu.geovisionBackEnd.enums.TipoComprobanteEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaResponseDTO {

    private Long idVenta;
    private LocalDateTime fechaRegistro;
    private BigDecimal total;
    private BigDecimal totalPagado;
    private BigDecimal saldoPendiente;
    private EstadoVentaEnum estado;
    private String estadoDescripcion;
    private TipoComprobanteEnum tipoComprobante;
    private String tipoComprobanteDescripcion;
    private String numeroComprobante;
    private Long idEmpleado;
    private String nombreCompletoEmpleado;
    private Long idPaciente;
    private String nombreCompletoPaciente;
    private Long idReceta;
    private List<ProductoVendidoDTO> productos;
    private List<ServicioVendidoDTO> servicios;
    private List<PagoResponseDTO> pagos;
}

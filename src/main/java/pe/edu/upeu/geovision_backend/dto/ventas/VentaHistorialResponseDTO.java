package pe.edu.upeu.geovision_backend.dto.ventas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.TipoComprobanteEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaHistorialResponseDTO {

    private Long idVenta;
    private LocalDateTime fechaRegistro;
    private String numeroComprobante;
    private TipoComprobanteEnum tipoComprobante;
    private String tipoComprobanteDescripcion;
    private String clienteNombre;
    private String clienteDocumento;
    private String vendedorNombre;
    private BigDecimal total;
    private BigDecimal totalPagado;
    private BigDecimal saldoPendiente;
    private EstadoVentaEnum estado;
    private String estadoDescripcion;
    private Boolean tieneReceta;
    private Integer cantidadItems;
}

package pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VentaResponseDTO {

    private Long idVenta;
    private LocalDateTime fechaRegistro;
    private String moneda;
    private String observaciones;
    private BigDecimal total;
    private Long pacienteId;
    private String pacienteNombreCompleto;
    private Long compradorId;
    private String compradorNombreCompleto;
    private Long empleadoId;
    private String empleadoNombreCompleto;
    private Long recetaId;
    private Long estadoVentaId;
    private String estadoVentaNombre;

    private List<ItemVendidoResponseDTO> itemsVendidos = new ArrayList<>();
    private List<ServicioVendidoResponseDTO> serviciosVendidos = new ArrayList<>();
    private ComprobanteResponseDTO comprobante;

    private BigDecimal totalPagado = BigDecimal.ZERO;
    private BigDecimal saldoPendiente = BigDecimal.ZERO;
}

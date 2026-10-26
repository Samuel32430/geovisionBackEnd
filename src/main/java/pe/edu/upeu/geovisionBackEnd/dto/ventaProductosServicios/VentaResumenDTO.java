package pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaResumenDTO {

    private Long idVenta;
    private String comprobanteSerieNumero;  // Formato real: B001-000482
    private LocalDateTime fechaRegistro;    // Fecha y hora de venta
    private String clienteNombre;           // Nombre completo del cliente
    private String clienteDni;              // DNI visible en la fila de la tabla
    private String clienteIniciales;        // Iniciales para avatar visual (ej. MR)
    private String tipoVenta;               // Venta directa, Venta de servicio, Venta con receta
    private BigDecimal total;               // Total de la venta
    private String estadoVisual;            // En Laboratorio, Completado, Pago Pendiente, Cancelado
    private String estadoVenta;             // Estado comercial (REGISTRADA, ENTREGADA, ANULADA)
    private String estadoCobro;             // Estado financiero (PENDIENTE, CON_ADELANTO, CANCELADO)
    private BigDecimal totalPagado;
    private BigDecimal saldoPendiente;
}

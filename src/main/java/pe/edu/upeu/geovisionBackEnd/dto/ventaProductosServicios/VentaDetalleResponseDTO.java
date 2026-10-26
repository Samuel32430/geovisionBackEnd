package pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaDetalleResponseDTO {

    // Cabecera de la transacción
    private Long idVenta;
    private String comprobanteSerieNumero;  // Formato real: B001-000482
    private String clienteNombre;           // Nombre del cliente (sin DNI en modal)
    private LocalDateTime fechaEmision;     // Fecha y hora
    private String tipoVenta;               // Venta con receta / Venta directa / Venta de servicio
    private String recetaCodigo;            // Interna #RC-084 (o null si no aplica)
    private Long recetaId;
    private String estadoActual;            // En Laboratorio / Completado / Pago Pendiente / Cancelado

    // Detalle de ítems
    @Builder.Default
    private List<ItemDetalleResponseDTO> items = new ArrayList<>();

    // Cobro y taller
    private String metodoCobro;             // Tarjeta de Crédito (Visa **** 4921) / Efectivo
    private LocalDate fechaEntregaEstimada; // Fecha de taller si aplica
    private Boolean tieneOrdenTrabajo;      // Habilita botón Imprimir Orden de Trabajo
    private String numeroOrdenTrabajo;      // #ORD-2024-089

    // Resumen financiero
    private BigDecimal subtotal;
    private BigDecimal total;
    private BigDecimal adelantoAbonado;
    private BigDecimal saldoPendiente;
}

package pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDTO {

    private Long idPago;
    private Long idComprobante;
    private String serieNumeroComprobante;
    private Long idMetodoPago;
    private String nombreMetodoPago;
    private Long idEmpleado;
    private String nombreEmpleado;
    private LocalDateTime fechaRegistro;
    private BigDecimal monto;
    private String numeroOperacion;
    private Integer anulado;
    private String motivoAnulacion;
    private BigDecimal saldoRestante;
    private String nuevoEstadoCobro;

    // Métodos alias para compatibilidad bidireccional con nomenclatura previa
    public Long getMetodoPagoId() {
        return idMetodoPago;
    }

    public void setMetodoPagoId(Long metodoPagoId) {
        this.idMetodoPago = metodoPagoId;
    }

    public String getMetodoPagoNombre() {
        return nombreMetodoPago;
    }

    public void setMetodoPagoNombre(String metodoPagoNombre) {
        this.nombreMetodoPago = metodoPagoNombre;
    }

    public Long getEmpleadoId() {
        return idEmpleado;
    }

    public void setEmpleadoId(Long empleadoId) {
        this.idEmpleado = empleadoId;
    }

    public String getEmpleadoNombre() {
        return nombreEmpleado;
    }

    public void setEmpleadoNombre(String empleadoNombre) {
        this.nombreEmpleado = empleadoNombre;
    }
}

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
    private LocalDateTime fechaRegistro;
    private String numeroOperacion;
    private BigDecimal monto;
    private Integer anulado;
    private String motivoAnulacion;
    private Long metodoPagoId;
    private String metodoPagoNombre;
    private Long empleadoId;
    private String empleadoNombre;
}

package pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FiltroVentaDTO {

    private String termino;
    private String tipoVenta;       // DIRECTA, SERVICIO, CON_RECETA
    private String estadoVisual;    // EN_LABORATORIO, COMPLETADO, PAGO_PENDIENTE, CANCELADO
    private Long idEstado;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
}

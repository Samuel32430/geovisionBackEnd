package pe.edu.upeu.geovisionBackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoStockResponseDTO {

    private Long idMovimiento;
    private Long idProducto;
    private String codigoProducto;
    private String nombreProducto;
    private Integer cantidad;
    private Integer stockPrevio;
    private Integer stockPosterior;
    private Integer motivo;
    private String motivoDescripcion;
    private String observaciones;
    private String nombreUsuario;
    private LocalDateTime fechaRegistro;
}

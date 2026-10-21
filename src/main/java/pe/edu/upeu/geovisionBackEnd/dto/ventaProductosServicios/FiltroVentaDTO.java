package pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FiltroVentaDTO {

    private String termino;
    private Long idEstado;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
}

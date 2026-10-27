package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicioVendidoDTO {

    private Long idServicioVendido;

    @NotNull(message = "El ID del servicio es obligatorio")
    private Long idServicio;

    private String nombreServicio;

    @NotNull(message = "El precio del servicio es obligatorio")
    @PositiveOrZero(message = "El precio debe ser mayor o igual a cero")
    private BigDecimal precio;
}

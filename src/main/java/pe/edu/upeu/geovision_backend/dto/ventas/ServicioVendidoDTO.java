package pe.edu.upeu.geovision_backend.dto.ventas;

import lombok.Builder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record ServicioVendidoDTO(
    Long idServicioVendido,
    @NotNull(message = "El ID del servicio es obligatorio")
    Long idServicio,
    String nombreServicio,
    @NotNull(message = "El precio del servicio es obligatorio")
    @PositiveOrZero(message = "El precio debe ser mayor o igual a cero")
    BigDecimal precio
) {
}

package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicioDTO {

    private Long idServicio;

    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(max = 200, message = "El nombre del servicio no debe superar los 200 caracteres")
    private String nombre;

    @Size(max = 200, message = "La descripción no debe superar los 200 caracteres")
    private String descripcion;

    @NotNull(message = "El precio del servicio es obligatorio")
    @PositiveOrZero(message = "El precio debe ser mayor o igual a cero")
    private BigDecimal precio;

    @Builder.Default
    private Integer estado = 1;
}

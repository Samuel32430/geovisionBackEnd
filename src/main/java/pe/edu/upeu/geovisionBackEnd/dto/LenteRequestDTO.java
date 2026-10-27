package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LenteRequestDTO {

    @NotNull(message = "El ID de la receta es obligatorio")
    private Long idReceta;

    @NotNull(message = "El ID del material de cristal es obligatorio")
    private Long idMaterialCristal;

    private Long idProducto;

    @NotNull(message = "El subtotal del lente es obligatorio")
    @PositiveOrZero(message = "El subtotal debe ser mayor o igual a cero")
    private BigDecimal subTotal;

    private List<Long> tratamientosCristalIds;

    private List<Long> medidasIds;
}

package pe.edu.upeu.geovision_backend.dto.atencion;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.util.List;

public record LenteRequestDTO(
        @NotNull(message = "El ID de la receta es obligatorio")
        @Positive(message = "El ID de la receta debe ser positivo")
        Long idReceta,
        @NotNull(message = "El ID del material de cristal es obligatorio")
        @Positive(message = "El ID del material debe ser positivo")
        Long idMaterialCristal,
        @Positive(message = "El ID de la montura debe ser positivo")
        Long idProducto,
        @NotNull(message = "El subtotal del lente es obligatorio")
        @PositiveOrZero(message = "El subtotal debe ser mayor o igual a cero")
        @Digits(integer = 10, fraction = 2, message = "El subtotal admite hasta 10 enteros y 2 decimales")
        BigDecimal subTotal,
        List<@NotNull @Positive Long> tratamientosCristalIds,
        List<@NotNull @Positive Long> medidasIds
) {
}

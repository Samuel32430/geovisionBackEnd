package pe.edu.upeu.geovisionBackEnd.dto.controlInventario;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para los atributos específicos al registrar o actualizar un Cristal.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CristalRequestDTO {

    @DecimalMin(value = "1.001", message = "El índice de refracción debe ser estrictamente mayor a 1")
    @Digits(integer = 2, fraction = 3, message = "El índice de refracción debe tener como máximo 2 enteros y 3 decimales")
    private BigDecimal indiceRefraccion;

    @NotNull(message = "El material del cristal es obligatorio")
    @Positive(message = "El identificador de material de cristal debe ser positivo")
    private Long idMaterialCristal;

    private List<Long> idsTratamientos;
}

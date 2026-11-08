package pe.edu.upeu.geovision_backend.dto.inventario;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record EntradaStockRequestDTO(
    @NotNull(message = "El ID del producto es obligatorio")
    Long idProducto,
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad ingresada debe ser mayor a cero")
    Integer cantidad,
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = pe.edu.upeu.geovision_backend.config.DtoDefaultsJackson2.One.class)
    @tools.jackson.databind.annotation.JsonDeserialize(using = pe.edu.upeu.geovision_backend.config.DtoDefaultsJackson3.One.class)
    Integer motivo,
    @Size(max = 500, message = "Las observaciones no deben superar los 500 caracteres")
    String observaciones
) {
}

package pe.edu.upeu.geovision_backend.dto.inventario;

import lombok.Builder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Builder(toBuilder = true)
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(builder = EntradaStockRequestDTO.EntradaStockRequestDTOBuilder.class)
@tools.jackson.databind.annotation.JsonDeserialize(builder = EntradaStockRequestDTO.EntradaStockRequestDTOBuilder.class)
public record EntradaStockRequestDTO(
    @NotNull(message = "El ID del producto es obligatorio")
    Long idProducto,
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad ingresada debe ser mayor a cero")
    Integer cantidad,
    Integer motivo,
    @Size(max = 500, message = "Las observaciones no deben superar los 500 caracteres")
    String observaciones
) {
    @com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "")
    @tools.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "")
    public static class EntradaStockRequestDTOBuilder {
        private Integer motivo = 1;
    }
}

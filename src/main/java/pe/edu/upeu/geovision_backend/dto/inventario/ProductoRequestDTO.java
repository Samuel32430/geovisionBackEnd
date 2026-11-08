package pe.edu.upeu.geovision_backend.dto.inventario;

import lombok.Builder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Builder(toBuilder = true)
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(builder = ProductoRequestDTO.ProductoRequestDTOBuilder.class)
@tools.jackson.databind.annotation.JsonDeserialize(builder = ProductoRequestDTO.ProductoRequestDTOBuilder.class)
public record ProductoRequestDTO(
    @NotBlank(message = "El código del producto es obligatorio")
    @Size(max = 100, message = "El código del producto no debe superar los 100 caracteres")
    String codigo,
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 200, message = "El nombre del producto no debe superar los 200 caracteres")
    String nombre,
    @Size(max = 100, message = "La marca no debe superar los 100 caracteres")
    String marca,
    @Size(max = 200, message = "La descripción no debe superar los 200 caracteres")
    String descripcion,
    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio debe ser mayor o igual a cero")
    BigDecimal precio,
    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock debe ser mayor o igual a cero")
    Integer stock,
    Integer estado,
    @NotNull(message = "El ID de la categoría es obligatorio")
    Long idCategoria,
    @Valid
    MonturaDTO montura
) {
    @com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "")
    @tools.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "")
    public static class ProductoRequestDTOBuilder {
        private Integer stock = 0;
        private Integer estado = 1;
    }
}

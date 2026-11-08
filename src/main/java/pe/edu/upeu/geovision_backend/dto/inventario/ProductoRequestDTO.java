package pe.edu.upeu.geovision_backend.dto.inventario;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

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
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = pe.edu.upeu.geovision_backend.config.DtoDefaultsJackson2.Zero.class)
    @tools.jackson.databind.annotation.JsonDeserialize(using = pe.edu.upeu.geovision_backend.config.DtoDefaultsJackson3.Zero.class)
    Integer stock,
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = pe.edu.upeu.geovision_backend.config.DtoDefaultsJackson2.One.class)
    @tools.jackson.databind.annotation.JsonDeserialize(using = pe.edu.upeu.geovision_backend.config.DtoDefaultsJackson3.One.class)
    Integer estado,
    @NotNull(message = "El ID de la categoría es obligatorio")
    Long idCategoria,
    @Valid
    MonturaDTO montura
) {
}

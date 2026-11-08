package pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento;

import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Builder(toBuilder = true)
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(builder = ServicioDTO.ServicioDTOBuilder.class)
@tools.jackson.databind.annotation.JsonDeserialize(builder = ServicioDTO.ServicioDTOBuilder.class)
public record ServicioDTO(
    Long idServicio,
    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(max = 200, message = "El nombre del servicio no debe superar los 200 caracteres")
    String nombre,
    @Size(max = 200, message = "La descripción no debe superar los 200 caracteres")
    String descripcion,
    @NotNull(message = "El precio del servicio es obligatorio")
    @PositiveOrZero(message = "El precio debe ser mayor o igual a cero")
    @Digits(integer = 10, fraction = 2, message = "El precio admite hasta 10 enteros y 2 decimales")
    BigDecimal precio,
    @NotNull(message = "El estado es obligatorio")
    @Min(value = 0, message = "El estado debe ser 0 o 1")
    @Max(value = 1, message = "El estado debe ser 0 o 1")
    Integer estado
) {
    @com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "")
    @tools.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "")
    public static class ServicioDTOBuilder {
        private Integer estado = 1;
    }
}

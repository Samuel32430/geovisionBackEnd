package pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento;

import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(builder = CategoriaDTO.CategoriaDTOBuilder.class)
@tools.jackson.databind.annotation.JsonDeserialize(builder = CategoriaDTO.CategoriaDTOBuilder.class)
public record CategoriaDTO(
    Long idCategoria,
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 100, message = "El nombre de la categoría no debe superar los 100 caracteres")
    String nombre,
    @Size(max = 200, message = "La descripción no debe superar los 200 caracteres")
    String descripcion,
    @NotNull(message = "El estado es obligatorio")
    @Min(value = 0, message = "El estado debe ser 0 o 1")
    @Max(value = 1, message = "El estado debe ser 0 o 1")
    Integer estado,
    LocalDateTime fechaRegistro
) {
    @com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "")
    @tools.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "")
    public static class CategoriaDTOBuilder {
        private Integer estado = 1;
    }
}

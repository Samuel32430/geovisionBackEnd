package pe.edu.upeu.geovisionBackEnd.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(onConstructor_ = @JsonCreator(mode = JsonCreator.Mode.DISABLED))
public class CategoriaDTO {

    private Long idCategoria;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 100, message = "El nombre de la categoría no debe superar los 100 caracteres")
    private String nombre;

    @Size(max = 200, message = "La descripción no debe superar los 200 caracteres")
    private String descripcion;

    @Builder.Default
    @NotNull(message = "El estado es obligatorio")
    @Min(value = 0, message = "El estado debe ser 0 o 1")
    @Max(value = 1, message = "El estado debe ser 0 o 1")
    private Integer estado = 1;

    private LocalDateTime fechaRegistro;
}

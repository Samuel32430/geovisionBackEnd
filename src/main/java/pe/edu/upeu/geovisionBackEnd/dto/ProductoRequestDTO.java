package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequestDTO {

    @NotBlank(message = "El código del producto es obligatorio")
    @Size(max = 100, message = "El código del producto no debe superar los 100 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 200, message = "El nombre del producto no debe superar los 200 caracteres")
    private String nombre;

    @Size(max = 100, message = "La marca no debe superar los 100 caracteres")
    private String marca;

    @Size(max = 200, message = "La descripción no debe superar los 200 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio debe ser mayor o igual a cero")
    private BigDecimal precio;

    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock debe ser mayor o igual a cero")
    @Builder.Default
    private Integer stock = 0;

    @Builder.Default
    private Integer estado = 1;

    @NotNull(message = "El ID de la categoría es obligatorio")
    private Long idCategoria;

    @Valid
    private MonturaDTO montura;
}

package pe.edu.upeu.geovisionBackEnd.dto.controlInventario;

import jakarta.validation.constraints.*;
import lombok.*;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.TipoItem;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.UnidadVenta;

import java.math.BigDecimal;

/**
 * DTO para el registro inicial de un Item en catálogo.
 * Nota: No incluye stock, pues el stock no se ingresa directamente desde el cliente (RF-68, RF-70).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDTO {

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 100, message = "El código no debe superar los 100 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200, message = "El nombre no debe superar los 200 caracteres")
    private String nombre;

    @Size(max = 1000, message = "La descripción no debe superar los 1000 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
    @Digits(integer = 12, fraction = 2, message = "El precio debe tener como máximo 12 enteros y 2 decimales")
    private BigDecimal precio;

    @NotNull(message = "El tipo de ítem es obligatorio")
    private TipoItem tipo;

    @NotNull(message = "La unidad de venta es obligatoria")
    private UnidadVenta unidadVenta;

    @Builder.Default
    private Boolean controlaStock = true;

    @Builder.Default
    private Boolean estado = true;

    @NotNull(message = "La categoría es obligatoria")
    @Positive(message = "El identificador de categoría debe ser positivo")
    private Long idCategoria;

    @Positive(message = "El identificador de marca debe ser positivo")
    private Long idMarca;
}

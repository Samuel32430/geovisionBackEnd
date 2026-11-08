package pe.edu.upeu.geovision_backend.dto.atencion;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
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
public class MedidaOjoDTO {

    @Positive(message = "El ID de la medida del ojo debe ser positivo")
    private Long idMedidaOjo;

    @Size(max = 50, message = "La agudeza visual no debe superar los 50 caracteres")
    private String agudezaVisual;

    @Digits(integer = 3, fraction = 2, message = "La esfera admite hasta 3 enteros y 2 decimales")
    private BigDecimal esfera;

    @Digits(integer = 3, fraction = 2, message = "El cilindro admite hasta 3 enteros y 2 decimales")
    private BigDecimal cilindro;

    @Min(value = 0, message = "El eje no puede ser negativo")
    @Max(value = 180, message = "El eje no puede ser mayor a 180 grados")
    private Integer eje;

    @Digits(integer = 2, fraction = 2, message = "La adicion admite hasta 2 enteros y 2 decimales")
    private BigDecimal adicion;
}

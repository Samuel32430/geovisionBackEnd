package pe.edu.upeu.geovision_backend.dto.atencion;

import lombok.Builder;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public record MedidaOjoDTO(
    @Positive(message = "El ID de la medida del ojo debe ser positivo")
    Long idMedidaOjo,
    @Size(max = 50, message = "La agudeza visual no debe superar los 50 caracteres")
    String agudezaVisual,
    @Digits(integer = 3, fraction = 2, message = "La esfera admite hasta 3 enteros y 2 decimales")
    BigDecimal esfera,
    @Digits(integer = 3, fraction = 2, message = "El cilindro admite hasta 3 enteros y 2 decimales")
    BigDecimal cilindro,
    @Min(value = 0, message = "El eje no puede ser negativo")
    @Max(value = 180, message = "El eje no puede ser mayor a 180 grados")
    Integer eje,
    @Digits(integer = 2, fraction = 2, message = "La adicion admite hasta 2 enteros y 2 decimales")
    BigDecimal adicion
) {
}

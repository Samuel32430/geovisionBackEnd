package pe.edu.upeu.geovisionBackEnd.dto.gestionMantenimiento;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoParametroCalidadRequestDTO {

    @NotBlank(message = "nombre es obligatorio")
    @Size(max = 200, message = "nombre no puede superar 200 caracteres")
    private String nombre;

    @NotBlank(message = "unidad es obligatorio")
    @Size(max = 50, message = "unidad no puede superar 50 caracteres")
    private String unidad;

    @Digits(integer = 6, fraction = 4, message = "tolerancia excede la precision permitida")
    @DecimalMin(value = "0", inclusive = true, message = "tolerancia debe ser mayor o igual a 0")
    private BigDecimal tolerancia;

    @Digits(integer = 6, fraction = 4, message = "periodo excede la precision permitida")
    @DecimalMin(value = "0", inclusive = false, message = "periodo debe ser mayor a 0")
    private BigDecimal periodo;

    @NotNull(message = "valorEsperado es obligatorio")
    @Digits(integer = 6, fraction = 4, message = "valorEsperado excede la precision permitida")
    private BigDecimal valorEsperado;

    @JsonIgnore
    @AssertTrue(message = "La tolerancia no puede superar la mitad del periodo")
    public boolean isToleranciaValida() {
        return periodo == null || tolerancia == null || periodo.signum() <= 0
                || tolerancia.compareTo(periodo.divide(BigDecimal.valueOf(2))) <= 0;
    }
}

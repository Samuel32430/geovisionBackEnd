package pe.edu.upeu.geovisionBackEnd.dto.elaboracionOrdenTrabajo;

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
public class ParametroCalidadRequestDTO {

    @NotNull(message = "tolerancia es obligatorio")
    @Digits(integer = 6, fraction = 4, message = "tolerancia excede la precision permitida")
    @DecimalMin(value = "0", inclusive = true, message = "tolerancia debe ser mayor o igual a 0")
    private BigDecimal tolerancia;

    @NotNull(message = "valorEsperado es obligatorio")
    @Digits(integer = 6, fraction = 4, message = "valorEsperado excede la precision permitida")
    private BigDecimal valorEsperado;

    @NotNull(message = "valorObtenido es obligatorio")
    @Digits(integer = 6, fraction = 4, message = "valorObtenido excede la precision permitida")
    private BigDecimal valorObtenido;

    @Digits(integer = 6, fraction = 4, message = "periodo excede la precision permitida")
    @DecimalMin(value = "0", inclusive = false, message = "periodo debe ser mayor a 0")
    private BigDecimal periodo;

    @NotNull(message = "aprobado es obligatorio")
    private Boolean aprobado;

    @NotNull(message = "idControlCalidad es obligatorio")
    @Positive(message = "idControlCalidad debe ser positivo")
    private Long idControlCalidad;

    @NotNull(message = "idTipoParametroCalidad es obligatorio")
    @Positive(message = "idTipoParametroCalidad debe ser positivo")
    private Long idTipoParametroCalidad;

    @NotNull(message = "idMedidaOjo es obligatorio")
    @Positive(message = "idMedidaOjo debe ser positivo")
    private Long idMedidaOjo;

    @JsonIgnore
    @AssertTrue(message = "La tolerancia no puede superar la mitad del periodo")
    public boolean isToleranciaValida() {
        return periodo == null || tolerancia == null || periodo.signum() <= 0
                || tolerancia.compareTo(periodo.divide(BigDecimal.valueOf(2))) <= 0;
    }

    @JsonIgnore
    @AssertTrue(message = "La aprobacion debe coincidir con los valores y la tolerancia")
    public boolean isAprobacionCoherente() {
        if (aprobado == null || valorEsperado == null || valorObtenido == null
                || tolerancia == null || tolerancia.signum() < 0
                || (periodo != null && periodo.signum() <= 0)) {
            return true; // Las restricciones de campo informan los valores ausentes o invalidos.
        }
        BigDecimal diferencia = valorObtenido.subtract(valorEsperado).abs();
        if (periodo != null) {
            BigDecimal resto = diferencia.remainder(periodo);
            diferencia = resto.min(periodo.subtract(resto));
        }
        return aprobado.equals(diferencia.compareTo(tolerancia) <= 0);
    }
}

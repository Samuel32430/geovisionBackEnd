package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.constraints.Max;
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

    private Long idMedidaOjo;

    @Size(max = 50, message = "La agudeza visual no debe superar los 50 caracteres")
    private String agudezaVisual;

    private BigDecimal esfera;

    private BigDecimal cilindro;

    @Min(value = 0, message = "El eje no puede ser negativo")
    @Max(value = 180, message = "El eje no puede ser mayor a 180 grados")
    private Integer eje;

    private BigDecimal adicion;
}

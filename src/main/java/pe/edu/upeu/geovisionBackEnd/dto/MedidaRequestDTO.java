package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.geovisionBackEnd.enums.TipoMedidaEnum;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedidaRequestDTO {

    @NotNull(message = "El tipo de medida es obligatorio")
    @Builder.Default
    private TipoMedidaEnum tipoMedida = TipoMedidaEnum.LEJOS;

    @NotNull(message = "La distancia pupilar es obligatoria")
    @DecimalMin(value = "0", inclusive = false, message = "La distancia pupilar debe ser positiva")
    @Digits(integer = 3, fraction = 2, message = "La distancia pupilar admite hasta 3 enteros y 2 decimales")
    private BigDecimal distanciaPupilar;

    @DecimalMin(value = "0", message = "El prisma no puede ser negativo")
    @Digits(integer = 3, fraction = 2, message = "El prisma admite hasta 3 enteros y 2 decimales")
    private BigDecimal prisma;

    @Size(max = 255, message = "Las observaciones no deben superar los 255 caracteres")
    private String observaciones;

    @Positive(message = "El ID de la receta debe ser positivo")
    private Long idReceta;

    @Valid
    @NotNull(message = "Las medidas del ojo derecho son obligatorias")
    private MedidaOjoDTO ojoDerecho;

    @Valid
    @NotNull(message = "Las medidas del ojo izquierdo son obligatorias")
    private MedidaOjoDTO ojoIzquierdo;

    @Positive(message = "El ID del envio debe ser positivo")
    private Long idEnvioLaboratorio;
}

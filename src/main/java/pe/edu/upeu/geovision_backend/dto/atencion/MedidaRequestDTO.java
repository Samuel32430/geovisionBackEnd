package pe.edu.upeu.geovision_backend.dto.atencion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoMedidaEnum;

import java.math.BigDecimal;

public record MedidaRequestDTO(
    @NotNull(message = "El tipo de medida es obligatorio")
    TipoMedidaEnum tipoMedida,
    @NotNull(message = "La distancia pupilar es obligatoria")
    @DecimalMin(value = "0", inclusive = false, message = "La distancia pupilar debe ser positiva")
    @Digits(integer = 3, fraction = 2, message = "La distancia pupilar admite hasta 3 enteros y 2 decimales")
    BigDecimal distanciaPupilar,
    @DecimalMin(value = "0", message = "El prisma no puede ser negativo")
    @Digits(integer = 3, fraction = 2, message = "El prisma admite hasta 3 enteros y 2 decimales")
    BigDecimal prisma,
    @Size(max = 255, message = "Las observaciones no deben superar los 255 caracteres")
    String observaciones,
    @Positive(message = "El ID de la receta debe ser positivo")
    Long idReceta,
    @Valid
    @NotNull(message = "Las medidas del ojo derecho son obligatorias")
    MedidaOjoDTO ojoDerecho,
    @Valid
    @NotNull(message = "Las medidas del ojo izquierdo son obligatorias")
    MedidaOjoDTO ojoIzquierdo,
    @Positive(message = "El ID del envio debe ser positivo")
    Long idEnvioLaboratorio
) {
}

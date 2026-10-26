package pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta;

import lombok.Data;
import tools.jackson.databind.annotation.JsonDeserialize;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class MedidaOjoRequestDTO {
    @NotNull @Positive
    @JsonDeserialize(using = IdentificadorPacienteDeserializer.class)
    private Long idMedida;

    @NotNull @Positive
    @JsonDeserialize(using = IdentificadorPacienteDeserializer.class)
    private Long idTipoOjo;

    private BigDecimal agudezaVisual;

    @Digits(integer = 5, fraction = 2)
    private BigDecimal esfera;

    @Digits(integer = 5, fraction = 2)
    private BigDecimal cilindro;

    @DecimalMin("0") @DecimalMax("180") @Digits(integer = 4, fraction = 2)
    private BigDecimal eje;

    @Digits(integer = 5, fraction = 2)
    private BigDecimal adicion;

    @DecimalMin(value = "0", inclusive = false) @Digits(integer = 4, fraction = 2)
    private BigDecimal distanciaPupilarMm;

    @DecimalMin("0") @Digits(integer = 5, fraction = 2)
    private BigDecimal prisma;

    @Size(max = 100)
    private String basePrisma;
}


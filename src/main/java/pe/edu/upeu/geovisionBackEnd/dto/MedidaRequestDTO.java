package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
public class MedidaRequestDTO {

    @NotNull(message = "La distancia pupilar es obligatoria")
    private BigDecimal distanciaPupilar;

    private BigDecimal prisma;

    @Size(max = 255, message = "Las observaciones no deben superar los 255 caracteres")
    private String observaciones;

    private Long idReceta;

    @Valid
    @NotNull(message = "Las medidas del ojo derecho son obligatorias")
    private MedidaOjoDTO ojoDerecho;

    @Valid
    @NotNull(message = "Las medidas del ojo izquierdo son obligatorias")
    private MedidaOjoDTO ojoIzquierdo;

    private Long idEnvioLaboratorio;
}

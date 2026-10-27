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
public class ControlCalidadRequestDTO {

    @NotNull(message = "El ID del envío al laboratorio es obligatorio")
    private Long idEnvioLaboratorio;

    @NotNull(message = "Debe indicar si el control de calidad es aprobado")
    private Boolean aprobado;

    private BigDecimal tolerancia;

    @Size(max = 500, message = "Las observaciones no deben superar los 500 caracteres")
    private String observaciones;

    @Valid
    private MedidaRequestDTO medidasLensometria;
}

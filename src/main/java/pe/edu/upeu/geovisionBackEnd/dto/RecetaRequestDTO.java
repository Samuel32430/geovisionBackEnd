package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecetaRequestDTO {

    @NotNull(message = "El ID del paciente es obligatorio")
    @Positive(message = "El ID del paciente debe ser positivo")
    private Long idPaciente;

    @NotNull(message = "El ID del empleado/optómetra es obligatorio")
    @Positive(message = "El ID del empleado debe ser positivo")
    private Long idEmpleado;

    @Builder.Default
    @NotNull(message = "El indicador externo es obligatorio")
    @Min(value = 0, message = "El indicador externo debe ser 0 o 1")
    @Max(value = 1, message = "El indicador externo debe ser 0 o 1")
    private Integer esExterno = 0;

    private LocalDateTime fechaRegistro;

    @NotEmpty(message = "Debe registrar al menos una medida clínica")
    private List<@NotNull @Valid MedidaRequestDTO> medidas;
}

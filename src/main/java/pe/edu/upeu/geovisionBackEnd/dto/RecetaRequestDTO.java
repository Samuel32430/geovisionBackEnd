package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    private Long idPaciente;

    @NotNull(message = "El ID del empleado/optómetra es obligatorio")
    private Long idEmpleado;

    @Builder.Default
    private Integer esExterno = 0;

    private LocalDateTime fechaRegistro;

    @Valid
    @NotEmpty(message = "Debe registrar al menos una medida clínica")
    private List<MedidaRequestDTO> medidas;
}

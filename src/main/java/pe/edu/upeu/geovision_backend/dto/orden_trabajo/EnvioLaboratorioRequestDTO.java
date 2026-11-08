package pe.edu.upeu.geovision_backend.dto.orden_trabajo;

import lombok.Builder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Builder(toBuilder = true)
public record EnvioLaboratorioRequestDTO(
    @NotNull(message = "El ID de la orden de trabajo es obligatorio")
    @jakarta.validation.constraints.Positive
    Long idOrdenTrabajo,
    @NotNull(message = "El ID del laboratorio es obligatorio")
    @jakarta.validation.constraints.Positive
    Long idLaboratorio,
    LocalDate fechaRecepcionEstimada,
    @Size(max = 500, message = "Las observaciones no deben superar los 500 caracteres")
    String observaciones
) {
}

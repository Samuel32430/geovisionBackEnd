package pe.edu.upeu.geovision_backend.dto.orden_trabajo;

import lombok.Builder;
import pe.edu.upeu.geovision_backend.dto.atencion.MedidaRequestDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;


@Builder(toBuilder = true)
public record ControlCalidadRequestDTO(
    @NotNull @Positive
    Long idEnvioLaboratorio,
    @NotEmpty @Size(max=3) @Valid
    List<@NotNull MedidaRequestDTO> medidasLensometria,
    @Size(max=500)
    String observaciones
) {
}

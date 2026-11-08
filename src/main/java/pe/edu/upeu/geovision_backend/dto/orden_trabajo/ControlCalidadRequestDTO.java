package pe.edu.upeu.geovision_backend.dto.orden_trabajo;

import pe.edu.upeu.geovision_backend.dto.atencion.MedidaRequestDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ControlCalidadRequestDTO {
    @NotNull @Positive
    private Long idEnvioLaboratorio;
    @NotEmpty @Size(max=3) @Valid
    private List<@NotNull MedidaRequestDTO> medidasLensometria;
    @Size(max=500)
    private String observaciones;
}

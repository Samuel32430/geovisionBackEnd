package pe.edu.upeu.geovisionBackEnd.dto;

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

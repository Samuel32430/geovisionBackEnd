package pe.edu.upeu.geovisionBackEnd.dto.elaboracionOrdenTrabajo;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ControlCalidadRequestDTO {

    @NotBlank(message = "estado es obligatorio")
    @Size(max = 15, message = "estado no puede superar 15 caracteres")
    @Pattern(regexp = "EN_REVISION|APROBADO|OBSERVADO", message = "estado tiene un valor no permitido")
    private String estado = "EN_REVISION";

    @Size(max = 2000, message = "observaciones no puede superar 2000 caracteres")
    private String observaciones;

    @NotNull(message = "aprobado es obligatorio")
    private Boolean aprobado;

    @NotNull(message = "idEnvioLaboratorio es obligatorio")
    @Positive(message = "idEnvioLaboratorio debe ser positivo")
    private Long idEnvioLaboratorio;

    @NotNull(message = "idEmpleado es obligatorio")
    @Positive(message = "idEmpleado debe ser positivo")
    private Long idEmpleado;

    @JsonIgnore
    @AssertTrue(message = "Aprobado debe ser verdadero solo cuando el estado sea APROBADO")
    public boolean isAprobacionCoherente() {
        return estado == null || aprobado == null || aprobado.equals("APROBADO".equals(estado));
    }
}

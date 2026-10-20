package pe.edu.upeu.geovisionBackEnd.dto.elaboracionOrdenTrabajo;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnvioLaboratorioRequestDTO {

    @NotNull(message = "fechaEnvio es obligatorio")
    private LocalDateTime fechaEnvio;

    private LocalDateTime fechaRecepcion;

    @NotBlank(message = "motivo es obligatorio")
    @Size(max = 20, message = "motivo no puede superar 20 caracteres")
    @Pattern(regexp = "FABRICACION|CORRECCION", message = "motivo tiene un valor no permitido")
    private String motivo = "FABRICACION";

    @Size(max = 1000, message = "descripcion no puede superar 1000 caracteres")
    private String descripcion;

    @NotNull(message = "costoOptica es obligatorio")
    @Digits(integer = 12, fraction = 2, message = "costoOptica excede la precision permitida")
    @DecimalMin(value = "0", inclusive = true, message = "costoOptica debe ser mayor o igual a 0")
    private BigDecimal costoOptica = BigDecimal.ZERO;

    @NotNull(message = "idOrdenTrabajo es obligatorio")
    @Positive(message = "idOrdenTrabajo debe ser positivo")
    private Long idOrdenTrabajo;

    @NotNull(message = "idLaboratorio es obligatorio")
    @Positive(message = "idLaboratorio debe ser positivo")
    private Long idLaboratorio;

    @JsonIgnore
    @AssertTrue(message = "La fecha de recepcion no puede ser anterior a la fecha de envio")
    public boolean isFechasValidas() {
        return fechaRecepcion == null || fechaEnvio == null || !fechaRecepcion.isBefore(fechaEnvio);
    }
}

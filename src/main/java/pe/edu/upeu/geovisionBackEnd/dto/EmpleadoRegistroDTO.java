package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/** Alta laboral explícita, independiente de los roles de acceso. */
@Data
public class EmpleadoRegistroDTO {
    @NotNull(message = "La fecha de contrato es obligatoria para registrar al empleado")
    private LocalDate fechaContrato;
}

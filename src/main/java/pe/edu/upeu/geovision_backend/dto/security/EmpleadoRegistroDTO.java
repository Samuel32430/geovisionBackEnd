package pe.edu.upeu.geovision_backend.dto.security;

import lombok.Builder;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/** Alta laboral explícita, independiente de los roles de acceso. */

@Builder(toBuilder = true)
public record EmpleadoRegistroDTO(
    @NotNull(message = "La fecha de contrato es obligatoria para registrar al empleado")
    LocalDate fechaContrato
) {
}

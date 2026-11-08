package pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento;

import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Builder(toBuilder = true)
public record CargoDTO(
    Long idCargo,
    @NotBlank(message = "El nombre del cargo es obligatorio")
    @Size(max = 100, message = "El nombre del cargo no debe superar los 100 caracteres")
    String nombre
) {
}

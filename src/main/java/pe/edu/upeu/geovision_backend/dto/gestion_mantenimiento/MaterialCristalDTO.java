package pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento;

import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Builder(toBuilder = true)
public record MaterialCristalDTO(
    Long idMaterialCristal,
    @NotBlank(message = "El nombre del material de cristal es obligatorio")
    @Size(max = 100, message = "El nombre del material de cristal no debe superar los 100 caracteres")
    String nombre
) {
}

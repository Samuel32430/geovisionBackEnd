package pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MaterialCristalDTO(
    Long id,
    @NotBlank(message = "El nombre del material de cristal es obligatorio")
    @Size(max = 100, message = "El nombre del material de cristal no debe superar los 100 caracteres")
    String nombre
) {
}

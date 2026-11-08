package pe.edu.upeu.geovision_backend.dto.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record RolDTO(
    Long id,
    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 100, message = "El nombre del rol no debe superar los 100 caracteres")
    String nombre,
    Set<PermisoDTO> permisos
) {
}

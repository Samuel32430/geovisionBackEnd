package pe.edu.upeu.geovision_backend.dto.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pe.edu.upeu.geovision_backend.enums.security.ModuloEnum;

public record PermisoDTO(
    Long id,
    @NotBlank(message = "El nombre del permiso es obligatorio")
    @Size(max = 100, message = "El nombre del permiso no debe superar los 100 caracteres")
    String nombre,
    @NotNull(message = "El módulo es obligatorio")
    ModuloEnum modulo,
    String moduloDescripcion
) {
}

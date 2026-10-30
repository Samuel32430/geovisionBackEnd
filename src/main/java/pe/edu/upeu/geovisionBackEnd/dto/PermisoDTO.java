package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.geovisionBackEnd.enums.ModuloEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermisoDTO {

    private Long idPermiso;

    @NotBlank(message = "El nombre del permiso es obligatorio")
    @Size(max = 100, message = "El nombre del permiso no debe superar los 100 caracteres")
    private String nombre;

    @NotNull(message = "El módulo es obligatorio")
    private ModuloEnum modulo;

    private String moduloDescripcion;
}

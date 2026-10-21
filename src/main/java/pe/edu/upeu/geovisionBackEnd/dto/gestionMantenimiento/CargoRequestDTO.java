package pe.edu.upeu.geovisionBackEnd.dto.gestionMantenimiento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CargoRequestDTO {

    @NotBlank(message = "El nombre del cargo es obligatorio")
    @Size(max = 200, message = "El nombre no puede superar los 200 caracteres")
    private String nombre;
}

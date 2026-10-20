package pe.edu.upeu.geovisionBackEnd.dto.gestionMantenimiento;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoOrdenTrabajoRequestDTO {

    @NotBlank(message = "nombre es obligatorio")
    @Size(max = 200, message = "nombre no puede superar 200 caracteres")
    private String nombre;
}

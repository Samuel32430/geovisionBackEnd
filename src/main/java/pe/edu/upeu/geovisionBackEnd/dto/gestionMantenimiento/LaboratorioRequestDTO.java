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
public class LaboratorioRequestDTO {

    @NotBlank(message = "nombre es obligatorio")
    @Size(max = 200, message = "nombre no puede superar 200 caracteres")
    private String nombre;

    @NotBlank(message = "ruc es obligatorio")
    @Size(max = 20, message = "ruc no puede superar 20 caracteres")
    private String ruc;

    @Size(max = 50, message = "telefono no puede superar 50 caracteres")
    private String telefono;

    @Size(max = 500, message = "direccion no puede superar 500 caracteres")
    private String direccion;

    @Size(max = 200, message = "correo no puede superar 200 caracteres")
    @Email(message = "correo debe tener un formato valido")
    private String correo;

    @NotNull(message = "estado es obligatorio")
    private Boolean estado = true;
}

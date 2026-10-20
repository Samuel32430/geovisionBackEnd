package pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class PersonaRequestDTO {
    @NotBlank @Size(max = 200)
    private String nombres;

    @NotBlank @Size(max = 200)
    private String apellidos;

    @Size(max = 50)
    private String telefono;

    private LocalDate fechaNacimiento;

    @Min(0) @Max(1)
    private Integer sexo;

    @Size(max = 500)
    private String direccion;
}


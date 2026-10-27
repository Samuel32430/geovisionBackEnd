package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.geovisionBackEnd.enums.SexoEnum;
import pe.edu.upeu.geovisionBackEnd.enums.TipoDocumentoEnum;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaRequestDTO {

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 150, message = "Los nombres no deben superar los 150 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 150, message = "Los apellidos no deben superar los 150 caracteres")
    private String apellidos;

    @Size(max = 50, message = "El teléfono no debe superar los 50 caracteres")
    private String telefono;

    private LocalDate fechaNacimiento;

    private SexoEnum sexo;

    @Size(max = 200, message = "La dirección no debe superar los 200 caracteres")
    private String direccion;

    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDocumentoEnum tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 50, message = "El número de documento no debe superar los 50 caracteres")
    private String numeroDocumento;
}

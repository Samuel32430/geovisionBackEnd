package pe.edu.upeu.geovision_backend.dto.atencion;

import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pe.edu.upeu.geovision_backend.enums.atencion.SexoEnum;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoDocumentoEnum;

import java.time.LocalDate;

@Builder(toBuilder = true)
public record PersonaRequestDTO(
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 150, message = "Los nombres no deben superar los 150 caracteres")
    String nombres,
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 150, message = "Los apellidos no deben superar los 150 caracteres")
    String apellidos,
    @Size(max = 50, message = "El teléfono no debe superar los 50 caracteres")
    String telefono,
    LocalDate fechaNacimiento,
    SexoEnum sexo,
    @Size(max = 200, message = "La dirección no debe superar los 200 caracteres")
    String direccion,
    @NotNull(message = "El tipo de documento es obligatorio")
    TipoDocumentoEnum tipoDocumento,
    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 50, message = "El número de documento no debe superar los 50 caracteres")
    String numeroDocumento
) {
}

package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaboratorioDTO {

    private Long idLaboratorio;

    @NotBlank(message = "El nombre del laboratorio es obligatorio")
    @Size(max = 200, message = "El nombre del laboratorio no debe superar los 200 caracteres")
    private String nombre;

    @Size(max = 20, message = "El RUC no debe superar los 20 caracteres")
    private String ruc;

    @Size(max = 50, message = "El teléfono no debe superar los 50 caracteres")
    private String telefono;

    @Size(max = 200, message = "La dirección no debe superar los 200 caracteres")
    private String direccion;

    @Email(message = "El formato de correo no es válido")
    @Size(max = 100, message = "El correo no debe superar los 100 caracteres")
    private String correo;

    @Builder.Default
    private Integer estado = 1;
}

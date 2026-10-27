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
public class ConfiguracionEmpresaDTO {

    private Long idConfiguracionEmpresa;

    @NotBlank(message = "El nombre legal de la empresa es obligatorio")
    @Size(max = 200, message = "El nombre legal no debe superar los 200 caracteres")
    private String nombreLegal;

    @Size(max = 200, message = "El nombre comercial no debe superar los 200 caracteres")
    private String nombreComercial;

    @NotBlank(message = "El RUC de la empresa es obligatorio")
    @Size(max = 20, message = "El RUC no debe superar los 20 caracteres")
    private String ruc;

    @Size(max = 200, message = "La dirección no debe superar los 200 caracteres")
    private String direccion;

    @Size(max = 50, message = "El teléfono no debe superar los 50 caracteres")
    private String telefono;

    @Email(message = "El formato de correo no es válido")
    @Size(max = 100, message = "El correo no debe superar los 100 caracteres")
    private String correo;

    @Size(max = 200, message = "La ruta o URL del logo no debe superar los 200 caracteres")
    private String logo;
}

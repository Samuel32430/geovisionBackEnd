package pe.edu.upeu.geovision_backend.dto.gestion_configuracion;

import lombok.Builder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Builder(toBuilder = true)
public record ConfiguracionEmpresaDTO(
    Long idConfiguracionEmpresa,
    @NotBlank(message = "El nombre legal de la empresa es obligatorio")
    @Size(max = 200, message = "El nombre legal no debe superar los 200 caracteres")
    String nombreLegal,
    @Size(max = 200, message = "El nombre comercial no debe superar los 200 caracteres")
    String nombreComercial,
    @NotBlank(message = "El RUC de la empresa es obligatorio")
    @Pattern(regexp = "[0-9]{11}", message = "El RUC debe contener exactamente 11 dígitos")
    String ruc,
    @Size(max = 200, message = "La dirección no debe superar los 200 caracteres")
    String direccion,
    @Size(max = 50, message = "El teléfono no debe superar los 50 caracteres")
    String telefono,
    @Email(message = "El formato de correo no es válido")
    @Size(max = 100, message = "El correo no debe superar los 100 caracteres")
    String correo,
    @Size(max = 200, message = "La ruta o URL del logo no debe superar los 200 caracteres")
    String logo
) {
}

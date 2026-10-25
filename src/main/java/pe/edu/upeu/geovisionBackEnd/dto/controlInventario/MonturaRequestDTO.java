package pe.edu.upeu.geovisionBackEnd.dto.controlInventario;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO para los atributos específicos al registrar o actualizar una Montura.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonturaRequestDTO {

    @Size(max = 100, message = "El color no puede superar los 100 caracteres")
    private String color;

    @Size(max = 200, message = "Las medidas no pueden superar los 200 caracteres")
    private String medidas;

    @Positive(message = "El identificador de modelo de montura debe ser positivo")
    private Long idModeloMontura;

    @Positive(message = "El identificador de material de montura debe ser positivo")
    private Long idMaterialMontura;
}

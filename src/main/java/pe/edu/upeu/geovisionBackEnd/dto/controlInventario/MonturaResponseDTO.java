package pe.edu.upeu.geovisionBackEnd.dto.controlInventario;

import lombok.*;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.TipoItem;

/**
 * DTO de respuesta para los atributos específicos de una Montura.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonturaResponseDTO {

    private Long idItem;
    private TipoItem tipo;
    private String color;
    private String medidas;
    private Long idModeloMontura;
    private Long idMaterialMontura;
}

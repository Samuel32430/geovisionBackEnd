package pe.edu.upeu.geovisionBackEnd.dto.controlInventario;

import lombok.*;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.TipoItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de respuesta para los atributos específicos de un Cristal.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CristalResponseDTO {

    private Long idItem;
    private TipoItem tipo;
    private BigDecimal indiceRefraccion;
    private Long idMaterialCristal;
    private List<Long> idsTratamientos;
}

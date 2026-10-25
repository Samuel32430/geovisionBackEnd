package pe.edu.upeu.geovisionBackEnd.dto.controlInventario;

import lombok.*;

/**
 * DTO detallado que agrupa los datos del Item base junto con los subtipos de Cristal o Montura si aplican.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDetalleResponseDTO {

    private ItemResponseDTO item;
    private CristalResponseDTO cristal;
    private MonturaResponseDTO montura;
}

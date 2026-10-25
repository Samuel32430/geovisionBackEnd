package pe.edu.upeu.geovisionBackEnd.dto.controlInventario;

import lombok.*;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.TipoItem;

/**
 * DTO para encapsular parámetros de filtrado y búsqueda en el catálogo (RF-67).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemFiltroDTO {

    private String textoBusqueda;
    private String codigo;
    private Long idCategoria;
    private Long idMarca;
    private TipoItem tipo;
    private Boolean estado;
    private Boolean soloDisponibles;
}

package pe.edu.upeu.geovisionBackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponseDTO {

    private Long idProducto;
    private String codigo;
    private String nombre;
    private String marca;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private Integer estado;
    private String estadoDescripcion;
    private LocalDateTime fechaRegistro;
    private Long idCategoria;
    private String nombreCategoria;
    private MonturaDTO montura;
    private Boolean esMontura;

    public Boolean getEsMontura() {
        if (esMontura != null) {
            return esMontura;
        }
        return montura != null;
    }
}

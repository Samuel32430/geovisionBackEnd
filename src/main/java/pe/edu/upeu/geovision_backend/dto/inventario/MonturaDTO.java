package pe.edu.upeu.geovision_backend.dto.inventario;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.geovision_backend.enums.inventario.MaterialMonturaEnum;
import pe.edu.upeu.geovision_backend.enums.inventario.ModeloMonturaEnum;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonturaDTO {

    private Long idProducto;

    private String codigo;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;

    @Size(max = 100, message = "El color no debe superar los 100 caracteres")
    private String color;

    private ModeloMonturaEnum modelo;
    private String modeloDescripcion;

    private MaterialMonturaEnum material;
    private String materialDescripcion;
}

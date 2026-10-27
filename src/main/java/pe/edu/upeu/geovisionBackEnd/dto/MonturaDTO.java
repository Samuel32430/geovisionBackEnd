package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.geovisionBackEnd.enums.MaterialMonturaEnum;
import pe.edu.upeu.geovisionBackEnd.enums.ModeloMonturaEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonturaDTO {

    private Long idProducto;

    @Size(max = 100, message = "El color no debe superar los 100 caracteres")
    private String color;

    private ModeloMonturaEnum modelo;
    private String modeloDescripcion;

    private MaterialMonturaEnum material;
    private String materialDescripcion;
}

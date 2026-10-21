package pe.edu.upeu.geovisionBackEnd.dto.gestionMantenimiento;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServicioResponseDTO {

    private Long idServicio;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer estado;
}

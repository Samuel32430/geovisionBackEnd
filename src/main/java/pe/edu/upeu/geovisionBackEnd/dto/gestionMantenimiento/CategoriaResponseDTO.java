package pe.edu.upeu.geovisionBackEnd.dto.gestionMantenimiento;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaResponseDTO {

    private Long idCategoria;
    private String nombre;
    private String descripcion;
    private Integer estado;
    private LocalDate fechaRegistro;
}

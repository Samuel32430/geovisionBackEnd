package pe.edu.upeu.geovisionBackEnd.dto.gestionMantenimiento;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LaboratorioResponseDTO {

    private Long idLaboratorio;

    private String nombre;

    private String ruc;

    private String telefono;

    private String direccion;

    private String correo;

    private Boolean estado;
}

package pe.edu.upeu.geovisionBackEnd.dto.gestionMantenimiento;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoComprobanteResponseDTO {

    private Long idEstadoComprobante;
    private String nombre;
}

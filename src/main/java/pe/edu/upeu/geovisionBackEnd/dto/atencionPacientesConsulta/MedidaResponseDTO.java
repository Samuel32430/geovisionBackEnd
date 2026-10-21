package pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MedidaResponseDTO {
    private Long idMedida;
    private Long idExamen;
    private Long idTipoMedida;
    private String observaciones;
}


package pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta;

import lombok.Data;
import java.util.List;

@Data
public class MedidaDetalleResponseDTO {
    private MedidaResponseDTO medida;
    private List<MedidaOjoResponseDTO> ojos;
}


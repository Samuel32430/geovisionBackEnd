package pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta;

import lombok.Data;
import java.util.List;

@Data
public class ExamenDetalleResponseDTO {
    private ExamenResponseDTO examen;
    private List<MedidaDetalleResponseDTO> medidas;
    private RecetaResponseDTO receta;
}


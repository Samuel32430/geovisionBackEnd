package pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RecetaEntregaRequestDTO {
    @NotNull
    private LocalDateTime fechaDeEntrega;
}


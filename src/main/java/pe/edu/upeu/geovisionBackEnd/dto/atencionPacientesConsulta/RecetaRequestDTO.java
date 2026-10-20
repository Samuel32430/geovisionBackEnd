package pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RecetaRequestDTO {
    @NotNull @Positive
    private Long idExamen;

    @Size(max = 2000)
    private String observaciones;

    private LocalDateTime fechaDeEntrega;
}


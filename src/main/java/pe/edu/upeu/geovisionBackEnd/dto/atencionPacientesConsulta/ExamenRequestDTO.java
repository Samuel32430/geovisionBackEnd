package pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
public class ExamenRequestDTO {
    @NotNull @Positive
    private Long idPaciente;

    @NotNull @Min(0) @Max(1)
    private Integer esExterno = 0;

    @Size(max = 200)
    private String profesionalExterno;

    @Size(max = 1000)
    private String documentoExterno;

    @Size(max = 2000)
    private String observaciones;
}


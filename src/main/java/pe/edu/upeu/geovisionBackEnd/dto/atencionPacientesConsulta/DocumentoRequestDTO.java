package pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
public class DocumentoRequestDTO {
    @NotBlank @Size(max = 50)
    private String numero;

    @NotNull @Positive
    private Long idPersona;

    @NotNull @Positive
    private Long idTipoDocumento;
}


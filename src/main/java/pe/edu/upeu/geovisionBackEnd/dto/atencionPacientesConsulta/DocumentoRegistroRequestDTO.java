package pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta;

import jakarta.validation.constraints.*;
import lombok.Data;
import tools.jackson.databind.annotation.JsonDeserialize;

@Data
public class DocumentoRegistroRequestDTO {
    @NotBlank @Size(max = 50)
    private String numero;
    @NotNull @Positive
    @JsonDeserialize(using = IdentificadorPacienteDeserializer.class)
    private Long idTipoDocumento;
}


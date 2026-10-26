package pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta;

import lombok.Data;
import tools.jackson.databind.annotation.JsonDeserialize;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
public class PacienteRequestDTO {
    @NotNull @Positive
    @JsonDeserialize(using = IdentificadorPacienteDeserializer.class)
    private Long idPersona;
}


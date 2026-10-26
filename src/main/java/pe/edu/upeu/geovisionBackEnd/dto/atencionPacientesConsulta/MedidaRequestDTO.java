package pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta;

import lombok.Data;
import tools.jackson.databind.annotation.JsonDeserialize;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
public class MedidaRequestDTO {
    @NotNull @Positive
    @JsonDeserialize(using = IdentificadorPacienteDeserializer.class)
    private Long idExamen;

    @NotNull @Positive
    @JsonDeserialize(using = IdentificadorPacienteDeserializer.class)
    private Long idTipoMedida;

    @Size(max = 1000)
    private String observaciones;
}


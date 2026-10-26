package pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class PacienteRegistroRequestDTO {
    @Valid @NotNull
    private PersonaRequestDTO persona;
    @NotNull @Size(max = 10)
    private List<@NotNull @Valid DocumentoRegistroRequestDTO> documentos = new ArrayList<>();
}


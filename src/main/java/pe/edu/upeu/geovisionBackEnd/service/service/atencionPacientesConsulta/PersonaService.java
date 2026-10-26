package pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import java.util.List;

public interface PersonaService {
    PersonaResponseDTO create(@Valid @NotNull PersonaRequestDTO request);
    PersonaResponseDTO update(@Positive @NotNull Long id, @Valid @NotNull PersonaRequestDTO request);
    PersonaResponseDTO read(@Positive @NotNull Long id);
}


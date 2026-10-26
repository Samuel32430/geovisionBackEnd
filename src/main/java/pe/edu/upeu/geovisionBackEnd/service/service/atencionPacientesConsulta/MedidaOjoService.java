package pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import java.util.List;

public interface MedidaOjoService {
    MedidaOjoResponseDTO create(@Valid @NotNull MedidaOjoRequestDTO request);
    MedidaOjoResponseDTO update(@Positive @NotNull Long id, @Valid @NotNull MedidaOjoRequestDTO request);
    MedidaOjoResponseDTO read(@Positive @NotNull Long id);
    List<MedidaOjoResponseDTO> listarPorMedida(@Positive @NotNull Long medida);
}


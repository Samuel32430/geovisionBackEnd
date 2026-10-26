package pe.edu.upeu.geovisionBackEnd.service.service.atencionPacientesConsulta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import pe.edu.upeu.geovisionBackEnd.dto.atencionPacientesConsulta.*;
import java.util.List;

public interface MedidaService {
    MedidaResponseDTO create(@Valid @NotNull MedidaRequestDTO request);
    MedidaResponseDTO update(@Positive @NotNull Long id, @Valid @NotNull MedidaRequestDTO request);
    MedidaDetalleResponseDTO read(@Positive @NotNull Long id);
    List<MedidaResponseDTO> listarPorExamen(@Positive @NotNull Long examen);
}


package pe.edu.upeu.geovisionBackEnd.service.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import pe.edu.upeu.geovisionBackEnd.dto.ConfiguracionEmpresaDTO;

public interface ConfiguracionEmpresaService {
    ConfiguracionEmpresaDTO obtener();
    ConfiguracionEmpresaDTO registrar(@NotNull @Valid ConfiguracionEmpresaDTO request);
    ConfiguracionEmpresaDTO actualizar(@NotNull @Valid ConfiguracionEmpresaDTO request);
}

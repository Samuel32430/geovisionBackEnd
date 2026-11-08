package pe.edu.upeu.geovision_backend.service.gestion_configuracion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import pe.edu.upeu.geovision_backend.dto.gestion_configuracion.ConfiguracionEmpresaDTO;

public interface ConfiguracionEmpresaService {
    ConfiguracionEmpresaDTO obtener();
    ConfiguracionEmpresaDTO registrar(@NotNull @Valid ConfiguracionEmpresaDTO request);
    ConfiguracionEmpresaDTO actualizar(@NotNull @Valid ConfiguracionEmpresaDTO request);
}

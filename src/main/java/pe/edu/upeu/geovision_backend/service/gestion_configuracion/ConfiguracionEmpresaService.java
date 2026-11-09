package pe.edu.upeu.geovision_backend.service.gestion_configuracion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import pe.edu.upeu.geovision_backend.dto.gestion_configuracion.ConfiguracionEmpresaDTO;

public interface ConfiguracionEmpresaService {

    // >>>> QUERIES
    ConfiguracionEmpresaDTO obtener();

    // >>>> CREATION
    ConfiguracionEmpresaDTO registrar(@NotNull @Valid ConfiguracionEmpresaDTO request);

    // >>>> UPDATES
    ConfiguracionEmpresaDTO actualizar(@NotNull @Valid ConfiguracionEmpresaDTO request);

}

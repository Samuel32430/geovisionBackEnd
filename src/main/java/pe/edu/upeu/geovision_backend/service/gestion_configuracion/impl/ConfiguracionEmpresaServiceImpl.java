package pe.edu.upeu.geovision_backend.service.gestion_configuracion.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pe.edu.upeu.geovision_backend.dto.gestion_configuracion.ConfiguracionEmpresaDTO;
import pe.edu.upeu.geovision_backend.entity.gestion_configuracion.ConfiguracionEmpresa;
import pe.edu.upeu.geovision_backend.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.mapper.gestion_configuracion.ConfiguracionEmpresaMapper;
import pe.edu.upeu.geovision_backend.repository.gestion_configuracion.ConfiguracionEmpresaLock;
import pe.edu.upeu.geovision_backend.repository.gestion_configuracion.ConfiguracionEmpresaRepository;
import pe.edu.upeu.geovision_backend.service.gestion_configuracion.ConfiguracionEmpresaService;

@Service
@Validated
@RequiredArgsConstructor
public class ConfiguracionEmpresaServiceImpl implements ConfiguracionEmpresaService {
    private final ConfiguracionEmpresaRepository repository;
    private final ConfiguracionEmpresaLock lock;
    private final ConfiguracionEmpresaMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public ConfiguracionEmpresaDTO obtener() {
        comprobarIntegridad();
        return mapper.toDTO(empresaExistente());
    }

    @Override
    @Transactional
    public ConfiguracionEmpresaDTO registrar(ConfiguracionEmpresaDTO request) {
        lock.bloquear();
        if (repository.count() > 0) {
            throw new ReglaNegocioException("La empresa ya está configurada. Utilice la opción de actualización.");
        }
        ConfiguracionEmpresa empresa = new ConfiguracionEmpresa();
        mapper.actualizar(request, empresa);
        return mapper.toDTO(repository.saveAndFlush(empresa));
    }

    @Override
    @Transactional
    public ConfiguracionEmpresaDTO actualizar(ConfiguracionEmpresaDTO request) {
        lock.bloquear();
        comprobarIntegridad();
        ConfiguracionEmpresa empresa = empresaExistente();
        mapper.actualizar(request, empresa);
        return mapper.toDTO(repository.saveAndFlush(empresa));
    }

    private ConfiguracionEmpresa empresaExistente() {
        return repository.findFirstByOrderByIdConfiguracionEmpresaAsc()
                .orElseThrow(() -> new RecursosNoEncontradosException("Los datos de la empresa aún no han sido configurados"));
    }

    private void comprobarIntegridad() {
        if (repository.count() > 1) {
            throw new ReglaNegocioException("Existen múltiples empresas configuradas. Se requiere revisar los datos existentes.");
        }
    }
}

package pe.edu.upeu.geovision_backend.mapper.gestion_configuracion;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovision_backend.dto.gestion_configuracion.ConfiguracionEmpresaDTO;
import pe.edu.upeu.geovision_backend.entity.gestion_configuracion.ConfiguracionEmpresa;

@Component
public class ConfiguracionEmpresaMapper {
    public void actualizar(ConfiguracionEmpresaDTO source, ConfiguracionEmpresa target) {
        // El identificador del cuerpo nunca controla qué registro se modifica.
        target.setNombreLegal(source.nombreLegal().trim());
        target.setNombreComercial(limpiar(source.nombreComercial()));
        target.setRuc(source.ruc());
        target.setDireccion(limpiar(source.direccion()));
        target.setTelefono(limpiar(source.telefono()));
        target.setCorreo(limpiar(source.correo()));
        target.setLogo(limpiar(source.logo()));
    }

    public ConfiguracionEmpresaDTO toDTO(ConfiguracionEmpresa source) {
        return new ConfiguracionEmpresaDTO(source.getId(), source.getNombreLegal(), source.getNombreComercial(), source.getRuc(), source.getDireccion(), source.getTelefono(), source.getCorreo(), source.getLogo());
    }

    private String limpiar(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

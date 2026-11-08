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
        return ConfiguracionEmpresaDTO.builder()
                .idConfiguracionEmpresa(source.getIdConfiguracionEmpresa())
                .nombreLegal(source.getNombreLegal()).nombreComercial(source.getNombreComercial())
                .ruc(source.getRuc()).direccion(source.getDireccion()).telefono(source.getTelefono())
                .correo(source.getCorreo()).logo(source.getLogo()).build();
    }

    private String limpiar(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

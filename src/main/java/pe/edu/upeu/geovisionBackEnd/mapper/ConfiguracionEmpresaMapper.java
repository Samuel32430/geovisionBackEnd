package pe.edu.upeu.geovisionBackEnd.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.ConfiguracionEmpresaDTO;
import pe.edu.upeu.geovisionBackEnd.entity.ConfiguracionEmpresa;

@Component
public class ConfiguracionEmpresaMapper {
    public void actualizar(ConfiguracionEmpresaDTO source, ConfiguracionEmpresa target) {
        // El identificador del cuerpo nunca controla qué registro se modifica.
        target.setNombreLegal(source.getNombreLegal().trim());
        target.setNombreComercial(limpiar(source.getNombreComercial()));
        target.setRuc(source.getRuc());
        target.setDireccion(limpiar(source.getDireccion()));
        target.setTelefono(limpiar(source.getTelefono()));
        target.setCorreo(limpiar(source.getCorreo()));
        target.setLogo(limpiar(source.getLogo()));
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

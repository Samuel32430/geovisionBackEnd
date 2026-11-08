package pe.edu.upeu.geovision_backend.mapper.gestion_configuracion;

import pe.edu.upeu.geovision_backend.dto.gestion_configuracion.ConfiguracionEmpresaDTO;
import pe.edu.upeu.geovision_backend.entity.gestion_configuracion.ConfiguracionEmpresa;

public final class ConfiguracionEmpresaMapper {
    private ConfiguracionEmpresaMapper() {}

    public static void actualizar(ConfiguracionEmpresaDTO source, ConfiguracionEmpresa target) {
        // El identificador del cuerpo nunca controla qué registro se modifica.
        target.setNombreLegal(source.nombreLegal().trim());
        target.setNombreComercial(limpiar(source.nombreComercial()));
        target.setRuc(source.ruc());
        target.setDireccion(limpiar(source.direccion()));
        target.setTelefono(limpiar(source.telefono()));
        target.setCorreo(limpiar(source.correo()));
        target.setLogo(limpiar(source.logo()));
    }

    public static ConfiguracionEmpresaDTO toDTO(ConfiguracionEmpresa source) {
        return new ConfiguracionEmpresaDTO(source.getId(), source.getNombreLegal(), source.getNombreComercial(), source.getRuc(), source.getDireccion(), source.getTelefono(), source.getCorreo(), source.getLogo());
    }

    private static String limpiar(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

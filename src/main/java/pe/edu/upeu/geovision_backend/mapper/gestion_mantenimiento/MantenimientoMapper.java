package pe.edu.upeu.geovision_backend.mapper.gestion_mantenimiento;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.CategoriaDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.LaboratorioDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.MaterialCristalDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.ServicioDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.TratamientoCristalDTO;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.Categoria;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.Laboratorio;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.MaterialCristal;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.Servicio;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.TratamientoCristal;

@Component
public class MantenimientoMapper {
    public void actualizar(CategoriaDTO source, Categoria target) {
        target.setNombre(limpiar(source.nombre()));
        target.setDescripcion(limpiar(source.descripcion()));
        target.setActivo((source.estado() == null ? null : source.estado() == 1));
    }

    public CategoriaDTO toDTO(Categoria source) {
        return CategoriaDTO.builder().idCategoria(source.getIdCategoria())
                .nombre(source.getNombre())
                .descripcion(source.getDescripcion())
                .estado((source.getActivo() == null ? null : Boolean.TRUE.equals(source.getActivo()) ? 1 : 0))
                .fechaRegistro(source.getFechaRegistro()).build();
    }

    public void actualizar(MaterialCristalDTO source, MaterialCristal target) {
        target.setNombre(limpiar(source.nombre()));
    }

    public MaterialCristalDTO toDTO(MaterialCristal source) {
        return MaterialCristalDTO.builder().idMaterialCristal(source.getIdMaterialCristal())
                .nombre(source.getNombre()).build();
    }

    public void actualizar(TratamientoCristalDTO source, TratamientoCristal target) {
        target.setNombre(limpiar(source.nombre()));
    }

    public TratamientoCristalDTO toDTO(TratamientoCristal source) {
        return TratamientoCristalDTO.builder().idTratamientoCristal(source.getIdTratamientoCristal())
                .nombre(source.getNombre()).build();
    }

    public void actualizar(ServicioDTO source, Servicio target) {
        target.setNombre(limpiar(source.nombre()));
        target.setDescripcion(limpiar(source.descripcion()));
        target.setPrecio(source.precio());
        target.setActivo((source.estado() == null ? null : source.estado() == 1));
    }

    public ServicioDTO toDTO(Servicio source) {
        return ServicioDTO.builder().idServicio(source.getIdServicio())
                .nombre(source.getNombre())
                .descripcion(source.getDescripcion())
                .precio(source.getPrecio())
                .estado((source.getActivo() == null ? null : Boolean.TRUE.equals(source.getActivo()) ? 1 : 0)).build();
    }

    public void actualizar(LaboratorioDTO source, Laboratorio target) {
        target.setNombre(limpiar(source.nombre()));
        target.setRuc(limpiar(source.ruc()));
        target.setTelefono(limpiar(source.telefono()));
        target.setDireccion(limpiar(source.direccion()));
        target.setCorreo(limpiar(source.correo()));
        target.setActivo((source.estado() == null ? null : source.estado() == 1));
    }

    public LaboratorioDTO toDTO(Laboratorio source) {
        return LaboratorioDTO.builder().idLaboratorio(source.getIdLaboratorio())
                .nombre(source.getNombre())
                .ruc(source.getRuc())
                .telefono(source.getTelefono())
                .direccion(source.getDireccion())
                .correo(source.getCorreo())
                .estado((source.getActivo() == null ? null : Boolean.TRUE.equals(source.getActivo()) ? 1 : 0)).build();
    }

    private String limpiar(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

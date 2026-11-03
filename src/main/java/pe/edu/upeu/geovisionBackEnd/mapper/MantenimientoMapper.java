package pe.edu.upeu.geovisionBackEnd.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.*;
import pe.edu.upeu.geovisionBackEnd.entity.*;

@Component
public class MantenimientoMapper {
    public void actualizar(CategoriaDTO source, Categoria target) {
        target.setNombre(limpiar(source.getNombre()));
        target.setDescripcion(limpiar(source.getDescripcion()));
        target.setEstado(pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum.fromCodigo(source.getEstado()));
    }

    public CategoriaDTO toDTO(Categoria source) {
        return CategoriaDTO.builder().idCategoria(source.getIdCategoria())
                .nombre(source.getNombre())
                .descripcion(source.getDescripcion())
                .estado(source.getEstadoCodigo())
                .fechaRegistro(source.getFechaRegistro()).build();
    }

    public void actualizar(MaterialCristalDTO source, MaterialCristal target) {
        target.setNombre(limpiar(source.getNombre()));
    }

    public MaterialCristalDTO toDTO(MaterialCristal source) {
        return MaterialCristalDTO.builder().idMaterialCristal(source.getIdMaterialCristal())
                .nombre(source.getNombre()).build();
    }

    public void actualizar(TratamientoCristalDTO source, TratamientoCristal target) {
        target.setNombre(limpiar(source.getNombre()));
    }

    public TratamientoCristalDTO toDTO(TratamientoCristal source) {
        return TratamientoCristalDTO.builder().idTratamientoCristal(source.getIdTratamientoCristal())
                .nombre(source.getNombre()).build();
    }

    public void actualizar(ServicioDTO source, Servicio target) {
        target.setNombre(limpiar(source.getNombre()));
        target.setDescripcion(limpiar(source.getDescripcion()));
        target.setPrecio(source.getPrecio());
        target.setEstado(pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum.fromCodigo(source.getEstado()));
    }

    public ServicioDTO toDTO(Servicio source) {
        return ServicioDTO.builder().idServicio(source.getIdServicio())
                .nombre(source.getNombre())
                .descripcion(source.getDescripcion())
                .precio(source.getPrecio())
                .estado(source.getEstadoCodigo()).build();
    }

    public void actualizar(LaboratorioDTO source, Laboratorio target) {
        target.setNombre(limpiar(source.getNombre()));
        target.setRuc(limpiar(source.getRuc()));
        target.setTelefono(limpiar(source.getTelefono()));
        target.setDireccion(limpiar(source.getDireccion()));
        target.setCorreo(limpiar(source.getCorreo()));
        target.setEstado(pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum.fromCodigo(source.getEstado()));
    }

    public LaboratorioDTO toDTO(Laboratorio source) {
        return LaboratorioDTO.builder().idLaboratorio(source.getIdLaboratorio())
                .nombre(source.getNombre())
                .ruc(source.getRuc())
                .telefono(source.getTelefono())
                .direccion(source.getDireccion())
                .correo(source.getCorreo())
                .estado(source.getEstadoCodigo()).build();
    }

    private String limpiar(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

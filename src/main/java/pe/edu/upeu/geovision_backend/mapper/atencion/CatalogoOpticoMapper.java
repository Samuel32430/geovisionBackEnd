package pe.edu.upeu.geovision_backend.mapper.atencion;

import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.MaterialCristalDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.MonturaDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.TratamientoCristalDTO;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.MaterialCristal;
import pe.edu.upeu.geovision_backend.entity.inventario.Montura;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.TratamientoCristal;

public final class CatalogoOpticoMapper {
    private CatalogoOpticoMapper() {}

    public static MaterialCristalDTO toMaterialCristalDTO(MaterialCristal material) {
        return new MaterialCristalDTO(material.getId(), material.getNombre());
    }

    public static TratamientoCristalDTO toTratamientoCristalDTO(TratamientoCristal tratamiento) {
        return new TratamientoCristalDTO(tratamiento.getId(), tratamiento.getNombre());
    }

    public static MonturaDTO toMonturaDTO(Montura montura) {
        return new MonturaDTO(montura.getId(), montura.getProducto().getCodigo(), montura.getProducto().getNombre(), montura.getProducto().getPrecio(), montura.getProducto().getStock(), montura.getColor(), montura.getModelo(), montura.getModelo() == null ? null : montura.getModelo().getDescripcion(), montura.getMaterial(), montura.getMaterial() == null ? null : montura.getMaterial().getDescripcion());
    }
}

package pe.edu.upeu.geovision_backend.mapper.atencion;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.MaterialCristalDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.MonturaDTO;
import pe.edu.upeu.geovision_backend.dto.gestion_mantenimiento.TratamientoCristalDTO;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.MaterialCristal;
import pe.edu.upeu.geovision_backend.entity.inventario.Montura;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.TratamientoCristal;

@Component
public class CatalogoOpticoMapper {

    public MaterialCristalDTO toMaterialCristalDTO(MaterialCristal material) {
        return MaterialCristalDTO.builder().idMaterialCristal(material.getIdMaterialCristal())
                .nombre(material.getNombre()).build();
    }

    public TratamientoCristalDTO toTratamientoCristalDTO(TratamientoCristal tratamiento) {
        return TratamientoCristalDTO.builder().idTratamientoCristal(tratamiento.getIdTratamientoCristal())
                .nombre(tratamiento.getNombre()).build();
    }

    public MonturaDTO toMonturaDTO(Montura montura) {
        return MonturaDTO.builder()
                .idProducto(montura.getIdProducto())
                .codigo(montura.getProducto().getCodigo())
                .nombre(montura.getProducto().getNombre())
                .precio(montura.getProducto().getPrecio())
                .stock(montura.getProducto().getStock())
                .color(montura.getColor())
                .modelo(montura.getModelo())
                .modeloDescripcion(montura.getModelo() == null ? null : montura.getModelo().getDescripcion())
                .material(montura.getMaterial())
                .materialDescripcion(montura.getMaterial() == null ? null : montura.getMaterial().getDescripcion())
                .build();
    }
}

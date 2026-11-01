package pe.edu.upeu.geovisionBackEnd.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.MaterialCristalDTO;
import pe.edu.upeu.geovisionBackEnd.dto.MonturaDTO;
import pe.edu.upeu.geovisionBackEnd.dto.TratamientoCristalDTO;
import pe.edu.upeu.geovisionBackEnd.entity.MaterialCristal;
import pe.edu.upeu.geovisionBackEnd.entity.Montura;
import pe.edu.upeu.geovisionBackEnd.entity.TratamientoCristal;

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

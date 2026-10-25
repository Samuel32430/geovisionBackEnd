package pe.edu.upeu.geovisionBackEnd.mapper.controlInventario;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.controlInventario.CristalRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlInventario.CristalResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.controlInventario.Cristal;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.TipoItem;

import java.util.List;

@Component
public class CristalMapper {

    public Cristal toEntity(Long idItem, CristalRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Cristal.builder()
                .idItem(idItem)
                .tipo(TipoItem.CRISTAL)
                .indiceRefraccion(dto.getIndiceRefraccion())
                .idMaterialCristal(dto.getIdMaterialCristal())
                .build();
    }

    public CristalResponseDTO toResponseDTO(Cristal entity, List<Long> idsTratamientos) {
        if (entity == null) {
            return null;
        }
        return CristalResponseDTO.builder()
                .idItem(entity.getIdItem())
                .tipo(entity.getTipo())
                .indiceRefraccion(entity.getIndiceRefraccion())
                .idMaterialCristal(entity.getIdMaterialCristal())
                .idsTratamientos(idsTratamientos)
                .build();
    }
}

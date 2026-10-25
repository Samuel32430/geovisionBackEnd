package pe.edu.upeu.geovisionBackEnd.mapper.controlInventario;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.controlInventario.MonturaRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlInventario.MonturaResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.controlInventario.Montura;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.TipoItem;

@Component
public class MonturaMapper {

    public Montura toEntity(Long idItem, MonturaRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Montura.builder()
                .idItem(idItem)
                .tipo(TipoItem.MONTURA)
                .color(dto.getColor())
                .medidas(dto.getMedidas())
                .idModeloMontura(dto.getIdModeloMontura())
                .idMaterialMontura(dto.getIdMaterialMontura())
                .build();
    }

    public MonturaResponseDTO toResponseDTO(Montura entity) {
        if (entity == null) {
            return null;
        }
        return MonturaResponseDTO.builder()
                .idItem(entity.getIdItem())
                .tipo(entity.getTipo())
                .color(entity.getColor())
                .medidas(entity.getMedidas())
                .idModeloMontura(entity.getIdModeloMontura())
                .idMaterialMontura(entity.getIdMaterialMontura())
                .build();
    }
}

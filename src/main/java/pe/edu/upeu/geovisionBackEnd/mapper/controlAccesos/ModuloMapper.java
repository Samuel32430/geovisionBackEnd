package pe.edu.upeu.geovisionBackEnd.mapper.controlAccesos;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.ModuloRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlAccesos.ModuloResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Modulo;

@Component
public class ModuloMapper {

    public Modulo toEntity(ModuloRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Modulo.builder()
                .nombre(dto.getNombre())
                .build();
    }

    public ModuloResponseDTO toResponseDTO(Modulo entity) {
        if (entity == null) {
            return null;
        }
        return ModuloResponseDTO.builder()
                .idModulo(entity.getIdModulo())
                .nombre(entity.getNombre())
                .build();
    }
}

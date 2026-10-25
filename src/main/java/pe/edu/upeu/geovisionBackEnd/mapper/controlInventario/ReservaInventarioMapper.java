package pe.edu.upeu.geovisionBackEnd.mapper.controlInventario;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.controlInventario.ReservaInventarioRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlInventario.ReservaInventarioResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.controlInventario.ReservaInventario;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.EstadoReserva;

@Component
public class ReservaInventarioMapper {

    public ReservaInventario toEntity(ReservaInventarioRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return ReservaInventario.builder()
                .cantidad(dto.getCantidad())
                .estado(EstadoReserva.ACTIVA)
                .idItem(dto.getIdItem())
                .idItemVendido(dto.getIdItemVendido())
                .build();
    }

    public ReservaInventarioResponseDTO toResponseDTO(ReservaInventario entity) {
        if (entity == null) {
            return null;
        }
        return ReservaInventarioResponseDTO.builder()
                .idReservaInventario(entity.getIdReservaInventario())
                .fechaRegistro(entity.getFechaRegistro())
                .cantidad(entity.getCantidad())
                .estado(entity.getEstado())
                .idItem(entity.getIdItem())
                .idItemVendido(entity.getIdItemVendido())
                .build();
    }
}

package pe.edu.upeu.geovisionBackEnd.mapper.controlInventario;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.controlInventario.MovimientoInventarioRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlInventario.MovimientoInventarioResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.controlInventario.MovimientoInventario;

@Component
public class MovimientoInventarioMapper {

    public MovimientoInventario toEntity(MovimientoInventarioRequestDTO dto, Integer cantidadConSigno) {
        if (dto == null) {
            return null;
        }
        return MovimientoInventario.builder()
                .cantidad(cantidadConSigno)
                .motivo(dto.getMotivo())
                .observaciones(dto.getObservaciones())
                .idItem(dto.getIdItem())
                .idItemVendido(dto.getIdItemVendido())
                .idEmpleado(dto.getIdEmpleado())
                .build();
    }

    public MovimientoInventarioResponseDTO toResponseDTO(MovimientoInventario entity) {
        if (entity == null) {
            return null;
        }
        return MovimientoInventarioResponseDTO.builder()
                .idMovimientoInventario(entity.getIdMovimientoInventario())
                .fechaRegistro(entity.getFechaRegistro())
                .cantidad(entity.getCantidad())
                .motivo(entity.getMotivo())
                .observaciones(entity.getObservaciones())
                .idItem(entity.getIdItem())
                .idItemVendido(entity.getIdItemVendido())
                .idEmpleado(entity.getIdEmpleado())
                .build();
    }
}

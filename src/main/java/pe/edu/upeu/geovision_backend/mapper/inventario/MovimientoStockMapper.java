package pe.edu.upeu.geovision_backend.mapper.inventario;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovision_backend.dto.inventario.MovimientoStockResponseDTO;
import pe.edu.upeu.geovision_backend.entity.inventario.MovimientoStockLog;
import pe.edu.upeu.geovision_backend.enums.inventario.MotivoMovimientoStockEnum;

@Component
public class MovimientoStockMapper {

    public MovimientoStockResponseDTO toDTO(MovimientoStockLog log) {
        if (log == null) {
            return null;
        }

        String motivoDesc = null;
        if (log.getMotivo() != null) {
            try {
                motivoDesc = MotivoMovimientoStockEnum.fromCodigo(log.getMotivo()).getDescripcion();
            } catch (Exception ignored) {
                motivoDesc = "Motivo " + log.getMotivo();
            }
        }

        String nombreUsuario = null;
        if (log.getUsuario() != null) {
            nombreUsuario = log.getUsuario().getNombre();
        }

        return new MovimientoStockResponseDTO(log.getId(), log.getProducto() != null ? log.getProducto().getId() : null, log.getProducto() != null ? log.getProducto().getCodigo() : null, log.getProducto() != null ? log.getProducto().getNombre() : null, log.getCantidad(), log.getStockPrevio(), log.getStockPosterior(), log.getMotivo(), motivoDesc, log.getObservaciones(), nombreUsuario, log.getFechaRegistro());
    }
}

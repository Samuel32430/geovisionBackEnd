package pe.edu.upeu.geovisionBackEnd.mapper.controlInventario;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.controlInventario.ItemRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlInventario.ItemResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlInventario.ItemStockResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.controlInventario.ItemUpdateRequestDTO;
import pe.edu.upeu.geovisionBackEnd.entity.controlInventario.Item;
import pe.edu.upeu.geovisionBackEnd.enums.controlInventario.TipoItem;

@Component
public class ItemMapper {

    public Item toEntity(ItemRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Item.builder()
                .codigo(dto.getCodigo())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .precio(dto.getPrecio())
                .tipo(dto.getTipo())
                .unidadVenta(dto.getUnidadVenta())
                .controlaStock(dto.getTipo() == TipoItem.CRISTAL ? false : dto.getControlaStock())
                .estado(dto.getEstado() != null ? dto.getEstado() : true)
                .stock(0) // El stock inicial siempre parte en 0 y solo varía por movimientos
                .idCategoria(dto.getIdCategoria())
                .idMarca(dto.getIdMarca())
                .build();
    }

    public ItemResponseDTO toResponseDTO(Item entity) {
        if (entity == null) {
            return null;
        }
        return ItemResponseDTO.builder()
                .idItem(entity.getIdItem())
                .codigo(entity.getCodigo())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .precio(entity.getPrecio())
                .tipo(entity.getTipo())
                .unidadVenta(entity.getUnidadVenta())
                .controlaStock(entity.getControlaStock())
                .estado(entity.getEstado())
                .fechaRegistro(entity.getFechaRegistro())
                .stock(entity.getStock())
                .idCategoria(entity.getIdCategoria())
                .idMarca(entity.getIdMarca())
                .build();
    }

    public ItemStockResponseDTO toStockResponseDTO(Item entity, Integer cantidadReservada) {
        if (entity == null) {
            return null;
        }
        int reservado = cantidadReservada != null ? cantidadReservada : 0;
        int stockFisico = entity.getStock() != null ? entity.getStock() : 0;
        int stockDisponible = Boolean.TRUE.equals(entity.getControlaStock())
                ? Math.max(0, stockFisico - reservado)
                : 999999; // Cristales y no controlados no limitan por stock físico

        boolean disponibleParaVenta = Boolean.TRUE.equals(entity.getEstado()) &&
                (!Boolean.TRUE.equals(entity.getControlaStock()) || stockDisponible > 0);

        return ItemStockResponseDTO.builder()
                .idItem(entity.getIdItem())
                .codigo(entity.getCodigo())
                .nombre(entity.getNombre())
                .tipo(entity.getTipo())
                .precio(entity.getPrecio())
                .controlaStock(entity.getControlaStock())
                .stockFisico(stockFisico)
                .stockReservado(reservado)
                .stockDisponible(stockDisponible)
                .disponibleParaVenta(disponibleParaVenta)
                .estado(entity.getEstado())
                .build();
    }

    /**
     * Aplica la actualización de datos básicos permitidos (RF-70).
     * No modifica: idItem, codigo, tipo, controlaStock, estado, stock, fechaRegistro.
     */
    public void updateEntityFromDTO(ItemUpdateRequestDTO dto, Item entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setPrecio(dto.getPrecio());
        entity.setUnidadVenta(dto.getUnidadVenta());
        entity.setIdCategoria(dto.getIdCategoria());
        entity.setIdMarca(dto.getIdMarca());
    }
}

package pe.edu.upeu.geovision_backend.mapper.inventario;

import pe.edu.upeu.geovision_backend.dto.inventario.MonturaDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoRequestDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoResponseDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoStockResponseDTO;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.Categoria;
import pe.edu.upeu.geovision_backend.entity.inventario.Montura;
import pe.edu.upeu.geovision_backend.entity.inventario.Producto;
import pe.edu.upeu.geovision_backend.enums.inventario.MaterialMonturaEnum;
import pe.edu.upeu.geovision_backend.enums.inventario.ModeloMonturaEnum;

public final class ProductoMapper {
    private ProductoMapper() {}

    public static ProductoResponseDTO toResponseDTO(Producto producto, Montura montura) {
        if (producto == null) {
            return null;
        }

        MonturaDTO monturaDTO = null;
        if (montura != null) {
            monturaDTO = toMonturaDTO(montura, producto);
        }

        return new ProductoResponseDTO(producto.getId(), producto.getCodigo(), producto.getNombre(), producto.getMarca(), producto.getDescripcion(), producto.getPrecio(), producto.getStock(), (producto.getActivo() == null ? null : Boolean.TRUE.equals(producto.getActivo()) ? 1 : 0), producto.getActivo() == null ? null : Boolean.TRUE.equals(producto.getActivo()) ? "ACTIVO" : "INACTIVO", producto.getFechaRegistro(), producto.getCategoria() != null ? producto.getCategoria().getId() : null, producto.getCategoria() != null ? producto.getCategoria().getNombre() : null, monturaDTO, monturaDTO != null);
    }

    public static MonturaDTO toMonturaDTO(Montura montura, Producto producto) {
        if (montura == null) {
            return null;
        }

        String modeloDesc = null;
        if (montura.getModelo() != null) {
            modeloDesc = montura.getModelo().getDescripcion();
        }

        String materialDesc = null;
        if (montura.getMaterial() != null) {
            materialDesc = montura.getMaterial().getDescripcion();
        }

        return new MonturaDTO(montura.getId(), producto != null ? producto.getCodigo() : null, producto != null ? producto.getNombre() : null, producto != null ? producto.getPrecio() : null, producto != null ? producto.getStock() : null, montura.getColor(), montura.getModelo(), modeloDesc, montura.getMaterial(), materialDesc);
    }

    public static ProductoStockResponseDTO toProductoStockResponseDTO(Producto producto, Integer umbralStockMinimo) {
        if (producto == null) {
            return null;
        }

        boolean disponible = (producto.getStock() != null && producto.getStock() > 0)
                && (Boolean.TRUE.equals(producto.getActivo()));

        return new ProductoStockResponseDTO(producto.getId(), producto.getCodigo(), producto.getNombre(), producto.getStock(), umbralStockMinimo != null ? umbralStockMinimo : 5, producto.getPrecio(), disponible, (producto.getActivo() == null ? null : Boolean.TRUE.equals(producto.getActivo()) ? 1 : 0), producto.getActivo() == null ? null : Boolean.TRUE.equals(producto.getActivo()) ? "ACTIVO" : "INACTIVO");
    }

    public static Producto toEntity(ProductoRequestDTO dto, Categoria categoria) {
        if (dto == null) {
            return null;
        }

        Boolean estado = true;
        if (dto.estado() != null) {
            if (dto.estado() != 0 && dto.estado() != 1) {
                throw new IllegalArgumentException("El estado debe ser 0 o 1");
            }
            estado = dto.estado() == 1;
        }

        return new Producto(null, dto.codigo() != null ? dto.codigo().trim() : null, dto.nombre() != null ? dto.nombre().trim() : null, dto.marca() != null ? dto.marca().trim() : null, dto.descripcion() != null ? dto.descripcion().trim() : null, dto.precio(), dto.stock() != null ? dto.stock() : 0, estado, null, categoria);
    }

    public static Montura toMonturaEntity(MonturaDTO dto, Producto producto) {
        if (dto == null) {
            return null;
        }

        return new Montura(producto != null ? producto.getId() : null, producto, dto.color() != null ? dto.color().trim() : null, dto.modelo(), dto.material());
    }
}

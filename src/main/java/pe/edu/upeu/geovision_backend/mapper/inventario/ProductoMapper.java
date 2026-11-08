package pe.edu.upeu.geovision_backend.mapper.inventario;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovision_backend.dto.inventario.MonturaDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoRequestDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoResponseDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoStockResponseDTO;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.Categoria;
import pe.edu.upeu.geovision_backend.entity.inventario.Montura;
import pe.edu.upeu.geovision_backend.entity.inventario.Producto;
import pe.edu.upeu.geovision_backend.enums.inventario.MaterialMonturaEnum;
import pe.edu.upeu.geovision_backend.enums.inventario.ModeloMonturaEnum;

@Component
public class ProductoMapper {

    public ProductoResponseDTO toResponseDTO(Producto producto, Montura montura) {
        if (producto == null) {
            return null;
        }

        MonturaDTO monturaDTO = null;
        if (montura != null) {
            monturaDTO = toMonturaDTO(montura, producto);
        }

        return ProductoResponseDTO.builder()
                .idProducto(producto.getIdProducto())
                .codigo(producto.getCodigo())
                .nombre(producto.getNombre())
                .marca(producto.getMarca())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .estado((producto.getActivo() == null ? null : Boolean.TRUE.equals(producto.getActivo()) ? 1 : 0))
                .estadoDescripcion(producto.getActivo() == null ? null : Boolean.TRUE.equals(producto.getActivo()) ? "ACTIVO" : "INACTIVO")
                .fechaRegistro(producto.getFechaRegistro())
                .idCategoria(producto.getCategoria() != null ? producto.getCategoria().getIdCategoria() : null)
                .nombreCategoria(producto.getCategoria() != null ? producto.getCategoria().getNombre() : null)
                .montura(monturaDTO)
                .esMontura(monturaDTO != null)
                .build();
    }

    public MonturaDTO toMonturaDTO(Montura montura, Producto producto) {
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

        return MonturaDTO.builder()
                .idProducto(montura.getIdProducto())
                .codigo(producto != null ? producto.getCodigo() : null)
                .nombre(producto != null ? producto.getNombre() : null)
                .precio(producto != null ? producto.getPrecio() : null)
                .stock(producto != null ? producto.getStock() : null)
                .color(montura.getColor())
                .modelo(montura.getModelo())
                .modeloDescripcion(modeloDesc)
                .material(montura.getMaterial())
                .materialDescripcion(materialDesc)
                .build();
    }

    public ProductoStockResponseDTO toProductoStockResponseDTO(Producto producto, Integer umbralStockMinimo) {
        if (producto == null) {
            return null;
        }

        boolean disponible = (producto.getStock() != null && producto.getStock() > 0)
                && (Boolean.TRUE.equals(producto.getActivo()));

        return ProductoStockResponseDTO.builder()
                .idProducto(producto.getIdProducto())
                .codigo(producto.getCodigo())
                .nombre(producto.getNombre())
                .stockActual(producto.getStock())
                .stockMinimoReferencial(umbralStockMinimo != null ? umbralStockMinimo : 5)
                .precio(producto.getPrecio())
                .disponible(disponible)
                .estado((producto.getActivo() == null ? null : Boolean.TRUE.equals(producto.getActivo()) ? 1 : 0))
                .estadoDescripcion(producto.getActivo() == null ? null : Boolean.TRUE.equals(producto.getActivo()) ? "ACTIVO" : "INACTIVO")
                .build();
    }

    public Producto toEntity(ProductoRequestDTO dto, Categoria categoria) {
        if (dto == null) {
            return null;
        }

        Boolean estado = true;
        if (dto.getEstado() != null) {
            if (dto.getEstado() != 0 && dto.getEstado() != 1) {
                throw new IllegalArgumentException("El estado debe ser 0 o 1");
            }
            estado = dto.getEstado() == 1;
        }

        return Producto.builder()
                .codigo(dto.getCodigo() != null ? dto.getCodigo().trim() : null)
                .nombre(dto.getNombre() != null ? dto.getNombre().trim() : null)
                .marca(dto.getMarca() != null ? dto.getMarca().trim() : null)
                .descripcion(dto.getDescripcion() != null ? dto.getDescripcion().trim() : null)
                .precio(dto.getPrecio())
                .stock(dto.getStock() != null ? dto.getStock() : 0)
                .activo(estado)
                .categoria(categoria)
                .build();
    }

    public Montura toMonturaEntity(MonturaDTO dto, Producto producto) {
        if (dto == null) {
            return null;
        }

        return Montura.builder()
                .idProducto(producto != null ? producto.getIdProducto() : null)
                .producto(producto)
                .color(dto.getColor() != null ? dto.getColor().trim() : null)
                .modelo(dto.getModelo())
                .material(dto.getMaterial())
                .build();
    }
}

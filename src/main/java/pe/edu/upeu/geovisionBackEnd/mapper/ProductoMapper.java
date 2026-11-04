package pe.edu.upeu.geovisionBackEnd.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.MonturaDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ProductoRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ProductoResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ProductoStockResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.Categoria;
import pe.edu.upeu.geovisionBackEnd.entity.Montura;
import pe.edu.upeu.geovisionBackEnd.entity.Producto;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;
import pe.edu.upeu.geovisionBackEnd.enums.MaterialMonturaEnum;
import pe.edu.upeu.geovisionBackEnd.enums.ModeloMonturaEnum;

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
                .estado(producto.getEstadoCodigo())
                .estadoDescripcion(producto.getEstado() != null ? producto.getEstado().name() : null)
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
                && (producto.getEstado() == EstadoGeneralEnum.ACTIVO);

        return ProductoStockResponseDTO.builder()
                .idProducto(producto.getIdProducto())
                .codigo(producto.getCodigo())
                .nombre(producto.getNombre())
                .stockActual(producto.getStock())
                .stockMinimoReferencial(umbralStockMinimo != null ? umbralStockMinimo : 5)
                .precio(producto.getPrecio())
                .disponible(disponible)
                .estado(producto.getEstadoCodigo())
                .estadoDescripcion(producto.getEstado() != null ? producto.getEstado().name() : null)
                .build();
    }

    public Producto toEntity(ProductoRequestDTO dto, Categoria categoria) {
        if (dto == null) {
            return null;
        }

        EstadoGeneralEnum estado = EstadoGeneralEnum.ACTIVO;
        if (dto.getEstado() != null) {
            estado = EstadoGeneralEnum.fromCodigo(dto.getEstado());
        }

        return Producto.builder()
                .codigo(dto.getCodigo() != null ? dto.getCodigo().trim() : null)
                .nombre(dto.getNombre() != null ? dto.getNombre().trim() : null)
                .marca(dto.getMarca() != null ? dto.getMarca().trim() : null)
                .descripcion(dto.getDescripcion() != null ? dto.getDescripcion().trim() : null)
                .precio(dto.getPrecio())
                .stock(dto.getStock() != null ? dto.getStock() : 0)
                .estado(estado)
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

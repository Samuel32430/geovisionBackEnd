package pe.edu.upeu.geovisionBackEnd.service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovisionBackEnd.dto.ProductoRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ProductoResponseDTO;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;

public interface ProductoCatalogoService {

    Page<ProductoResponseDTO> listarProductos(String termino, Long idCategoria, EstadoGeneralEnum estado, Pageable pageable);

    ProductoResponseDTO obtenerPorId(Long id);

    ProductoResponseDTO obtenerPorCodigo(String codigo);

    ProductoResponseDTO registrarProducto(ProductoRequestDTO request);

    ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO request);

    ProductoResponseDTO cambiarEstado(Long id, Integer nuevoEstado);

    Page<ProductoResponseDTO> listarDisponiblesParaVenta(Pageable pageable);

    boolean validarDisponibilidad(Long idProducto, Integer cantidad);
}

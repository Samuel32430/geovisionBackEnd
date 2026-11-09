package pe.edu.upeu.geovision_backend.service.inventario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoRequestDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoResponseDTO;

public interface ProductoCatalogoService {

    // >>>> QUERIES
    Page<ProductoResponseDTO> listarProductos(String termino, Long idCategoria, Boolean estado, Pageable pageable);

    ProductoResponseDTO obtenerPorId(Long id);

    ProductoResponseDTO obtenerPorCodigo(String codigo);

    Page<ProductoResponseDTO> listarDisponiblesParaVenta(Pageable pageable);

    // >>>> AVAILABILITY CHECKS
    boolean validarDisponibilidad(Long idProducto, Integer cantidad);

    // >>>> CREATION
    ProductoResponseDTO registrarProducto(ProductoRequestDTO request);

    // >>>> UPDATES
    ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO request);

    ProductoResponseDTO cambiarEstado(Long id, Integer nuevoEstado);

}

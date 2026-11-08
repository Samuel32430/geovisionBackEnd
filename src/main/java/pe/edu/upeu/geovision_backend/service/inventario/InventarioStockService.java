package pe.edu.upeu.geovision_backend.service.inventario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovision_backend.dto.inventario.AjusteStockRequestDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.EntradaStockRequestDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.MovimientoStockResponseDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoResponseDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoStockResponseDTO;

import java.util.List;

public interface InventarioStockService {

    ProductoStockResponseDTO consultarStockActual(Long idProducto, Integer umbralStockMinimo);

    MovimientoStockResponseDTO registrarEntradaStock(EntradaStockRequestDTO request, String username);

    MovimientoStockResponseDTO registrarAjusteStock(AjusteStockRequestDTO request, String username);

    Page<MovimientoStockResponseDTO> listarMovimientosKardex(Long idProducto, Pageable pageable);

    List<ProductoResponseDTO> listarAlertasBajoStock(Integer umbral);
}

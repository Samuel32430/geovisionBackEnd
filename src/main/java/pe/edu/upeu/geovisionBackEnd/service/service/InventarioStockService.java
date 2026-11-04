package pe.edu.upeu.geovisionBackEnd.service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovisionBackEnd.dto.AjusteStockRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.EntradaStockRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.MovimientoStockResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ProductoResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ProductoStockResponseDTO;

import java.util.List;

public interface InventarioStockService {

    ProductoStockResponseDTO consultarStockActual(Long idProducto, Integer umbralStockMinimo);

    MovimientoStockResponseDTO registrarEntradaStock(EntradaStockRequestDTO request, String username);

    MovimientoStockResponseDTO registrarAjusteStock(AjusteStockRequestDTO request, String username);

    Page<MovimientoStockResponseDTO> listarMovimientosKardex(Long idProducto, Pageable pageable);

    List<ProductoResponseDTO> listarAlertasBajoStock(Integer umbral);
}

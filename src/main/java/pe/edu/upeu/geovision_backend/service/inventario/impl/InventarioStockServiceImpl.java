package pe.edu.upeu.geovision_backend.service.inventario.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.dto.inventario.AjusteStockRequestDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.EntradaStockRequestDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.MovimientoStockResponseDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoResponseDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoStockResponseDTO;
import pe.edu.upeu.geovision_backend.entity.inventario.Montura;
import pe.edu.upeu.geovision_backend.entity.inventario.MovimientoStockLog;
import pe.edu.upeu.geovision_backend.entity.inventario.Producto;
import pe.edu.upeu.geovision_backend.entity.security.Usuario;
import pe.edu.upeu.geovision_backend.enums.inventario.MotivoMovimientoStockEnum;
import pe.edu.upeu.geovision_backend.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.mapper.inventario.MovimientoStockMapper;
import pe.edu.upeu.geovision_backend.mapper.inventario.ProductoMapper;
import pe.edu.upeu.geovision_backend.repository.inventario.MonturaRepository;
import pe.edu.upeu.geovision_backend.repository.inventario.MovimientoStockLogRepository;
import pe.edu.upeu.geovision_backend.repository.inventario.ProductoRepository;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioRepository;
import pe.edu.upeu.geovision_backend.service.inventario.InventarioStockService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioStockServiceImpl implements InventarioStockService {

    // >>>> CONSTANTS
    private static final Logger log = LoggerFactory.getLogger(InventarioStockServiceImpl.class);

    // >>>> DEPENDENCIES
    private final ProductoRepository productoRepository;
    private final MonturaRepository monturaRepository;
    private final MovimientoStockLogRepository movimientoStockLogRepository;
    private final UsuarioRepository usuarioRepository;

    // >>>> QUERIES
    @Override
    @Transactional(readOnly = true)
    public ProductoStockResponseDTO consultarStockActual(Long idProducto, Integer umbralStockMinimo) {
        log.debug("Consultando stock actual de producto ID: {}", idProducto);

        if (idProducto == null || idProducto <= 0) {
            throw new ReglaNegocioException("El ID del producto debe ser un número positivo");
        }

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RecursosNoEncontradosException("Producto no encontrado con ID: " + idProducto));

        return ProductoMapper.toProductoStockResponseDTO(producto, umbralStockMinimo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovimientoStockResponseDTO> listarMovimientosKardex(Long idProducto, Pageable pageable) {
        log.debug("Listando historial de movimientos (Kardex) para producto ID: {}", idProducto);

        if (idProducto == null || idProducto <= 0) {
            throw new ReglaNegocioException("El ID del producto debe ser un número positivo");
        }

        if (!productoRepository.existsById(idProducto)) {
            throw new RecursosNoEncontradosException("Producto no encontrado con ID: " + idProducto);
        }

        return movimientoStockLogRepository.findByProductoIdOrderByFechaRegistroDesc(idProducto, pageable)
                .map(MovimientoStockMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarAlertasBajoStock(Integer umbral) {
        int umbralCalculado = (umbral != null && umbral >= 0) ? umbral : 5;
        log.debug("Consultando productos con stock bajo o agotado (stock <= {})", umbralCalculado);

        return productoRepository.findProductosBajoStock(umbralCalculado).stream()
                .map(p -> {
                    Montura montura = monturaRepository.findById(p.getId()).orElse(null);
                    return ProductoMapper.toResponseDTO(p, montura);
                }
                )
                .toList();
    }

    // >>>> STOCK ENTRIES
    @Override
    @Transactional
    public MovimientoStockResponseDTO registrarEntradaStock(EntradaStockRequestDTO request, String username) {
        log.info("Iniciando registro de entrada de stock para producto ID: {}, cantidad: {}, usuario: {}",
                request.idProducto(), request.cantidad(), username
        );

        if (request.cantidad() == null || request.cantidad() <= 0) {
            throw new ReglaNegocioException("La cantidad ingresada debe ser mayor a cero");
        }

        Usuario usuario = obtenerUsuarioAutenticado(username);

        Producto producto = productoRepository.findByIdForUpdate(request.idProducto())
                .orElseThrow(() -> new RecursosNoEncontradosException("Producto no encontrado con ID: " + request.idProducto()));

        int stockPrevio = producto.getStock() != null ? producto.getStock() : 0;
        int nuevoStock = stockPrevio + request.cantidad();

        producto.setStock(nuevoStock);
        productoRepository.save(producto);

        int motivoCodigo = request.motivo() != null
                ? request.motivo()
                : MotivoMovimientoStockEnum.ENTRADA_POR_COMPRA.getCodigo();

        MovimientoStockLog movimiento = new MovimientoStockLog(null, LocalDateTime.now(), request.cantidad(), stockPrevio, nuevoStock, motivoCodigo, request.observaciones() != null ? request.observaciones().trim() : null, producto, usuario);

        MovimientoStockLog movimientoGuardado = movimientoStockLogRepository.save(movimiento);

        log.info("Entrada de stock registrada exitosamente. Producto ID: {}, Stock Previo: {}, Nuevo Stock: {}",
                producto.getId(), stockPrevio, nuevoStock
        );

        return MovimientoStockMapper.toDTO(movimientoGuardado);
    }

    // >>>> STOCK ADJUSTMENTS
    @Override
    @Transactional
    public MovimientoStockResponseDTO registrarAjusteStock(AjusteStockRequestDTO request, String username) {
        log.info("Iniciando registro de ajuste de stock para producto ID: {}, tipo: {}, cantidad: {}, usuario: {}",
                request.idProducto(), request.tipoAjuste(), request.cantidad(), username
        );

        if (request.cantidad() == null || request.cantidad() <= 0) {
            throw new ReglaNegocioException("La cantidad del ajuste debe ser mayor a cero");
        }
        if (request.observaciones() == null || request.observaciones().trim().isBlank()) {
            throw new ReglaNegocioException("La justificación u observaciones son obligatorias para registrar un ajuste");
        }

        Usuario usuario = obtenerUsuarioAutenticado(username);

        Producto producto = productoRepository.findByIdForUpdate(request.idProducto())
                .orElseThrow(() -> new RecursosNoEncontradosException("Producto no encontrado con ID: " + request.idProducto()));

        int stockPrevio = producto.getStock() != null ? producto.getStock() : 0;
        boolean esIncremento = "INCREMENTO".equalsIgnoreCase(request.tipoAjuste().trim());

        int nuevoStock;
        if (esIncremento) {
            nuevoStock = stockPrevio + request.cantidad();
        } else {
            if (stockPrevio - request.cantidad() < 0) {
                log.warn("Rechazando ajuste de salida: stock actual ({}) es insuficiente para restar ({})",
                        stockPrevio, request.cantidad()
                );
                throw new ReglaNegocioException(String.format(
                        "Operación rechazada: Un ajuste de salida de %d unidades dejaría el stock en negativo (Stock actual: %d)",
                        request.cantidad(), stockPrevio
                    )
                );
            }
            nuevoStock = stockPrevio - request.cantidad();
        }

        producto.setStock(nuevoStock);
        productoRepository.save(producto);

        MovimientoStockLog movimiento = new MovimientoStockLog(null, LocalDateTime.now(), request.cantidad(), stockPrevio, nuevoStock, request.motivo(), request.observaciones().trim(), producto, usuario);

        MovimientoStockLog movimientoGuardado = movimientoStockLogRepository.save(movimiento);

        log.info("Ajuste de stock registrado exitosamente. Producto ID: {}, Stock Previo: {}, Nuevo Stock: {}",
                producto.getId(), stockPrevio, nuevoStock
        );

        return MovimientoStockMapper.toDTO(movimientoGuardado);
    }

    // >>>> HELPERS
    private Usuario obtenerUsuarioAutenticado(String username) {
        if (username == null || username.isBlank()) {
            throw new ReglaNegocioException("No se encontró usuario autenticado en la sesión");
        }
        return usuarioRepository.findByNombreOrCorreo(username, username)
                .or(() -> usuarioRepository.findByNombre(username))
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario de sesión no encontrado: " + username));
    }

}

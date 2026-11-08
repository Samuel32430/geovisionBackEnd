package pe.edu.upeu.geovision_backend.service.inventario.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoRequestDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoResponseDTO;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.Categoria;
import pe.edu.upeu.geovision_backend.entity.inventario.Montura;
import pe.edu.upeu.geovision_backend.entity.inventario.Producto;
import pe.edu.upeu.geovision_backend.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.mapper.inventario.ProductoMapper;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.CategoriaRepository;
import pe.edu.upeu.geovision_backend.repository.inventario.MonturaRepository;
import pe.edu.upeu.geovision_backend.repository.inventario.ProductoRepository;
import pe.edu.upeu.geovision_backend.service.inventario.ProductoCatalogoService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoCatalogoServiceImpl implements ProductoCatalogoService {

    private final ProductoRepository productoRepository;
    private final MonturaRepository monturaRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> listarProductos(
            String termino,
            Long idCategoria,
            Boolean estado,
            Pageable pageable) {

        log.debug("Consultando productos con filtros: termino='{}', categoria={}, estado={}",
                termino, idCategoria, estado);

        String terminoFiltrado = (termino != null && !termino.trim().isEmpty()) ? termino.trim() : null;

        return productoRepository.buscarProductosPaginado(terminoFiltrado, idCategoria, estado, pageable)
                .map(this::construirResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(Long id) {
        if (id == null || id <= 0) {
            throw new ReglaNegocioException("El ID del producto debe ser un número positivo");
        }

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradosException("Producto no encontrado con ID: " + id));

        return construirResponseDTO(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new ReglaNegocioException("El código de producto es obligatorio");
        }

        Producto producto = productoRepository.findByCodigo(codigo.trim())
                .orElseThrow(() -> new RecursosNoEncontradosException("Producto no encontrado con código: " + codigo));

        return construirResponseDTO(producto);
    }

    @Override
    @Transactional
    public ProductoResponseDTO registrarProducto(ProductoRequestDTO request) {
        log.info("Registrando nuevo producto con código: {}", request.codigo());

        String codigo = request.codigo().trim();
        if (productoRepository.existsByCodigo(codigo)) {
            throw new ReglaNegocioException("Ya existe un producto registrado con el código: " + codigo);
        }

        Categoria categoria = categoriaRepository.findById(request.idCategoria())
                .orElseThrow(() -> new RecursosNoEncontradosException("Categoría no encontrada con ID: " + request.idCategoria()));

        if (!Boolean.TRUE.equals(categoria.getActivo())) {
            throw new ReglaNegocioException("No se pueden asociar productos a una categoría inactiva");
        }

        Producto producto = productoMapper.toEntity(request, categoria);
        Producto productoGuardado = productoRepository.save(producto);

        Montura monturaGuardada = null;
        if (request.montura() != null) {
            Montura montura = productoMapper.toMonturaEntity(request.montura(), productoGuardado);
            monturaGuardada = monturaRepository.save(montura);
        }

        log.info("Producto registrado exitosamente con ID: {}", productoGuardado.getIdProducto());
        return productoMapper.toResponseDTO(productoGuardado, monturaGuardada);
    }

    @Override
    @Transactional
    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO request) {
        log.info("Actualizando producto con ID: {}", id);

        if (id == null || id <= 0) {
            throw new ReglaNegocioException("El ID del producto debe ser un número positivo");
        }

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradosException("Producto no encontrado con ID: " + id));

        String nuevoCodigo = request.codigo().trim();
        if (productoRepository.existsByCodigoAndIdProductoNot(nuevoCodigo, id)) {
            throw new ReglaNegocioException("Ya existe otro producto registrado con el código: " + nuevoCodigo);
        }

        Categoria categoria = categoriaRepository.findById(request.idCategoria())
                .orElseThrow(() -> new RecursosNoEncontradosException("Categoría no encontrada con ID: " + request.idCategoria()));

        if (!Boolean.TRUE.equals(categoria.getActivo())) {
            throw new ReglaNegocioException("No se pueden asociar productos a una categoría inactiva");
        }

        producto.setCodigo(nuevoCodigo);
        producto.setNombre(request.nombre().trim());
        producto.setMarca(request.marca() != null ? request.marca().trim() : null);
        producto.setDescripcion(request.descripcion() != null ? request.descripcion().trim() : null);
        producto.setPrecio(request.precio());
        producto.setCategoria(categoria);

        if (request.estado() != null) {
            if (request.estado() != 0 && request.estado() != 1) {
                throw new ReglaNegocioException("El estado debe ser 0 o 1");
            }
            producto.setActivo(request.estado() == 1);
        }

        Producto productoActualizado = productoRepository.save(producto);

        Montura monturaFinal = null;
        if (request.montura() != null) {
            Optional<Montura> monturaExistente = monturaRepository.findById(id);
            if (monturaExistente.isPresent()) {
                Montura m = monturaExistente.get();
                m.setColor(request.montura().color() != null ? request.montura().color().trim() : null);
                m.setModelo(request.montura().modelo());
                m.setMaterial(request.montura().material());
                monturaFinal = monturaRepository.save(m);
            } else {
                Montura nuevaMontura = productoMapper.toMonturaEntity(request.montura(), productoActualizado);
                monturaFinal = monturaRepository.save(nuevaMontura);
            }
        } else {
            monturaFinal = monturaRepository.findById(id).orElse(null);
        }

        log.info("Producto actualizado exitosamente con ID: {}", id);
        return productoMapper.toResponseDTO(productoActualizado, monturaFinal);
    }

    @Override
    @Transactional
    public ProductoResponseDTO cambiarEstado(Long id, Integer nuevoEstado) {
        log.info("Cambiando estado de producto ID: {} a {}", id, nuevoEstado);

        if (id == null || id <= 0) {
            throw new ReglaNegocioException("El ID del producto debe ser un número positivo");
        }
        if (nuevoEstado == null || (nuevoEstado != 0 && nuevoEstado != 1)) {
            throw new ReglaNegocioException("El nuevo estado debe ser 0 o 1");
        }

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradosException("Producto no encontrado con ID: " + id));

        Boolean estado = nuevoEstado == 1;
        producto.setActivo(estado);
        Producto guardado = productoRepository.save(producto);

        return construirResponseDTO(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> listarDisponiblesParaVenta(Pageable pageable) {
        log.debug("Listando productos disponibles para venta (activos y con stock > 0)");
        return productoRepository.findByActivoAndStockGreaterThan(true, 0, pageable)
                .map(this::construirResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarDisponibilidad(Long idProducto, Integer cantidad) {
        if (idProducto == null || idProducto <= 0 || cantidad == null || cantidad <= 0) {
            return false;
        }

        return productoRepository.findById(idProducto)
                .map(p -> Boolean.TRUE.equals(p.getActivo()) && p.getStock() != null && p.getStock() >= cantidad)
                .orElse(false);
    }

    private ProductoResponseDTO construirResponseDTO(Producto producto) {
        Montura montura = monturaRepository.findById(producto.getIdProducto()).orElse(null);
        return productoMapper.toResponseDTO(producto, montura);
    }
}

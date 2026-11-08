package pe.edu.upeu.geovision_backend.controller.inventario;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoRequestDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoResponseDTO;
import pe.edu.upeu.geovision_backend.service.inventario.ProductoCatalogoService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Slf4j
public class ProductoCatalogoController {

    private final ProductoCatalogoService productoCatalogoService;

    /**
     * RF-66, RF-67 / CU512: Consultar y filtrar productos en el catálogo.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_LEER', 'INVENTARIO_LEER', 'ROLE_ADMINISTRADOR')")
    public ResponseEntity<Page<ProductoResponseDTO>> listarProductos(
            @RequestParam(required = false) String termino,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) @Min(0) @Max(1) Integer estado,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "nombre") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        log.debug("REST listar productos: termino={}, categoria={}, estado={}", termino, idCategoria, estado);

        String campoOrden = "estado".equals(sortBy) ? "activo" : sortBy;
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(campoOrden).descending() : Sort.by(campoOrden).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Boolean activo = estado == null ? null : estado == 1;
        Page<ProductoResponseDTO> resultado = productoCatalogoService.listarProductos(termino, idCategoria, activo, pageable);

        return ResponseEntity.ok(resultado);
    }

    /**
     * RF-66 / CU512: Obtener producto por su ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_LEER', 'INVENTARIO_LEER', 'ROLE_ADMINISTRADOR')")
    public ResponseEntity<ProductoResponseDTO> obtenerPorId(@PathVariable("id") @Positive Long id) {
        log.debug("REST obtener producto ID: {}", id);
        return ResponseEntity.ok(productoCatalogoService.obtenerPorId(id));
    }

    /**
     * RF-66 / CU512: Obtener producto por su código único.
     */
    @GetMapping("/codigo/{codigo}")
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_LEER', 'INVENTARIO_LEER', 'ROLE_ADMINISTRADOR')")
    public ResponseEntity<ProductoResponseDTO> obtenerPorCodigo(@PathVariable("codigo") String codigo) {
        log.debug("REST obtener producto por código: {}", codigo);
        return ResponseEntity.ok(productoCatalogoService.obtenerPorCodigo(codigo));
    }

    /**
     * RF-80 / CU514: Mostrar en ventas solo productos activos y disponibles con stock > 0.
     */
    @GetMapping("/disponibles-venta")
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_LEER', 'VENTAS_LEER', 'INVENTARIO_LEER', 'ROLE_ADMINISTRADOR')")
    public ResponseEntity<Page<ProductoResponseDTO>> listarDisponiblesParaVenta(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {

        log.debug("REST listar productos disponibles para venta");
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
        return ResponseEntity.ok(productoCatalogoService.listarDisponiblesParaVenta(pageable));
    }

    /**
     * RF-81 / CU514: Validar disponibilidad de stock cuando se seleccione un producto para venta.
     */
    @GetMapping("/{id}/validar-disponibilidad")
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_LEER', 'VENTAS_LEER', 'INVENTARIO_LEER', 'ROLE_ADMINISTRADOR')")
    public ResponseEntity<Map<String, Object>> validarDisponibilidad(
            @PathVariable("id") @Positive Long id,
            @RequestParam("cantidad") @Positive Integer cantidad) {

        log.debug("REST validar disponibilidad producto ID: {}, cantidad: {}", id, cantidad);
        boolean disponible = productoCatalogoService.validarDisponibilidad(id, cantidad);

        return ResponseEntity.ok(Map.of(
                "id", id,
                "cantidadSolicitada", cantidad,
                "disponible", disponible
        ));
    }

    /**
     * RF-68, RF-69 / CU512: Registrar nuevo producto / montura en el catálogo.
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_REGISTRAR', 'INVENTARIO_GESTIONAR', 'ROLE_ADMINISTRADOR')")
    public ResponseEntity<ProductoResponseDTO> registrarProducto(@Valid @RequestBody ProductoRequestDTO request) {
        log.info("REST registrar producto con código: {}", request.codigo());
        ProductoResponseDTO response = productoCatalogoService.registrarProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * RF-70 / CU512: Actualizar datos básicos de producto.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_GESTIONAR', 'INVENTARIO_GESTIONAR', 'ROLE_ADMINISTRADOR')")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(
            @PathVariable("id") @Positive Long id,
            @Valid @RequestBody ProductoRequestDTO request) {

        log.info("REST actualizar producto ID: {}", id);
        ProductoResponseDTO response = productoCatalogoService.actualizarProducto(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * RF-71 / CU512: Habilitar o deshabilitar producto para venta (activar/desactivar).
     */
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_GESTIONAR', 'INVENTARIO_GESTIONAR', 'ROLE_ADMINISTRADOR')")
    public ResponseEntity<ProductoResponseDTO> cambiarEstado(
            @PathVariable("id") @Positive Long id,
            @RequestParam(value = "estado", required = false) Integer estadoParam,
            @RequestBody(required = false) Map<String, Integer> body) {

        Integer nuevoEstado = estadoParam;
        if (nuevoEstado == null && body != null && body.containsKey("estado")) {
            nuevoEstado = body.get("estado");
        }

        log.info("REST cambiar estado producto ID: {} a {}", id, nuevoEstado);
        ProductoResponseDTO response = productoCatalogoService.cambiarEstado(id, nuevoEstado);
        return ResponseEntity.ok(response);
    }
}

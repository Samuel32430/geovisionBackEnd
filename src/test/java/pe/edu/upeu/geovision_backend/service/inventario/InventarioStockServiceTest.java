package pe.edu.upeu.geovision_backend.service.inventario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovision_backend.dto.inventario.AjusteStockRequestDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.EntradaStockRequestDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.MovimientoStockResponseDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoResponseDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoStockResponseDTO;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.Categoria;
import pe.edu.upeu.geovision_backend.entity.inventario.MovimientoStockLog;
import pe.edu.upeu.geovision_backend.entity.inventario.Producto;
import pe.edu.upeu.geovision_backend.entity.security.Usuario;
import pe.edu.upeu.geovision_backend.enums.inventario.MotivoMovimientoStockEnum;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.mapper.inventario.MovimientoStockMapper;
import pe.edu.upeu.geovision_backend.mapper.inventario.ProductoMapper;
import pe.edu.upeu.geovision_backend.repository.inventario.MonturaRepository;
import pe.edu.upeu.geovision_backend.repository.inventario.MovimientoStockLogRepository;
import pe.edu.upeu.geovision_backend.repository.inventario.ProductoRepository;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioRepository;
import pe.edu.upeu.geovision_backend.service.inventario.impl.InventarioStockServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventarioStockServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private MonturaRepository monturaRepository;

    @Mock
    private MovimientoStockLogRepository movimientoStockLogRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Spy
    private ProductoMapper productoMapper = new ProductoMapper();

    @Spy
    private MovimientoStockMapper movimientoStockMapper = new MovimientoStockMapper();

    @InjectMocks
    private InventarioStockServiceImpl inventarioStockService;

    private Producto producto;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        Categoria categoria = Categoria.builder().idCategoria(1L).nombre("MONTURAS").build();

        producto = Producto.builder()
                .idProducto(10L)
                .codigo("MONT-001")
                .nombre("Montura Aviator")
                .precio(new BigDecimal("350.00"))
                .stock(20)
                .activo(true)
                .categoria(categoria)
                .fechaRegistro(LocalDateTime.now())
                .build();

        usuario = Usuario.builder()
                .idUsuario(1L)
                .nombre("admin")
                .correo("admin@geovision.com")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("CU513 / RF-74, RF-79: Registrar entrada de stock incrementa stock y crea Kardex con bloqueo pesimista")
    void registrarEntradaStockExitoso() {
        EntradaStockRequestDTO request = EntradaStockRequestDTO.builder()
                .idProducto(10L)
                .cantidad(10)
                .motivo(1)
                .observaciones("Llegada de pedido proveedor N° 402")
                .build();

        when(usuarioRepository.findByNombreOrCorreo("admin", "admin")).thenReturn(Optional.of(usuario));
        when(productoRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        when(movimientoStockLogRepository.save(any(MovimientoStockLog.class))).thenAnswer(invocation -> {
            MovimientoStockLog log = invocation.getArgument(0);
            log.setIdMovimiento(100L);
            return log;
        });

        MovimientoStockResponseDTO response = inventarioStockService.registrarEntradaStock(request, "admin");

        assertThat(response).isNotNull();
        assertThat(response.getIdMovimiento()).isEqualTo(100L);
        assertThat(response.getStockPrevio()).isEqualTo(20);
        assertThat(response.getStockPosterior()).isEqualTo(30);
        assertThat(producto.getStock()).isEqualTo(30);

        verify(productoRepository).findByIdForUpdate(10L);
        verify(productoRepository).save(producto);
        verify(movimientoStockLogRepository).save(any(MovimientoStockLog.class));
    }

    @Test
    @DisplayName("CU513 / RF-76: Registrar entrada con cantidad <= 0 es rechazada")
    void registrarEntradaCantidadInvalidaRechazada() {
        EntradaStockRequestDTO request = EntradaStockRequestDTO.builder()
                .idProducto(10L)
                .cantidad(0)
                .build();

        assertThatThrownBy(() -> inventarioStockService.registrarEntradaStock(request, "admin"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("La cantidad ingresada debe ser mayor a cero");

        verify(productoRepository, never()).findByIdForUpdate(any());
    }

    @Test
    @DisplayName("CU513 / RF-75, RF-78: Ajuste de stock por incremento suma unidades y genera Kardex")
    void registrarAjusteIncrementoExitoso() {
        AjusteStockRequestDTO request = AjusteStockRequestDTO.builder()
                .idProducto(10L)
                .cantidad(5)
                .tipoAjuste("INCREMENTO")
                .motivo(MotivoMovimientoStockEnum.AJUSTE_POR_DIFERENCIA.getCodigo())
                .observaciones("Conteo físico anual arrojó 5 unidades adicionales")
                .build();

        when(usuarioRepository.findByNombreOrCorreo("admin", "admin")).thenReturn(Optional.of(usuario));
        when(productoRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        when(movimientoStockLogRepository.save(any(MovimientoStockLog.class))).thenAnswer(invocation -> {
            MovimientoStockLog log = invocation.getArgument(0);
            log.setIdMovimiento(101L);
            return log;
        });

        MovimientoStockResponseDTO response = inventarioStockService.registrarAjusteStock(request, "admin");

        assertThat(response).isNotNull();
        assertThat(response.getStockPrevio()).isEqualTo(20);
        assertThat(response.getStockPosterior()).isEqualTo(25);
        assertThat(producto.getStock()).isEqualTo(25);
    }

    @Test
    @DisplayName("CU513 / RF-77: Impedir que un ajuste de salida deje stock negativo (Prevención estricta)")
    void registrarAjusteSalidaExcesivaRechazado() {
        AjusteStockRequestDTO request = AjusteStockRequestDTO.builder()
                .idProducto(10L)
                .cantidad(25) // Stock actual es 20 -> dejaría -5
                .tipoAjuste("DECREMENTO")
                .motivo(MotivoMovimientoStockEnum.DANIO_DETERIORO.getCodigo())
                .observaciones("Deterioro en exhibidor")
                .build();

        when(usuarioRepository.findByNombreOrCorreo("admin", "admin")).thenReturn(Optional.of(usuario));
        when(productoRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(producto));

        assertThatThrownBy(() -> inventarioStockService.registrarAjusteStock(request, "admin"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("dejaría el stock en negativo");

        assertThat(producto.getStock()).isEqualTo(20); // No cambia
        verify(movimientoStockLogRepository, never()).save(any());
    }

    @Test
    @DisplayName("CU513 / RF-75: Ajuste sin observaciones o justificación es rechazado")
    void registrarAjusteSinJustificacionRechazado() {
        AjusteStockRequestDTO request = AjusteStockRequestDTO.builder()
                .idProducto(10L)
                .cantidad(2)
                .tipoAjuste("DECREMENTO")
                .motivo(3)
                .observaciones("   ") // Vacía
                .build();

        assertThatThrownBy(() -> inventarioStockService.registrarAjusteStock(request, "admin"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("La justificación u observaciones son obligatorias");

        verify(productoRepository, never()).findByIdForUpdate(any());
    }

    @Test
    @DisplayName("CU513 / RF-72, RF-73: Consultar stock actual y disponibilidad")
    void consultarStockActual() {
        when(productoRepository.findById(10L)).thenReturn(Optional.of(producto));

        ProductoStockResponseDTO response = inventarioStockService.consultarStockActual(10L, 5);

        assertThat(response).isNotNull();
        assertThat(response.getIdProducto()).isEqualTo(10L);
        assertThat(response.getStockActual()).isEqualTo(20);
        assertThat(response.getDisponible()).isTrue();
        assertThat(response.getStockMinimoReferencial()).isEqualTo(5);
    }

    @Test
    @DisplayName("CU514 / RF-82: Consultar productos con stock bajo o agotado")
    void listarAlertasBajoStock() {
        Producto productoBajo = Producto.builder()
                .idProducto(11L)
                .codigo("MONT-002")
                .nombre("Montura Estuche")
                .stock(2)
                .precio(new BigDecimal("100.00"))
                .activo(true)
                .build();

        when(productoRepository.findProductosBajoStock(5)).thenReturn(List.of(productoBajo));
        when(monturaRepository.findById(11L)).thenReturn(Optional.empty());

        List<ProductoResponseDTO> alertas = inventarioStockService.listarAlertasBajoStock(5);

        assertThat(alertas).hasSize(1);
        assertThat(alertas.get(0).getIdProducto()).isEqualTo(11L);
        assertThat(alertas.get(0).getStock()).isEqualTo(2);
    }

    @Test
    @DisplayName("CU514 / RF-83: Consultar historial de movimientos (Kardex) de un producto")
    void listarMovimientosKardex() {
        Pageable pageable = PageRequest.of(0, 10);
        MovimientoStockLog log = MovimientoStockLog.builder()
                .idMovimiento(1L)
                .producto(producto)
                .usuario(usuario)
                .cantidad(5)
                .stockPrevio(15)
                .stockPosterior(20)
                .motivo(1)
                .fechaRegistro(LocalDateTime.now())
                .build();

        when(productoRepository.existsById(10L)).thenReturn(true);
        when(movimientoStockLogRepository.findByProductoIdProductoOrderByFechaRegistroDesc(10L, pageable))
                .thenReturn(new PageImpl<>(List.of(log), pageable, 1));

        Page<MovimientoStockResponseDTO> kardex = inventarioStockService.listarMovimientosKardex(10L, pageable);

        assertThat(kardex.getContent()).hasSize(1);
        assertThat(kardex.getContent().get(0).getNombreUsuario()).isEqualTo("admin");
        assertThat(kardex.getContent().get(0).getCantidad()).isEqualTo(5);
    }
}

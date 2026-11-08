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

    @InjectMocks
    private InventarioStockServiceImpl inventarioStockService;

    private Producto producto;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        Categoria categoria = new Categoria(1L, "MONTURAS", null, true, null);

        producto = new Producto(10L, "MONT-001", "Montura Aviator", null, null, new BigDecimal("350.00"), 20, true, LocalDateTime.now(), categoria);

        usuario = new Usuario(1L, "admin", null, "admin@geovision.com", true, null);
    }

    @Test
    @DisplayName("CU513 / RF-74, RF-79: Registrar entrada de stock incrementa stock y crea Kardex con bloqueo pesimista")
    void registrarEntradaStockExitoso() {
        EntradaStockRequestDTO request = new EntradaStockRequestDTO(10L, 10, 1, "Llegada de pedido proveedor N° 402");

        when(usuarioRepository.findByNombreOrCorreo("admin", "admin")).thenReturn(Optional.of(usuario));
        when(productoRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        when(movimientoStockLogRepository.save(any(MovimientoStockLog.class))).thenAnswer(invocation -> {
            MovimientoStockLog log = invocation.getArgument(0);
            log.setId(100L);
            return log;
        });

        MovimientoStockResponseDTO response = inventarioStockService.registrarEntradaStock(request, "admin");

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.stockPrevio()).isEqualTo(20);
        assertThat(response.stockPosterior()).isEqualTo(30);
        assertThat(producto.getStock()).isEqualTo(30);

        verify(productoRepository).findByIdForUpdate(10L);
        verify(productoRepository).save(producto);
        verify(movimientoStockLogRepository).save(any(MovimientoStockLog.class));
    }

    @Test
    @DisplayName("CU513 / RF-76: Registrar entrada con cantidad <= 0 es rechazada")
    void registrarEntradaCantidadInvalidaRechazada() {
        EntradaStockRequestDTO request = new EntradaStockRequestDTO(10L, 0, 1, null);

        assertThatThrownBy(() -> inventarioStockService.registrarEntradaStock(request, "admin"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("La cantidad ingresada debe ser mayor a cero");

        verify(productoRepository, never()).findByIdForUpdate(any());
    }

    @Test
    @DisplayName("CU513 / RF-75, RF-78: Ajuste de stock por incremento suma unidades y genera Kardex")
    void registrarAjusteIncrementoExitoso() {
        AjusteStockRequestDTO request = new AjusteStockRequestDTO(10L, 5, "INCREMENTO",
                MotivoMovimientoStockEnum.AJUSTE_POR_DIFERENCIA.getCodigo(), "Conteo físico anual arrojó 5 unidades adicionales");

        when(usuarioRepository.findByNombreOrCorreo("admin", "admin")).thenReturn(Optional.of(usuario));
        when(productoRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        when(movimientoStockLogRepository.save(any(MovimientoStockLog.class))).thenAnswer(invocation -> {
            MovimientoStockLog log = invocation.getArgument(0);
            log.setId(101L);
            return log;
        });

        MovimientoStockResponseDTO response = inventarioStockService.registrarAjusteStock(request, "admin");

        assertThat(response).isNotNull();
        assertThat(response.stockPrevio()).isEqualTo(20);
        assertThat(response.stockPosterior()).isEqualTo(25);
        assertThat(producto.getStock()).isEqualTo(25);
    }

    @Test
    @DisplayName("CU513 / RF-77: Impedir que un ajuste de salida deje stock negativo (Prevención estricta)")
    void registrarAjusteSalidaExcesivaRechazado() {
        // Stock actual es 20; retirar 25 dejaría -5.
        AjusteStockRequestDTO request = new AjusteStockRequestDTO(10L, 25, "DECREMENTO",
                MotivoMovimientoStockEnum.DANIO_DETERIORO.getCodigo(), "Deterioro en exhibidor");

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
        AjusteStockRequestDTO request = new AjusteStockRequestDTO(10L, 2, "DECREMENTO", 3, "   ");

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
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.stockActual()).isEqualTo(20);
        assertThat(response.disponible()).isTrue();
        assertThat(response.stockMinimoReferencial()).isEqualTo(5);
    }

    @Test
    @DisplayName("CU514 / RF-82: Consultar productos con stock bajo o agotado")
    void listarAlertasBajoStock() {
        Producto productoBajo = new Producto(11L, "MONT-002", "Montura Estuche", null, null, new BigDecimal("100.00"), 2, true, null, null);

        when(productoRepository.findProductosBajoStock(5)).thenReturn(List.of(productoBajo));
        when(monturaRepository.findById(11L)).thenReturn(Optional.empty());

        List<ProductoResponseDTO> alertas = inventarioStockService.listarAlertasBajoStock(5);

        assertThat(alertas).hasSize(1);
        assertThat(alertas.get(0).id()).isEqualTo(11L);
        assertThat(alertas.get(0).stock()).isEqualTo(2);
    }

    @Test
    @DisplayName("CU514 / RF-83: Consultar historial de movimientos (Kardex) de un producto")
    void listarMovimientosKardex() {
        Pageable pageable = PageRequest.of(0, 10);
        MovimientoStockLog log = new MovimientoStockLog(1L, LocalDateTime.now(), 5, 15, 20, 1, null, producto, usuario);

        when(productoRepository.existsById(10L)).thenReturn(true);
        when(movimientoStockLogRepository.findByProductoIdOrderByFechaRegistroDesc(10L, pageable))
                .thenReturn(new PageImpl<>(List.of(log), pageable, 1));

        Page<MovimientoStockResponseDTO> kardex = inventarioStockService.listarMovimientosKardex(10L, pageable);

        assertThat(kardex.getContent()).hasSize(1);
        assertThat(kardex.getContent().get(0).nombreUsuario()).isEqualTo("admin");
        assertThat(kardex.getContent().get(0).cantidad()).isEqualTo(5);
    }
}

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
import pe.edu.upeu.geovision_backend.dto.inventario.MonturaDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoRequestDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoResponseDTO;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.Categoria;
import pe.edu.upeu.geovision_backend.entity.inventario.Montura;
import pe.edu.upeu.geovision_backend.entity.inventario.Producto;
import pe.edu.upeu.geovision_backend.enums.inventario.MaterialMonturaEnum;
import pe.edu.upeu.geovision_backend.enums.inventario.ModeloMonturaEnum;
import pe.edu.upeu.geovision_backend.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.CategoriaRepository;
import pe.edu.upeu.geovision_backend.repository.inventario.MonturaRepository;
import pe.edu.upeu.geovision_backend.repository.inventario.ProductoRepository;
import pe.edu.upeu.geovision_backend.service.inventario.impl.ProductoCatalogoServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoCatalogoServiceTest {

    // >>>> FIXTURES
    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private MonturaRepository monturaRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoCatalogoServiceImpl productoCatalogoService;

    private Categoria categoriaMonturas;
    private Producto productoBase;
    private Montura monturaBase;

    // >>>> SETUP
    @BeforeEach
    void setUp() {
        categoriaMonturas = new Categoria(1L, "MONTURAS", null, true, null);

        productoBase = new Producto(10L, "MONT-001", "Montura Ray-Ban Aviator", "Ray-Ban", null, new BigDecimal("450.00"), 15, true, LocalDateTime.now(), categoriaMonturas);

        monturaBase = new Montura(10L, productoBase, "Dorado", ModeloMonturaEnum.ARO_COMPLETO, MaterialMonturaEnum.METAL);
    }

    // >>>> TESTS
    @Test
    @DisplayName("CU512 / RF-68, RF-69: Registrar producto simple exitosamente")
    void registrarProductoSimpleExitoso() {
        ProductoRequestDTO request = new ProductoRequestDTO("ACC-001", "Líquido Limpiador 60ml", "OptiClean", null, new BigDecimal("25.00"), 50, 1, 1L, null);

        when(productoRepository.existsByCodigo("ACC-001")).thenReturn(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaMonturas));

        Producto guardado = new Producto(20L, "ACC-001", "Líquido Limpiador 60ml", "OptiClean", null, new BigDecimal("25.00"), 50, true, LocalDateTime.now(), categoriaMonturas);

        when(productoRepository.save(any(Producto.class))).thenReturn(guardado);

        ProductoResponseDTO response = productoCatalogoService.registrarProducto(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(20L);
        assertThat(response.codigo()).isEqualTo("ACC-001");
        assertThat(response.esMontura()).isFalse();
        verify(monturaRepository, never()).save(any());
    }

    @Test
    @DisplayName("CU512 / RF-68: Registrar montura persiste producto y montura de forma atómica")
    void registrarMonturaAtomicoExitoso() {
        MonturaDTO monturaDTO = new MonturaDTO(null, null, null, null, null, "Dorado", ModeloMonturaEnum.ARO_COMPLETO, null, MaterialMonturaEnum.METAL, null);

        ProductoRequestDTO request = new ProductoRequestDTO("MONT-001", "Montura Ray-Ban Aviator", "Ray-Ban", null, new BigDecimal("450.00"), 15, 1, 1L, monturaDTO);

        when(productoRepository.existsByCodigo("MONT-001")).thenReturn(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaMonturas));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoBase);
        when(monturaRepository.save(any(Montura.class))).thenReturn(monturaBase);

        ProductoResponseDTO response = productoCatalogoService.registrarProducto(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.esMontura()).isTrue();
        assertThat(response.montura().color()).isEqualTo("Dorado");
        verify(productoRepository).save(any(Producto.class));
        verify(monturaRepository).save(any(Montura.class));
    }

    @Test
    @DisplayName("CU512 / RF-69: Rechaza registro si el código de producto ya existe")
    void registrarProductoCodigoDuplicadoRechazado() {
        ProductoRequestDTO request = new ProductoRequestDTO("MONT-001", "Montura Repetida", null, null, new BigDecimal("200.00"), 0, 1, 1L, null);

        when(productoRepository.existsByCodigo("MONT-001")).thenReturn(true);

        assertThatThrownBy(() -> productoCatalogoService.registrarProducto(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("Ya existe un producto registrado con el código: MONT-001");

        verify(productoRepository, never()).save(any());
    }

    @Test
    @DisplayName("CU512 / RF-70: Actualizar producto exitosamente validando unicidad de código")
    void actualizarProductoExitoso() {
        ProductoRequestDTO request = new ProductoRequestDTO("MONT-001-MOD", "Montura Aviator Modificada", "Ray-Ban", null, new BigDecimal("480.00"), 0, 1, 1L, null);

        when(productoRepository.findById(10L)).thenReturn(Optional.of(productoBase));
        when(productoRepository.existsByCodigoAndIdNot("MONT-001-MOD", 10L)).thenReturn(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaMonturas));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoBase);
        when(monturaRepository.findById(10L)).thenReturn(Optional.of(monturaBase));

        ProductoResponseDTO response = productoCatalogoService.actualizarProducto(10L, request);

        assertThat(response).isNotNull();
        verify(productoRepository).save(productoBase);
    }

    @Test
    @DisplayName("CU512 / RF-71: Cambiar estado activa/desactiva producto")
    void cambiarEstadoExitoso() {
        when(productoRepository.findById(10L)).thenReturn(Optional.of(productoBase));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoBase);
        when(monturaRepository.findById(10L)).thenReturn(Optional.of(monturaBase));

        ProductoResponseDTO response = productoCatalogoService.cambiarEstado(10L, 0);

        assertThat(response).isNotNull();
        assertThat(productoBase.getActivo()).isEqualTo(false);
    }

    @Test
    @DisplayName("CU514 / RF-80: Listar productos disponibles para venta con stock > 0")
    void listarDisponiblesParaVenta() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productoRepository.findByActivoAndStockGreaterThan(true, 0, pageable))
                .thenReturn(new PageImpl<>(List.of(productoBase), pageable, 1));
        when(monturaRepository.findById(10L)).thenReturn(Optional.of(monturaBase));

        Page<ProductoResponseDTO> resultado = productoCatalogoService.listarDisponiblesParaVenta(pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).stock()).isEqualTo(15);
    }

    @Test
    @DisplayName("CU514 / RF-81: Validar disponibilidad de producto para venta")
    void validarDisponibilidadParaVenta() {
        when(productoRepository.findById(10L)).thenReturn(Optional.of(productoBase));

        assertThat(productoCatalogoService.validarDisponibilidad(10L, 10)).isTrue();
        assertThat(productoCatalogoService.validarDisponibilidad(10L, 15)).isTrue();
        assertThat(productoCatalogoService.validarDisponibilidad(10L, 16)).isFalse();
    }
}

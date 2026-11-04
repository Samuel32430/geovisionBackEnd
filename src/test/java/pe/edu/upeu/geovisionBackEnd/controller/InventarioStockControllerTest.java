package pe.edu.upeu.geovisionBackEnd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pe.edu.upeu.geovisionBackEnd.dto.AjusteStockRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.EntradaStockRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.MovimientoStockResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ProductoResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ProductoStockResponseDTO;
import pe.edu.upeu.geovisionBackEnd.exception.GlobalExceptionHandler;
import pe.edu.upeu.geovisionBackEnd.service.service.InventarioStockService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class InventarioStockControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private InventarioStockService inventarioStockService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private InventarioStockController inventarioStockController;

    private MovimientoStockResponseDTO movimientoResponse;
    private ProductoStockResponseDTO stockResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(inventarioStockController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        movimientoResponse = MovimientoStockResponseDTO.builder()
                .idMovimiento(100L)
                .idProducto(1L)
                .codigoProducto("MONT-001")
                .nombreProducto("Montura Ray-Ban")
                .cantidad(10)
                .stockPrevio(5)
                .stockPosterior(15)
                .motivo(1)
                .motivoDescripcion("Entrada por compra")
                .observaciones("Factura F001-9988")
                .nombreUsuario("admin")
                .fechaRegistro(LocalDateTime.now())
                .build();

        stockResponse = ProductoStockResponseDTO.builder()
                .idProducto(1L)
                .codigo("MONT-001")
                .nombre("Montura Ray-Ban")
                .stockActual(15)
                .stockMinimoReferencial(5)
                .precio(new BigDecimal("350.00"))
                .disponible(true)
                .estado(1)
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/inventario/stock/{idProducto}: Retorna 200 OK con stock actual")
    void consultarStockActual_retorna200() throws Exception {
        when(inventarioStockService.consultarStockActual(1L, 5)).thenReturn(stockResponse);

        mockMvc.perform(get("/api/v1/inventario/stock/1")
                        .param("umbralStockMinimo", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto").value(1L))
                .andExpect(jsonPath("$.stockActual").value(15))
                .andExpect(jsonPath("$.disponible").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/inventario/entradas: Retorna 201 Created al registrar entrada")
    void registrarEntradaStock_retorna201() throws Exception {
        EntradaStockRequestDTO request = EntradaStockRequestDTO.builder()
                .idProducto(1L)
                .cantidad(10)
                .motivo(1)
                .observaciones("Factura F001-9988")
                .build();

        when(inventarioStockService.registrarEntradaStock(any(EntradaStockRequestDTO.class), anyString()))
                .thenReturn(movimientoResponse);

        mockMvc.perform(post("/api/v1/inventario/entradas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idMovimiento").value(100L))
                .andExpect(jsonPath("$.stockPosterior").value(15));
    }

    @Test
    @DisplayName("POST /api/v1/inventario/ajustes: Retorna 201 Created al registrar ajuste")
    void registrarAjusteStock_retorna201() throws Exception {
        AjusteStockRequestDTO request = AjusteStockRequestDTO.builder()
                .idProducto(1L)
                .cantidad(2)
                .tipoAjuste("DECREMENTO")
                .motivo(3)
                .observaciones("Lente astillado en exhibidor")
                .build();

        MovimientoStockResponseDTO ajusteResponse = MovimientoStockResponseDTO.builder()
                .idMovimiento(101L)
                .idProducto(1L)
                .cantidad(2)
                .stockPrevio(15)
                .stockPosterior(13)
                .motivo(3)
                .motivoDescripcion("Daño / Deterioro")
                .observaciones("Lente astillado en exhibidor")
                .build();

        when(inventarioStockService.registrarAjusteStock(any(AjusteStockRequestDTO.class), anyString()))
                .thenReturn(ajusteResponse);

        mockMvc.perform(post("/api/v1/inventario/ajustes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idMovimiento").value(101L))
                .andExpect(jsonPath("$.stockPosterior").value(13));
    }

    @Test
    @DisplayName("GET /api/v1/inventario/alertas/bajo-stock: Retorna 200 OK con lista de alertas")
    void listarAlertasBajoStock_retorna200() throws Exception {
        ProductoResponseDTO alerta = ProductoResponseDTO.builder()
                .idProducto(2L)
                .codigo("MONT-002")
                .nombre("Montura Agotada")
                .stock(1)
                .build();

        when(inventarioStockService.listarAlertasBajoStock(5)).thenReturn(List.of(alerta));

        mockMvc.perform(get("/api/v1/inventario/alertas/bajo-stock")
                        .param("umbral", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("MONT-002"))
                .andExpect(jsonPath("$[0].stock").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/inventario/movimientos/{idProducto}: Retorna 200 OK con Kardex paginado")
    void listarMovimientosKardex_retorna200() throws Exception {
        when(inventarioStockService.listarMovimientosKardex(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(movimientoResponse), org.springframework.data.domain.PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/inventario/movimientos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].codigoProducto").value("MONT-001"))
                .andExpect(jsonPath("$.content[0].stockPosterior").value(15));
    }
}

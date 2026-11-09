package pe.edu.upeu.geovision_backend.controller.inventario;

import org.springframework.data.domain.PageRequest;

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
import pe.edu.upeu.geovision_backend.dto.inventario.AjusteStockRequestDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.EntradaStockRequestDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.MovimientoStockResponseDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoResponseDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoStockResponseDTO;
import pe.edu.upeu.geovision_backend.exception.GlobalExceptionHandler;
import pe.edu.upeu.geovision_backend.service.inventario.InventarioStockService;

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

    // >>>> FIXTURES
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

    // >>>> SETUP
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(inventarioStockController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        movimientoResponse = new MovimientoStockResponseDTO(100L, 1L, "MONT-001", "Montura Ray-Ban", 10, 5, 15, 1, "Entrada por compra", "Factura F001-9988", "admin", LocalDateTime.now());

        stockResponse = new ProductoStockResponseDTO(1L, "MONT-001", "Montura Ray-Ban", 15, 5, new BigDecimal("350.00"), true, 1, null);
    }

    // >>>> TESTS
    @Test
    @DisplayName("GET /api/v1/inventario/stock/{idProducto}: Retorna 200 OK con stock actual")
    void consultarStockActual_retorna200() throws Exception {
        when(inventarioStockService.consultarStockActual(1L, 5)).thenReturn(stockResponse);

        mockMvc.perform(get("/api/v1/inventario/stock/1")
                        .param("umbralStockMinimo", "5")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.stockActual").value(15))
                .andExpect(jsonPath("$.disponible").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/inventario/entradas: Retorna 201 Created al registrar entrada")
    void registrarEntradaStock_retorna201() throws Exception {
        EntradaStockRequestDTO request = new EntradaStockRequestDTO(1L, 10, 1, "Factura F001-9988");

        when(inventarioStockService.registrarEntradaStock(any(EntradaStockRequestDTO.class), anyString()))
                .thenReturn(movimientoResponse);

        mockMvc.perform(post("/api/v1/inventario/entradas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.stockPosterior").value(15));
    }

    @Test
    @DisplayName("POST /api/v1/inventario/ajustes: Retorna 201 Created al registrar ajuste")
    void registrarAjusteStock_retorna201() throws Exception {
        AjusteStockRequestDTO request = new AjusteStockRequestDTO(1L, 2, "DECREMENTO", 3, "Lente astillado en exhibidor");

        MovimientoStockResponseDTO ajusteResponse = new MovimientoStockResponseDTO(101L, 1L, null, null, 2, 15, 13, 3, "Daño / Deterioro", "Lente astillado en exhibidor", null, null);

        when(inventarioStockService.registrarAjusteStock(any(AjusteStockRequestDTO.class), anyString()))
                .thenReturn(ajusteResponse);

        mockMvc.perform(post("/api/v1/inventario/ajustes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(101L))
                .andExpect(jsonPath("$.stockPosterior").value(13));
    }

    @Test
    @DisplayName("GET /api/v1/inventario/alertas/bajo-stock: Retorna 200 OK con lista de alertas")
    void listarAlertasBajoStock_retorna200() throws Exception {
        ProductoResponseDTO alerta = new ProductoResponseDTO(2L, "MONT-002", "Montura Agotada", null, null, null, 1, null, null, null, null, null, null, null);

        when(inventarioStockService.listarAlertasBajoStock(5)).thenReturn(List.of(alerta));

        mockMvc.perform(get("/api/v1/inventario/alertas/bajo-stock")
                        .param("umbral", "5")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("MONT-002"))
                .andExpect(jsonPath("$[0].stock").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/inventario/movimientos/{idProducto}: Retorna 200 OK con Kardex paginado")
    void listarMovimientosKardex_retorna200() throws Exception {
        when(inventarioStockService.listarMovimientosKardex(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(movimientoResponse), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/inventario/movimientos/1")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].codigoProducto").value("MONT-001"))
                .andExpect(jsonPath("$.content[0].stockPosterior").value(15));
    }
}

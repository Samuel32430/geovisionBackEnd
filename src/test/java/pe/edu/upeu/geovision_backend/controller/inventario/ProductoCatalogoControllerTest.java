package pe.edu.upeu.geovision_backend.controller.inventario;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pe.edu.upeu.geovision_backend.dto.inventario.MonturaDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoRequestDTO;
import pe.edu.upeu.geovision_backend.dto.inventario.ProductoResponseDTO;
import pe.edu.upeu.geovision_backend.enums.inventario.MaterialMonturaEnum;
import pe.edu.upeu.geovision_backend.enums.inventario.ModeloMonturaEnum;
import pe.edu.upeu.geovision_backend.exception.GlobalExceptionHandler;
import pe.edu.upeu.geovision_backend.service.inventario.ProductoCatalogoService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductoCatalogoControllerTest {

    // >>>> FIXTURES
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ProductoCatalogoService productoCatalogoService;

    @InjectMocks
    private ProductoCatalogoController productoCatalogoController;

    private ProductoResponseDTO responseDTO;

    // >>>> SETUP
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(productoCatalogoController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        MonturaDTO monturaDTO = new MonturaDTO(null, null, null, null, null, "Negro", ModeloMonturaEnum.ARO_COMPLETO, null, MaterialMonturaEnum.ACETATO, null);

        responseDTO = new ProductoResponseDTO(1L, "MONT-001", "Montura Ray-Ban", null, null, new BigDecimal("350.00"), 10, 1, null, null, null, null, monturaDTO, true);
    }

    // >>>> TESTS
    @Test
    @DisplayName("GET /api/v1/productos: Retorna 200 OK con página de productos")
    void listarProductos_retorna200() throws Exception {
        when(productoCatalogoService.listarProductos(any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(responseDTO), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].codigo").value("MONT-001"))
                .andExpect(jsonPath("$.content[0].precio").value(350.00));
    }

    @Test
    @DisplayName("GET /api/v1/productos/{id}: Retorna 200 OK con detalle del producto")
    void obtenerPorId_retorna200() throws Exception {
        when(productoCatalogoService.obtenerPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Montura Ray-Ban"));
    }

    @Test
    @DisplayName("GET /api/v1/productos/disponibles-venta: Retorna 200 OK")
    void listarDisponiblesParaVenta_retorna200() throws Exception {
        when(productoCatalogoService.listarDisponiblesParaVenta(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(responseDTO), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/productos/disponibles-venta")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].codigo").value("MONT-001"));
    }

    @Test
    @DisplayName("GET /api/v1/productos/{id}/validar-disponibilidad: Retorna 200 OK con booleano")
    void validarDisponibilidad_retorna200() throws Exception {
        when(productoCatalogoService.validarDisponibilidad(1L, 5)).thenReturn(true);

        mockMvc.perform(get("/api/v1/productos/1/validar-disponibilidad")
                        .param("cantidad", "5")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cantidadSolicitada").value(5))
                .andExpect(jsonPath("$.disponible").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/productos: Retorna 201 Created al registrar producto")
    void registrarProducto_retorna201() throws Exception {
        ProductoRequestDTO request = new ProductoRequestDTO("MONT-001", "Montura Ray-Ban", null, null, new BigDecimal("350.00"), 10, 1, 1L, null);

        when(productoCatalogoService.registrarProducto(any(ProductoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("MONT-001"));
    }

    @Test
    @DisplayName("PUT /api/v1/productos/{id}: Retorna 200 OK al actualizar producto")
    void actualizarProducto_retorna200() throws Exception {
        ProductoRequestDTO request = new ProductoRequestDTO("MONT-001", "Montura Ray-Ban Actualizada", null, null, new BigDecimal("360.00"), 10, 1, 1L, null);

        when(productoCatalogoService.actualizarProducto(eq(1L), any(ProductoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("PATCH /api/v1/productos/{id}/estado: Retorna 200 OK al activar/desactivar")
    void cambiarEstado_retorna200() throws Exception {
        when(productoCatalogoService.cambiarEstado(1L, 0)).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/v1/productos/1/estado")
                        .param("estado", "0")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());
    }
}

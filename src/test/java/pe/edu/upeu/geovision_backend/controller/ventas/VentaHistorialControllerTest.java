package pe.edu.upeu.geovision_backend.controller.ventas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pe.edu.upeu.geovision_backend.dto.ventas.VentaHistorialResponseDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.VentaResponseDTO;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.exception.GlobalExceptionHandler;
import pe.edu.upeu.geovision_backend.service.ventas.VentaHistorialService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VentaHistorialControllerTest {

    // >>>> FIXTURES
    private MockMvc mockMvc;

    @Mock
    private VentaHistorialService ventaHistorialService;

    @InjectMocks
    private VentaHistorialController ventaHistorialController;

    // >>>> SETUP
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ventaHistorialController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // >>>> TESTS
    @Test
    @DisplayName("GET /api/v1/ventas/historial: Retorna 200 OK con página de ventas")
    void buscarHistorialVentas_retorna200OK() throws Exception {
        VentaHistorialResponseDTO dto = new VentaHistorialResponseDTO(200L, null, "B001-00000200", null, null, "Ana Torres", null, null, new BigDecimal("250.00"), new BigDecimal("250.00"), BigDecimal.ZERO, EstadoVentaEnum.PAGADA, null, null, null);

        Page<VentaHistorialResponseDTO> pagina = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

        when(ventaHistorialService.buscarHistorialVentas(any(), any(), any(), any(), any())).thenReturn(pagina);

        mockMvc.perform(get("/api/v1/ventas/historial")
                        .param("termino", "Ana")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(200L))
                .andExpect(jsonPath("$.content[0].clienteNombre").value("Ana Torres"));
    }

    @Test
    @DisplayName("GET /api/v1/ventas/historial/{idVenta}/detalle: Retorna 200 OK con detalle modal")
    void obtenerDetalleModalVenta_retorna200OK() throws Exception {
        VentaResponseDTO detalle = new VentaResponseDTO(200L, null, new BigDecimal("250.00"), new BigDecimal("250.00"), BigDecimal.ZERO, EstadoVentaEnum.PAGADA, null, null, null, "B001-00000200", null, null, null, null, null, null, null, null);

        when(ventaHistorialService.obtenerDetalleModalVenta(200L)).thenReturn(detalle);

        mockMvc.perform(get("/api/v1/ventas/historial/200/detalle")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(200L))
                .andExpect(jsonPath("$.numeroComprobante").value("B001-00000200"));
    }
}

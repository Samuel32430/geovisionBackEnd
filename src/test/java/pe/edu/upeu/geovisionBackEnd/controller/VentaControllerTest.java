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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pe.edu.upeu.geovisionBackEnd.dto.PagoRegistroRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ProductoVendidoDTO;
import pe.edu.upeu.geovisionBackEnd.dto.VentaRegistroRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.VentaResponseDTO;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoVentaEnum;
import pe.edu.upeu.geovisionBackEnd.enums.MetodoPagoEnum;
import pe.edu.upeu.geovisionBackEnd.enums.TipoComprobanteEnum;
import pe.edu.upeu.geovisionBackEnd.exception.GlobalExceptionHandler;
import pe.edu.upeu.geovisionBackEnd.service.service.VentaService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VentaControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private VentaService ventaService;

    @InjectMocks
    private VentaController ventaController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(ventaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/ventas: Retorna 201 Created cuando el request es válido")
    void registrarVenta_valido_retorna201Created() throws Exception {
        VentaRegistroRequestDTO request = VentaRegistroRequestDTO.builder()
                .idEmpleado(1L)
                .tipoComprobante(TipoComprobanteEnum.BOLETA)
                .total(new BigDecimal("120.00"))
                .productos(List.of(ProductoVendidoDTO.builder()
                        .idProducto(10L)
                        .cantidad(1)
                        .precioUnit(new BigDecimal("120.00"))
                        .build()))
                .pagos(List.of(PagoRegistroRequestDTO.builder()
                        .monto(new BigDecimal("120.00"))
                        .metodoPago(MetodoPagoEnum.EFECTIVO)
                        .build()))
                .build();

        VentaResponseDTO response = VentaResponseDTO.builder()
                .idVenta(1L)
                .numeroComprobante("B001-00000001")
                .total(new BigDecimal("120.00"))
                .totalPagado(new BigDecimal("120.00"))
                .saldoPendiente(BigDecimal.ZERO)
                .estado(EstadoVentaEnum.PAGADA)
                .build();

        when(ventaService.registrarVenta(any(VentaRegistroRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idVenta").value(1L))
                .andExpect(jsonPath("$.numeroComprobante").value("B001-00000001"))
                .andExpect(jsonPath("$.total").value(120.00))
                .andExpect(jsonPath("$.estado").value("PAGADA"));
    }

    @Test
    @DisplayName("POST /api/v1/ventas: Retorna 400 Bad Request cuando faltan pagos o total no es positivo")
    void registrarVenta_invalido_retorna400BadRequest() throws Exception {
        VentaRegistroRequestDTO requestInvalido = VentaRegistroRequestDTO.builder()
                .total(BigDecimal.ZERO) // No positivo
                .pagos(List.of()) // Pagos vacíos
                .build();

        mockMvc.perform(post("/api/v1/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/ventas/{id}: Retorna 200 OK con el detalle de la venta")
    void obtenerVentaPorId_existente_retorna200OK() throws Exception {
        VentaResponseDTO response = VentaResponseDTO.builder()
                .idVenta(1L)
                .numeroComprobante("B001-00000001")
                .total(new BigDecimal("120.00"))
                .totalPagado(new BigDecimal("120.00"))
                .saldoPendiente(BigDecimal.ZERO)
                .estado(EstadoVentaEnum.PAGADA)
                .build();

        when(ventaService.obtenerVentaPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/ventas/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idVenta").value(1L))
                .andExpect(jsonPath("$.numeroComprobante").value("B001-00000001"));
    }
}

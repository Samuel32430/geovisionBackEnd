package pe.edu.upeu.geovision_backend.controller.ventas;

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
import pe.edu.upeu.geovision_backend.dto.ventas.PagoPendienteResponseDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoRegistroRequestDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoResponseDTO;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.MetodoPagoEnum;
import pe.edu.upeu.geovision_backend.exception.GlobalExceptionHandler;
import pe.edu.upeu.geovision_backend.service.ventas.PagoPendienteService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PagoPendienteControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private PagoPendienteService pagoPendienteService;

    @InjectMocks
    private PagoPendienteController pagoPendienteController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(pagoPendienteController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/ventas/pagos-pendientes: Retorna 200 OK con lista de ventas adeudadas")
    void listarVentasConSaldoPendiente_retorna200OK() throws Exception {
        PagoPendienteResponseDTO dto = PagoPendienteResponseDTO.builder()
                .idVenta(100L)
                .numeroComprobante("B001-00000100")
                .totalVenta(new BigDecimal("300.00"))
                .totalPagado(new BigDecimal("100.00"))
                .saldoPendiente(new BigDecimal("200.00"))
                .estado(EstadoVentaEnum.PENDIENTE)
                .build();

        when(pagoPendienteService.listarVentasConPagosPendientes(null)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/ventas/pagos-pendientes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idVenta").value(100L))
                .andExpect(jsonPath("$[0].saldoPendiente").value(200.00));
    }

    @Test
    @DisplayName("GET /api/v1/ventas/pagos-pendientes/{idVenta}: Retorna 200 OK con detalle de deuda")
    void obtenerSaldoPendientePorVenta_retorna200OK() throws Exception {
        PagoPendienteResponseDTO dto = PagoPendienteResponseDTO.builder()
                .idVenta(100L)
                .numeroComprobante("B001-00000100")
                .totalVenta(new BigDecimal("300.00"))
                .totalPagado(new BigDecimal("100.00"))
                .saldoPendiente(new BigDecimal("200.00"))
                .estado(EstadoVentaEnum.PENDIENTE)
                .build();

        when(pagoPendienteService.obtenerSaldoPendientePorVenta(100L)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/ventas/pagos-pendientes/100")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idVenta").value(100L))
                .andExpect(jsonPath("$.saldoPendiente").value(200.00));
    }

    @Test
    @DisplayName("POST /api/v1/ventas/pagos-pendientes: Retorna 201 Created al registrar abono válido")
    void registrarPagoPendiente_valido_retorna201Created() throws Exception {
        PagoRegistroRequestDTO request = PagoRegistroRequestDTO.builder()
                .idVenta(100L)
                .monto(new BigDecimal("50.00"))
                .metodoPago(MetodoPagoEnum.EFECTIVO)
                .build();

        PagoResponseDTO response = PagoResponseDTO.builder()
                .idPago(10L)
                .idVenta(100L)
                .monto(new BigDecimal("50.00"))
                .metodoPago(MetodoPagoEnum.EFECTIVO)
                .build();

        when(pagoPendienteService.registrarPagoPendiente(any(PagoRegistroRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/ventas/pagos-pendientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPago").value(10L))
                .andExpect(jsonPath("$.monto").value(50.00));
    }

    @Test
    @DisplayName("POST /api/v1/ventas/pagos-pendientes: Retorna 400 Bad Request cuando el monto es nulo o no positivo")
    void registrarPagoPendiente_montoInvalido_retorna400BadRequest() throws Exception {
        PagoRegistroRequestDTO request = PagoRegistroRequestDTO.builder()
                .idVenta(100L)
                .monto(BigDecimal.ZERO) // No positivo
                .metodoPago(MetodoPagoEnum.EFECTIVO)
                .build();

        mockMvc.perform(post("/api/v1/ventas/pagos-pendientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

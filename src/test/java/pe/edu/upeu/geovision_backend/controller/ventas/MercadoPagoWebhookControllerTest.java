package pe.edu.upeu.geovision_backend.controller.ventas;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import pe.edu.upeu.geovision_backend.dto.ventas.MercadoPagoQrRequestDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.MercadoPagoQrResponseDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoResponseDTO;
import pe.edu.upeu.geovision_backend.enums.ventas.MetodoPagoEnum;
import pe.edu.upeu.geovision_backend.exception.GlobalExceptionHandler;
import pe.edu.upeu.geovision_backend.service.ventas.MercadoPagoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MercadoPagoWebhookControllerTest {

    // >>>> FIXTURES
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private MercadoPagoService mercadoPagoService;

    @InjectMocks
    private MercadoPagoWebhookController controller;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // >>>> TESTS
    @Test
    @DisplayName("POST /api/v1/ventas/pagos/qr: Retorna 200 OK y DTO de orden QR")
    void generarOrdenQr_valido_retorna200OK() throws Exception {
        MercadoPagoQrRequestDTO request = new MercadoPagoQrRequestDTO(101L, new BigDecimal("120.00"));

        MercadoPagoQrResponseDTO responseMock = new MercadoPagoQrResponseDTO(
                101L,
                "VENTA-101",
                new BigDecimal("120.00"),
                "00020101021243650016COM.MERCADOPAGOVENTA-101540120.0053036045802PE",
                "https://api.qrserver.com/v1/create-qr-code/?data=000201",
                "ORDER-MP-101",
                null,
                "Orden QR preparada con éxito"
        );

        when(mercadoPagoService.generarOrdenCobroQr(any(MercadoPagoQrRequestDTO.class))).thenReturn(responseMock);

        mockMvc.perform(post("/api/v1/ventas/pagos/qr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idVenta").value(101))
                .andExpect(jsonPath("$.externalReference").value("VENTA-101"))
                .andExpect(jsonPath("$.qrData").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/v1/ventas/pagos/webhook/mercadopago: Retorna 200 OK y procesa webhook")
    void recibirWebhookMercadoPago_retorna200OK() throws Exception {
        Map<String, Object> payload = Map.of(
                "action", "payment.created",
                "data", Map.of("id", "9988776655")
        );

        mockMvc.perform(post("/api/v1/ventas/pagos/webhook/mercadopago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());

        verify(mercadoPagoService).procesarWebhook(any());
    }

    @Test
    @DisplayName("POST /api/v1/ventas/pagos/confirmar-simulado: Retorna 201 Created y asienta pago")
    void confirmarPagoDigital_retorna201Created() throws Exception {
        PagoResponseDTO responseMock = new PagoResponseDTO(
                50L,
                101L,
                new BigDecimal("120.00"),
                MetodoPagoEnum.QR_YAPE_PLIN,
                "Billetera Digital / QR (Yape, Plin)",
                "MP-9988776655",
                LocalDateTime.now()
        );

        when(mercadoPagoService.confirmarPagoDigital(101L, "9988776655", new BigDecimal("120.00")))
                .thenReturn(responseMock);

        mockMvc.perform(post("/api/v1/ventas/pagos/confirmar-simulado")
                        .param("idVenta", "101")
                        .param("paymentId", "9988776655")
                        .param("monto", "120.00"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(50))
                .andExpect(jsonPath("$.metodoPago").value("QR_YAPE_PLIN"))
                .andExpect(jsonPath("$.numeroOperacion").value("MP-9988776655"));
    }
}

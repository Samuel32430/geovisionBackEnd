package pe.edu.upeu.geovision_backend.service.ventas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.geovision_backend.dto.ventas.MercadoPagoQrRequestDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.MercadoPagoQrResponseDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoResponseDTO;
import pe.edu.upeu.geovision_backend.entity.ventas.Pago;
import pe.edu.upeu.geovision_backend.entity.ventas.Venta;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.MetodoPagoEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.TipoComprobanteEnum;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.repository.ventas.PagoRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.VentaRepository;
import pe.edu.upeu.geovision_backend.service.ventas.impl.MercadoPagoServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MercadoPagoServiceTest {

    // >>>> FIXTURES
    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private MercadoPagoServiceImpl mercadoPagoService;

    private Venta ventaTest;

    @BeforeEach
    void setUp() {
        ventaTest = new Venta(
                300L,
                LocalDateTime.now(),
                new BigDecimal("180.00"),
                EstadoVentaEnum.PENDIENTE,
                "B001-00000300",
                TipoComprobanteEnum.BOLETA,
                null,
                null,
                null
        );
    }

    // >>>> TESTS
    @Test
    @DisplayName("Mercado Pago: Generar orden de cobro QR exitosamente con ID venta")
    void generarOrdenCobroQr_valido_retornaDTO() {
        MercadoPagoQrRequestDTO request = new MercadoPagoQrRequestDTO(300L, new BigDecimal("100.00"));

        when(ventaRepository.findById(300L)).thenReturn(Optional.of(ventaTest));
        when(pagoRepository.sumMontoPagadoByVentaId(300L)).thenReturn(BigDecimal.ZERO);

        MercadoPagoQrResponseDTO response = mercadoPagoService.generarOrdenCobroQr(request);

        assertThat(response).isNotNull();
        assertThat(response.idVenta()).isEqualTo(300L);
        assertThat(response.externalReference()).isEqualTo("VENTA-300");
        assertThat(response.qrData()).isNotBlank().startsWith("000201");
        assertThat(response.qrImageUrl()).isNotBlank();
    }

    @Test
    @DisplayName("Mercado Pago: Generar orden QR para nueva venta en mostrador (sin ID previo)")
    void generarOrdenCobroQr_sinIdVenta_retornaOrdenPreventa() {
        MercadoPagoQrRequestDTO request = new MercadoPagoQrRequestDTO(null, new BigDecimal("75.00"));

        MercadoPagoQrResponseDTO response = mercadoPagoService.generarOrdenCobroQr(request);

        assertThat(response).isNotNull();
        assertThat(response.idVenta()).isNull();
        assertThat(response.externalReference()).startsWith("VENTA-TEMP-");
        assertThat(response.qrData()).isNotBlank().startsWith("000201");
        assertThat(response.qrImageUrl()).isNotBlank();
    }

    @Test
    @DisplayName("Mercado Pago: Rechazar orden QR si el monto excede el saldo pendiente")
    void generarOrdenCobroQr_montoExcedeSaldo_lanzaReglaNegocioException() {
        MercadoPagoQrRequestDTO request = new MercadoPagoQrRequestDTO(300L, new BigDecimal("200.00")); // Saldo es 180

        when(ventaRepository.findById(300L)).thenReturn(Optional.of(ventaTest));
        when(pagoRepository.sumMontoPagadoByVentaId(300L)).thenReturn(BigDecimal.ZERO);

        assertThatThrownBy(() -> mercadoPagoService.generarOrdenCobroQr(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("excede el saldo pendiente");
    }

    @Test
    @DisplayName("Mercado Pago: Confirmar pago digital y actualizar estado a PAGADA si cubre la deuda")
    void confirmarPagoDigital_liquidaVenta_actualizaEstado() {
        when(ventaRepository.findByIdForUpdate(300L)).thenReturn(Optional.of(ventaTest));

        Pago pagoGuardado = new Pago(
                80L,
                LocalDateTime.now(),
                "MP-12345678",
                new BigDecimal("180.00"),
                MetodoPagoEnum.QR_YAPE_PLIN,
                ventaTest
        );
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoGuardado);
        when(pagoRepository.sumMontoPagadoByVentaId(300L)).thenReturn(new BigDecimal("180.00"));

        PagoResponseDTO resultado = mercadoPagoService.confirmarPagoDigital(300L, "12345678", new BigDecimal("180.00"));

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(80L);
        assertThat(resultado.metodoPago()).isEqualTo(MetodoPagoEnum.QR_YAPE_PLIN);
        assertThat(ventaTest.getEstado()).isEqualTo(EstadoVentaEnum.PAGADA);
        verify(ventaRepository).save(ventaTest);
    }

    @Test
    @DisplayName("Mercado Pago: Procesar webhook sin errores")
    void procesarWebhook_notificacionValida_procesaSinLanzarExcepcion() {
        Map<String, Object> payload = Map.of(
                "action", "payment.created",
                "data", Map.of("id", "9876543210")
        );

        mercadoPagoService.procesarWebhook(payload);
    }
}

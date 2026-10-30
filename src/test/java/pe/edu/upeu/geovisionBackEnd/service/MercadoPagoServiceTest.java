package pe.edu.upeu.geovisionBackEnd.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.geovisionBackEnd.dto.MercadoPagoQrRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.MercadoPagoQrResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.PagoResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.Pago;
import pe.edu.upeu.geovisionBackEnd.entity.Venta;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoVentaEnum;
import pe.edu.upeu.geovisionBackEnd.enums.MetodoPagoEnum;
import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;
import pe.edu.upeu.geovisionBackEnd.mapper.PagoMapper;
import pe.edu.upeu.geovisionBackEnd.repository.PagoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.VentaRepository;
import pe.edu.upeu.geovisionBackEnd.service.impl.MercadoPagoServiceImpl;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MercadoPagoServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private PagoMapper pagoMapper;

    @InjectMocks
    private MercadoPagoServiceImpl mercadoPagoService;

    private Venta ventaTest;

    @BeforeEach
    void setUp() {
        ventaTest = Venta.builder()
                .idVenta(300L)
                .total(new BigDecimal("180.00"))
                .estado(EstadoVentaEnum.PENDIENTE)
                .build();
    }

    @Test
    @DisplayName("Mercado Pago: Generar orden de cobro QR exitosamente")
    void generarOrdenCobroQr_valido_retornaDTO() {
        MercadoPagoQrRequestDTO request = MercadoPagoQrRequestDTO.builder()
                .idVenta(300L)
                .monto(new BigDecimal("100.00"))
                .build();

        when(ventaRepository.findById(300L)).thenReturn(Optional.of(ventaTest));
        when(pagoRepository.sumMontoPagadoByVentaId(300L)).thenReturn(BigDecimal.ZERO);

        MercadoPagoQrResponseDTO response = mercadoPagoService.generarOrdenCobroQr(request);

        assertThat(response).isNotNull();
        assertThat(response.getIdVenta()).isEqualTo(300L);
        assertThat(response.getExternalReference()).isEqualTo("VENTA-300");
        assertThat(response.getQrData()).contains("VENTA-300");
    }

    @Test
    @DisplayName("Mercado Pago: Rechazar orden QR si el monto excede el saldo pendiente")
    void generarOrdenCobroQr_montoExcedeSaldo_lanzaReglaNegocioException() {
        MercadoPagoQrRequestDTO request = MercadoPagoQrRequestDTO.builder()
                .idVenta(300L)
                .monto(new BigDecimal("200.00")) // Saldo es 180
                .build();

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

        Pago pagoGuardado = Pago.builder().idPago(80L).monto(new BigDecimal("180.00")).build();
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoGuardado);
        when(pagoRepository.sumMontoPagadoByVentaId(300L)).thenReturn(new BigDecimal("180.00"));

        PagoResponseDTO responseMock = PagoResponseDTO.builder()
                .idPago(80L)
                .idVenta(300L)
                .monto(new BigDecimal("180.00"))
                .metodoPago(MetodoPagoEnum.QR_YAPE_PLIN)
                .build();
        when(pagoMapper.toDTO(pagoGuardado)).thenReturn(responseMock);

        PagoResponseDTO resultado = mercadoPagoService.confirmarPagoDigital(300L, "12345678", new BigDecimal("180.00"));

        assertThat(resultado).isNotNull();
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

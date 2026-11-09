package pe.edu.upeu.geovision_backend.service.ventas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoPendienteResponseDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoRegistroRequestDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoResponseDTO;
import pe.edu.upeu.geovision_backend.entity.security.Empleado;
import pe.edu.upeu.geovision_backend.entity.atencion.Paciente;
import pe.edu.upeu.geovision_backend.entity.ventas.Pago;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.entity.ventas.Venta;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.MetodoPagoEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.TipoComprobanteEnum;
import pe.edu.upeu.geovision_backend.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.repository.ventas.PagoRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.VentaRepository;
import pe.edu.upeu.geovision_backend.service.ventas.impl.PagoPendienteServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoPendienteServiceTest {

    // >>>> FIXTURES
    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private PagoPendienteServiceImpl pagoPendienteService;

    private Venta ventaPendiente;
    private Empleado empleado;
    private Paciente paciente;
    private Persona personaPaciente;

    // >>>> SETUP
    @BeforeEach
    void setUp() {
        personaPaciente = new Persona(10L, "Maria", "Gomez", "987654321", null, null, null, "87654321", null);

        paciente = new Paciente(10L, personaPaciente, true, null, null);

        empleado = new Empleado(1L, null, true, null, null);

        ventaPendiente = new Venta(100L, LocalDateTime.now(), new BigDecimal("300.00"), EstadoVentaEnum.PENDIENTE, "B001-00000100", TipoComprobanteEnum.BOLETA, empleado, paciente, null);
    }

    // >>>> TESTS
    @Test
    @DisplayName("CUS07: Listar ventas con saldo pendiente aplicando filtro de búsqueda")
    void listarVentasConPagosPendientes_conFiltro_retornaLista() {
        when(ventaRepository.findVentasConPagosPendientes()).thenReturn(List.of(ventaPendiente));
        when(pagoRepository.sumMontoPagadoByVentaId(100L)).thenReturn(new BigDecimal("100.00"));
        when(pagoRepository.findByVentaId(100L)).thenReturn(Collections.emptyList());

        PagoPendienteResponseDTO dto = new PagoPendienteResponseDTO(100L, "B001-00000100", null, null, null, null, null, new BigDecimal("300.00"), new BigDecimal("100.00"), new BigDecimal("200.00"), EstadoVentaEnum.PENDIENTE, null);

        List<PagoPendienteResponseDTO> resultado = pagoPendienteService.listarVentasConPagosPendientes("Maria");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).saldoPendiente()).isEqualByComparingTo(new BigDecimal("200.00"));
    }

    @Test
    @DisplayName("CUS07: Obtener saldo pendiente por ID de venta exitoso")
    void obtenerSaldoPendientePorVenta_existente_retornaDTO() {
        when(ventaRepository.findById(100L)).thenReturn(Optional.of(ventaPendiente));
        when(pagoRepository.sumMontoPagadoByVentaId(100L)).thenReturn(new BigDecimal("100.00"));
        when(pagoRepository.findByVentaId(100L)).thenReturn(Collections.emptyList());

        PagoPendienteResponseDTO dtoMock = new PagoPendienteResponseDTO(100L, null, null, null, null, null, null, new BigDecimal("300.00"), new BigDecimal("100.00"), new BigDecimal("200.00"), EstadoVentaEnum.PENDIENTE, null);

        PagoPendienteResponseDTO resultado = pagoPendienteService.obtenerSaldoPendientePorVenta(100L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(100L);
        assertThat(resultado.saldoPendiente()).isEqualByComparingTo(new BigDecimal("200.00"));
    }

    @Test
    @DisplayName("CUS07: Registrar abono parcial exitoso manteniendo estado PENDIENTE")
    void registrarPagoPendiente_abonoParcial_mantieneEstadoPendiente() {
        PagoRegistroRequestDTO request = new PagoRegistroRequestDTO(100L, new BigDecimal("100.00"), MetodoPagoEnum.EFECTIVO, null);

        when(ventaRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(ventaPendiente));
        when(pagoRepository.sumMontoPagadoByVentaId(100L)).thenReturn(new BigDecimal("100.00"));

        Pago pagoGuardado = new Pago(50L, null, null, new BigDecimal("100.00"), null, null);
        when(pagoRepository.save(any())).thenReturn(pagoGuardado);

        PagoResponseDTO responseMock = new PagoResponseDTO(50L, 100L, new BigDecimal("100.00"), MetodoPagoEnum.EFECTIVO, null, null, null);

        PagoResponseDTO resultado = pagoPendienteService.registrarPagoPendiente(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(50L);
        assertThat(ventaPendiente.getEstado()).isEqualTo(EstadoVentaEnum.PENDIENTE);
        verify(ventaRepository, never()).save(any());
    }

    @Test
    @DisplayName("CUS07: Registrar liquidación total exitosa actualizando estado a PAGADA (RN-VE14)")
    void registrarPagoPendiente_liquidacionTotal_actualizaAPagada() {
        PagoRegistroRequestDTO request = new PagoRegistroRequestDTO(100L, new BigDecimal("200.00"), MetodoPagoEnum.EFECTIVO, null);

        when(ventaRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(ventaPendiente));
        when(pagoRepository.sumMontoPagadoByVentaId(100L)).thenReturn(new BigDecimal("100.00"));

        Pago pagoGuardado = new Pago(51L, null, null, new BigDecimal("200.00"), null, null);
        when(pagoRepository.save(any())).thenReturn(pagoGuardado);

        PagoResponseDTO responseMock = new PagoResponseDTO(51L, 100L, new BigDecimal("200.00"), MetodoPagoEnum.EFECTIVO, null, null, null);

        PagoResponseDTO resultado = pagoPendienteService.registrarPagoPendiente(request);

        assertThat(resultado).isNotNull();
        assertThat(ventaPendiente.getEstado()).isEqualTo(EstadoVentaEnum.PAGADA);
        verify(ventaRepository).save(ventaPendiente);
    }

    @Test
    @DisplayName("CUS07: Rechazar abono que excede el saldo pendiente restante (RN-VE13)")
    void registrarPagoPendiente_montoExcedeSaldo_lanzaReglaNegocioException() {
        PagoRegistroRequestDTO request = new PagoRegistroRequestDTO(100L, new BigDecimal("250.00"), MetodoPagoEnum.EFECTIVO, null);

        when(ventaRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(ventaPendiente));
        when(pagoRepository.sumMontoPagadoByVentaId(100L)).thenReturn(new BigDecimal("100.00"));

        assertThatThrownBy(() -> pagoPendienteService.registrarPagoPendiente(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("excede el saldo pendiente");

        verify(pagoRepository, never()).save(any());
    }

    @Test
    @DisplayName("CUS07: Rechazar pago en venta ya totalmente pagada (RN-VE14)")
    void registrarPagoPendiente_ventaYaPagada_lanzaReglaNegocioException() {
        ventaPendiente.setEstado(EstadoVentaEnum.PAGADA);

        PagoRegistroRequestDTO request = new PagoRegistroRequestDTO(100L, new BigDecimal("50.00"), MetodoPagoEnum.EFECTIVO, null);

        when(ventaRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(ventaPendiente));

        assertThatThrownBy(() -> pagoPendienteService.registrarPagoPendiente(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("RN-VE14");
    }

    @Test
    @DisplayName("CUS07: Rechazar pago con tarjeta sin número de operación/voucher (RN-VE12)")
    void registrarPagoPendiente_tarjetaSinVoucher_lanzaReglaNegocioException() {
        PagoRegistroRequestDTO request = new PagoRegistroRequestDTO(100L, new BigDecimal("50.00"), MetodoPagoEnum.TARJETA_CREDITO, "   ");

        when(ventaRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(ventaPendiente));

        assertThatThrownBy(() -> pagoPendienteService.registrarPagoPendiente(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("RN-VE12");
    }

    @Test
    @DisplayName("CUS07: Rechazar si la venta no existe")
    void registrarPagoPendiente_ventaNoExiste_lanzaRecursosNoEncontradosException() {
        PagoRegistroRequestDTO request = new PagoRegistroRequestDTO(999L, new BigDecimal("50.00"), MetodoPagoEnum.EFECTIVO, null);

        when(ventaRepository.findByIdForUpdate(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pagoPendienteService.registrarPagoPendiente(request))
                .isInstanceOf(RecursosNoEncontradosException.class);
    }
}

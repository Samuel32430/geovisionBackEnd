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
import pe.edu.upeu.geovision_backend.mapper.ventas.PagoMapper;
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

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private PagoMapper pagoMapper;

    @InjectMocks
    private PagoPendienteServiceImpl pagoPendienteService;

    private Venta ventaPendiente;
    private Empleado empleado;
    private Paciente paciente;
    private Persona personaPaciente;

    @BeforeEach
    void setUp() {
        personaPaciente = Persona.builder()
                .idPersona(10L)
                .nombres("Maria")
                .apellidos("Gomez")
                .numeroDocumento("87654321")
                .telefono("987654321")
                .build();

        paciente = Paciente.builder()
                .idPersona(10L)
                .persona(personaPaciente)
                .activo(true)
                .build();

        empleado = Empleado.builder()
                .idPersona(1L)
                .activo(true)
                .build();

        ventaPendiente = Venta.builder()
                .idVenta(100L)
                .numeroComprobante("B001-00000100")
                .tipoComprobante(TipoComprobanteEnum.BOLETA)
                .total(new BigDecimal("300.00"))
                .estado(EstadoVentaEnum.PENDIENTE)
                .empleado(empleado)
                .paciente(paciente)
                .fechaRegistro(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("CUS07: Listar ventas con saldo pendiente aplicando filtro de búsqueda")
    void listarVentasConPagosPendientes_conFiltro_retornaLista() {
        when(ventaRepository.findVentasConPagosPendientes()).thenReturn(List.of(ventaPendiente));
        when(pagoRepository.sumMontoPagadoByVentaId(100L)).thenReturn(new BigDecimal("100.00"));
        when(pagoRepository.findByVentaIdVenta(100L)).thenReturn(Collections.emptyList());

        PagoPendienteResponseDTO dto = PagoPendienteResponseDTO.builder()
                .idVenta(100L)
                .numeroComprobante("B001-00000100")
                .totalVenta(new BigDecimal("300.00"))
                .totalPagado(new BigDecimal("100.00"))
                .saldoPendiente(new BigDecimal("200.00"))
                .estado(EstadoVentaEnum.PENDIENTE)
                .build();

        when(pagoMapper.toPagoPendienteResponseDTO(any(), any(), any(), any())).thenReturn(dto);

        List<PagoPendienteResponseDTO> resultado = pagoPendienteService.listarVentasConPagosPendientes("Maria");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getSaldoPendiente()).isEqualByComparingTo(new BigDecimal("200.00"));
    }

    @Test
    @DisplayName("CUS07: Obtener saldo pendiente por ID de venta exitoso")
    void obtenerSaldoPendientePorVenta_existente_retornaDTO() {
        when(ventaRepository.findById(100L)).thenReturn(Optional.of(ventaPendiente));
        when(pagoRepository.sumMontoPagadoByVentaId(100L)).thenReturn(new BigDecimal("100.00"));
        when(pagoRepository.findByVentaIdVenta(100L)).thenReturn(Collections.emptyList());

        PagoPendienteResponseDTO dtoMock = PagoPendienteResponseDTO.builder()
                .idVenta(100L)
                .totalVenta(new BigDecimal("300.00"))
                .totalPagado(new BigDecimal("100.00"))
                .saldoPendiente(new BigDecimal("200.00"))
                .estado(EstadoVentaEnum.PENDIENTE)
                .build();

        when(pagoMapper.toPagoPendienteResponseDTO(any(), any(), any(), any())).thenReturn(dtoMock);

        PagoPendienteResponseDTO resultado = pagoPendienteService.obtenerSaldoPendientePorVenta(100L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdVenta()).isEqualTo(100L);
        assertThat(resultado.getSaldoPendiente()).isEqualByComparingTo(new BigDecimal("200.00"));
    }

    @Test
    @DisplayName("CUS07: Registrar abono parcial exitoso manteniendo estado PENDIENTE")
    void registrarPagoPendiente_abonoParcial_mantieneEstadoPendiente() {
        PagoRegistroRequestDTO request = PagoRegistroRequestDTO.builder()
                .idVenta(100L)
                .monto(new BigDecimal("100.00")) // Saldo restante será 100
                .metodoPago(MetodoPagoEnum.EFECTIVO)
                .build();

        when(ventaRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(ventaPendiente));
        when(pagoRepository.sumMontoPagadoByVentaId(100L)).thenReturn(new BigDecimal("100.00")); // total 300 - 100 = saldo 200

        Pago pagoGuardado = Pago.builder().idPago(50L).monto(new BigDecimal("100.00")).build();
        when(pagoMapper.toEntity(any(), any())).thenReturn(pagoGuardado);
        when(pagoRepository.save(any())).thenReturn(pagoGuardado);

        PagoResponseDTO responseMock = PagoResponseDTO.builder()
                .idPago(50L)
                .idVenta(100L)
                .monto(new BigDecimal("100.00"))
                .metodoPago(MetodoPagoEnum.EFECTIVO)
                .build();
        when(pagoMapper.toDTO(pagoGuardado)).thenReturn(responseMock);

        PagoResponseDTO resultado = pagoPendienteService.registrarPagoPendiente(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdPago()).isEqualTo(50L);
        assertThat(ventaPendiente.getEstado()).isEqualTo(EstadoVentaEnum.PENDIENTE);
        verify(ventaRepository, never()).save(any());
    }

    @Test
    @DisplayName("CUS07: Registrar liquidación total exitosa actualizando estado a PAGADA (RN-VE14)")
    void registrarPagoPendiente_liquidacionTotal_actualizaAPagada() {
        PagoRegistroRequestDTO request = PagoRegistroRequestDTO.builder()
                .idVenta(100L)
                .monto(new BigDecimal("200.00")) // Saldo restante será 0
                .metodoPago(MetodoPagoEnum.EFECTIVO)
                .build();

        when(ventaRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(ventaPendiente));
        when(pagoRepository.sumMontoPagadoByVentaId(100L)).thenReturn(new BigDecimal("100.00")); // saldo 200

        Pago pagoGuardado = Pago.builder().idPago(51L).monto(new BigDecimal("200.00")).build();
        when(pagoMapper.toEntity(any(), any())).thenReturn(pagoGuardado);
        when(pagoRepository.save(any())).thenReturn(pagoGuardado);

        PagoResponseDTO responseMock = PagoResponseDTO.builder()
                .idPago(51L)
                .idVenta(100L)
                .monto(new BigDecimal("200.00"))
                .metodoPago(MetodoPagoEnum.EFECTIVO)
                .build();
        when(pagoMapper.toDTO(pagoGuardado)).thenReturn(responseMock);

        PagoResponseDTO resultado = pagoPendienteService.registrarPagoPendiente(request);

        assertThat(resultado).isNotNull();
        assertThat(ventaPendiente.getEstado()).isEqualTo(EstadoVentaEnum.PAGADA);
        verify(ventaRepository).save(ventaPendiente);
    }

    @Test
    @DisplayName("CUS07: Rechazar abono que excede el saldo pendiente restante (RN-VE13)")
    void registrarPagoPendiente_montoExcedeSaldo_lanzaReglaNegocioException() {
        PagoRegistroRequestDTO request = PagoRegistroRequestDTO.builder()
                .idVenta(100L)
                .monto(new BigDecimal("250.00")) // Saldo restante es 200
                .metodoPago(MetodoPagoEnum.EFECTIVO)
                .build();

        when(ventaRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(ventaPendiente));
        when(pagoRepository.sumMontoPagadoByVentaId(100L)).thenReturn(new BigDecimal("100.00")); // Saldo 200

        assertThatThrownBy(() -> pagoPendienteService.registrarPagoPendiente(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("excede el saldo pendiente");

        verify(pagoRepository, never()).save(any());
    }

    @Test
    @DisplayName("CUS07: Rechazar pago en venta ya totalmente pagada (RN-VE14)")
    void registrarPagoPendiente_ventaYaPagada_lanzaReglaNegocioException() {
        ventaPendiente.setEstado(EstadoVentaEnum.PAGADA);

        PagoRegistroRequestDTO request = PagoRegistroRequestDTO.builder()
                .idVenta(100L)
                .monto(new BigDecimal("50.00"))
                .metodoPago(MetodoPagoEnum.EFECTIVO)
                .build();

        when(ventaRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(ventaPendiente));

        assertThatThrownBy(() -> pagoPendienteService.registrarPagoPendiente(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("RN-VE14");
    }

    @Test
    @DisplayName("CUS07: Rechazar pago con tarjeta sin número de operación/voucher (RN-VE12)")
    void registrarPagoPendiente_tarjetaSinVoucher_lanzaReglaNegocioException() {
        PagoRegistroRequestDTO request = PagoRegistroRequestDTO.builder()
                .idVenta(100L)
                .monto(new BigDecimal("50.00"))
                .metodoPago(MetodoPagoEnum.TARJETA_CREDITO)
                .numeroOperacion("   ") // Blanco
                .build();

        when(ventaRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(ventaPendiente));

        assertThatThrownBy(() -> pagoPendienteService.registrarPagoPendiente(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("RN-VE12");
    }

    @Test
    @DisplayName("CUS07: Rechazar si la venta no existe")
    void registrarPagoPendiente_ventaNoExiste_lanzaRecursosNoEncontradosException() {
        PagoRegistroRequestDTO request = PagoRegistroRequestDTO.builder()
                .idVenta(999L)
                .monto(new BigDecimal("50.00"))
                .metodoPago(MetodoPagoEnum.EFECTIVO)
                .build();

        when(ventaRepository.findByIdForUpdate(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pagoPendienteService.registrarPagoPendiente(request))
                .isInstanceOf(RecursosNoEncontradosException.class);
    }
}

package pe.edu.upeu.geovisionBackEnd.service;

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
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovisionBackEnd.dto.VentaHistorialResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.VentaResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.Empleado;
import pe.edu.upeu.geovisionBackEnd.entity.Paciente;
import pe.edu.upeu.geovisionBackEnd.entity.Persona;
import pe.edu.upeu.geovisionBackEnd.entity.Venta;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoVentaEnum;
import pe.edu.upeu.geovisionBackEnd.enums.TipoComprobanteEnum;
import pe.edu.upeu.geovisionBackEnd.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovisionBackEnd.mapper.VentaMapper;
import pe.edu.upeu.geovisionBackEnd.repository.PagoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.ProductoVendidoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.ServicioVendidoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.VentaRepository;
import pe.edu.upeu.geovisionBackEnd.service.impl.VentaHistorialServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VentaHistorialServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private ProductoVendidoRepository productoVendidoRepository;

    @Mock
    private ServicioVendidoRepository servicioVendidoRepository;

    @Mock
    private VentaMapper ventaMapper;

    @InjectMocks
    private VentaHistorialServiceImpl ventaHistorialService;

    private Venta ventaTest;

    @BeforeEach
    void setUp() {
        Persona persona = Persona.builder()
                .nombres("Ana")
                .apellidos("Torres")
                .numeroDocumento("77889900")
                .build();

        Paciente paciente = Paciente.builder()
                .idPersona(3L)
                .persona(persona)
                .build();

        Empleado empleado = Empleado.builder()
                .idPersona(1L)
                .build();

        ventaTest = Venta.builder()
                .idVenta(200L)
                .numeroComprobante("B001-00000200")
                .tipoComprobante(TipoComprobanteEnum.BOLETA)
                .total(new BigDecimal("250.00"))
                .estado(EstadoVentaEnum.PAGADA)
                .paciente(paciente)
                .empleado(empleado)
                .fechaRegistro(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("CUS08: Buscar historial de ventas paginado con filtros")
    void buscarHistorialVentas_paginado_retornaPaginaDTO() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Venta> paginaVentas = new PageImpl<>(List.of(ventaTest), pageable, 1);

        when(ventaRepository.buscarHistorialVentas(eq("Ana"), eq(EstadoVentaEnum.PAGADA), any(), any(), eq(pageable)))
                .thenReturn(paginaVentas);
        when(pagoRepository.sumMontoPagadoByVentaId(200L)).thenReturn(new BigDecimal("250.00"));
        when(productoVendidoRepository.findByVentaIdVenta(200L)).thenReturn(Collections.emptyList());
        when(servicioVendidoRepository.findByVentaIdVenta(200L)).thenReturn(Collections.emptyList());

        VentaHistorialResponseDTO dtoMock = VentaHistorialResponseDTO.builder()
                .idVenta(200L)
                .numeroComprobante("B001-00000200")
                .clienteNombre("Ana Torres")
                .total(new BigDecimal("250.00"))
                .totalPagado(new BigDecimal("250.00"))
                .saldoPendiente(BigDecimal.ZERO)
                .estado(EstadoVentaEnum.PAGADA)
                .build();

        when(ventaMapper.toHistorialResponseDTO(eq(ventaTest), any(), any(), any())).thenReturn(dtoMock);

        Page<VentaHistorialResponseDTO> resultado = ventaHistorialService.buscarHistorialVentas(
                "Ana",
                EstadoVentaEnum.PAGADA,
                null,
                null,
                pageable
        );

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().get(0).getClienteNombre()).isEqualTo("Ana Torres");
    }

    @Test
    @DisplayName("CUS08: Obtener detalle modal de venta existente exitoso")
    void obtenerDetalleModalVenta_existente_retornaResponseDTO() {
        when(ventaRepository.findById(200L)).thenReturn(Optional.of(ventaTest));
        when(pagoRepository.sumMontoPagadoByVentaId(200L)).thenReturn(new BigDecimal("250.00"));
        when(productoVendidoRepository.findByVentaIdVenta(200L)).thenReturn(Collections.emptyList());
        when(servicioVendidoRepository.findByVentaIdVenta(200L)).thenReturn(Collections.emptyList());
        when(pagoRepository.findByVentaIdVenta(200L)).thenReturn(Collections.emptyList());

        VentaResponseDTO dtoMock = VentaResponseDTO.builder()
                .idVenta(200L)
                .numeroComprobante("B001-00000200")
                .total(new BigDecimal("250.00"))
                .totalPagado(new BigDecimal("250.00"))
                .saldoPendiente(BigDecimal.ZERO)
                .estado(EstadoVentaEnum.PAGADA)
                .build();

        when(ventaMapper.toResponseDTO(eq(ventaTest), any(), any(), any(), any(), any()))
                .thenReturn(dtoMock);

        VentaResponseDTO resultado = ventaHistorialService.obtenerDetalleModalVenta(200L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdVenta()).isEqualTo(200L);
        assertThat(resultado.getNumeroComprobante()).isEqualTo("B001-00000200");
    }

    @Test
    @DisplayName("CUS08: Rechazar detalle modal si la venta no existe")
    void obtenerDetalleModalVenta_noExiste_lanzaRecursosNoEncontradosException() {
        when(ventaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ventaHistorialService.obtenerDetalleModalVenta(999L))
                .isInstanceOf(RecursosNoEncontradosException.class);
    }
}

package pe.edu.upeu.geovisionBackEnd.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.geovisionBackEnd.dto.PagoRegistroRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ProductoVendidoDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ServicioVendidoDTO;
import pe.edu.upeu.geovisionBackEnd.dto.VentaRegistroRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.VentaResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.Empleado;
import pe.edu.upeu.geovisionBackEnd.entity.Paciente;
import pe.edu.upeu.geovisionBackEnd.entity.Pago;
import pe.edu.upeu.geovisionBackEnd.entity.Persona;
import pe.edu.upeu.geovisionBackEnd.entity.Producto;
import pe.edu.upeu.geovisionBackEnd.entity.ProductoVendido;
import pe.edu.upeu.geovisionBackEnd.entity.Receta;
import pe.edu.upeu.geovisionBackEnd.entity.Servicio;
import pe.edu.upeu.geovisionBackEnd.entity.ServicioVendido;
import pe.edu.upeu.geovisionBackEnd.entity.Venta;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoGeneralEnum;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoVentaEnum;
import pe.edu.upeu.geovisionBackEnd.enums.MetodoPagoEnum;
import pe.edu.upeu.geovisionBackEnd.enums.TipoComprobanteEnum;
import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;
import pe.edu.upeu.geovisionBackEnd.mapper.PagoMapper;
import pe.edu.upeu.geovisionBackEnd.mapper.VentaMapper;
import pe.edu.upeu.geovisionBackEnd.repository.EmpleadoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.PacienteRepository;
import pe.edu.upeu.geovisionBackEnd.repository.PagoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.ProductoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.ProductoVendidoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.RecetaRepository;
import pe.edu.upeu.geovisionBackEnd.repository.ServicioRepository;
import pe.edu.upeu.geovisionBackEnd.repository.ServicioVendidoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.UsuarioRepository;
import pe.edu.upeu.geovisionBackEnd.repository.VentaRepository;
import pe.edu.upeu.geovisionBackEnd.service.impl.VentaServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private ProductoVendidoRepository productoVendidoRepository;

    @Mock
    private ServicioVendidoRepository servicioVendidoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ServicioRepository servicioRepository;

    @Mock
    private RecetaRepository recetaRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private VentaMapper ventaMapper;

    @Mock
    private PagoMapper pagoMapper;

    @InjectMocks
    private VentaServiceImpl ventaService;

    private Empleado empleadoTest;
    private Persona personaEmpleado;
    private Paciente pacienteTest;
    private Persona personaPaciente;
    private Producto productoTest;
    private Servicio servicioTest;

    @BeforeEach
    void setUp() {
        personaEmpleado = Persona.builder()
                .idPersona(1L)
                .nombres("Carlos")
                .apellidos("Vendedor")
                .numeroDocumento("44556677")
                .build();

        empleadoTest = Empleado.builder()
                .idPersona(1L)
                .persona(personaEmpleado)
                .estado(EstadoGeneralEnum.ACTIVO)
                .build();

        personaPaciente = Persona.builder()
                .idPersona(2L)
                .nombres("Juan")
                .apellidos("Pérez")
                .numeroDocumento("12345678")
                .build();

        pacienteTest = Paciente.builder()
                .idPersona(2L)
                .persona(personaPaciente)
                .estado(EstadoGeneralEnum.ACTIVO)
                .build();

        productoTest = Producto.builder()
                .idProducto(10L)
                .codigo("MONT-001")
                .nombre("Montura RayBan")
                .precio(new BigDecimal("150.00"))
                .stock(10)
                .estado(EstadoGeneralEnum.ACTIVO)
                .build();

        servicioTest = Servicio.builder()
                .idServicio(20L)
                .nombre("Medida de vista")
                .precio(new BigDecimal("30.00"))
                .estado(EstadoGeneralEnum.ACTIVO)
                .build();
    }

    @Test
    @DisplayName("CUS06: Registrar venta directa de productos al 100% exitosamente")
    void registrarVenta_directa100Porciento_exitoso() {
        VentaRegistroRequestDTO request = VentaRegistroRequestDTO.builder()
                .idEmpleado(1L)
                .idPaciente(2L)
                .tipoComprobante(TipoComprobanteEnum.BOLETA)
                .total(new BigDecimal("150.00"))
                .productos(List.of(ProductoVendidoDTO.builder()
                        .idProducto(10L)
                        .cantidad(1)
                        .precioUnit(new BigDecimal("150.00"))
                        .build()))
                .pagos(List.of(PagoRegistroRequestDTO.builder()
                        .monto(new BigDecimal("150.00"))
                        .metodoPago(MetodoPagoEnum.EFECTIVO)
                        .build()))
                .build();

        when(empleadoRepository.findByIdPersonaAndEstado(1L, EstadoGeneralEnum.ACTIVO))
                .thenReturn(Optional.of(empleadoTest));
        when(pacienteRepository.findById(2L))
                .thenReturn(Optional.of(pacienteTest));
        when(productoRepository.findById(10L))
                .thenReturn(Optional.of(productoTest));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> {
            Venta v = inv.getArgument(0);
            v.setIdVenta(100L);
            return v;
        });
        when(pagoMapper.toEntity(any(), any())).thenReturn(Pago.builder().idPago(1L).monto(new BigDecimal("150.00")).build());
        when(pagoRepository.save(any(Pago.class))).thenReturn(Pago.builder().idPago(1L).build());

        VentaResponseDTO responseMock = VentaResponseDTO.builder()
                .idVenta(100L)
                .total(new BigDecimal("150.00"))
                .totalPagado(new BigDecimal("150.00"))
                .saldoPendiente(BigDecimal.ZERO)
                .estado(EstadoVentaEnum.PAGADA)
                .build();

        when(ventaMapper.toResponseDTO(any(), any(), any(), any(), any(), any()))
                .thenReturn(responseMock);

        VentaResponseDTO resultado = ventaService.registrarVenta(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo(EstadoVentaEnum.PAGADA);
        assertThat(resultado.getSaldoPendiente()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(productoTest.getStock()).isEqualTo(9); // 10 - 1
        verify(productoRepository).save(productoTest);
        verify(ventaRepository).save(any(Venta.class));
    }

    @Test
    @DisplayName("CUS06: Rechazar venta cuando el stock disponible es insuficiente (RN-VE05)")
    void registrarVenta_stockInsuficiente_lanzaReglaNegocioException() {
        productoTest.setStock(2);

        VentaRegistroRequestDTO request = VentaRegistroRequestDTO.builder()
                .idEmpleado(1L)
                .tipoComprobante(TipoComprobanteEnum.BOLETA)
                .total(new BigDecimal("450.00"))
                .productos(List.of(ProductoVendidoDTO.builder()
                        .idProducto(10L)
                        .cantidad(3) // solicita 3, hay 2
                        .precioUnit(new BigDecimal("150.00"))
                        .build()))
                .pagos(List.of(PagoRegistroRequestDTO.builder()
                        .monto(new BigDecimal("450.00"))
                        .metodoPago(MetodoPagoEnum.EFECTIVO)
                        .build()))
                .build();

        when(empleadoRepository.findByIdPersonaAndEstado(1L, EstadoGeneralEnum.ACTIVO))
                .thenReturn(Optional.of(empleadoTest));
        when(productoRepository.findById(10L))
                .thenReturn(Optional.of(productoTest));

        assertThatThrownBy(() -> ventaService.registrarVenta(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("Stock insuficiente");

        verify(ventaRepository, never()).save(any());
    }

    @Test
    @DisplayName("CUS06: Rechazar pago parcial en venta directa sin receta (RN-VE06)")
    void registrarVenta_sinRecetaPagoParcial_lanzaReglaNegocioException() {
        VentaRegistroRequestDTO request = VentaRegistroRequestDTO.builder()
                .idEmpleado(1L)
                .tipoComprobante(TipoComprobanteEnum.BOLETA)
                .total(new BigDecimal("150.00"))
                .productos(List.of(ProductoVendidoDTO.builder()
                        .idProducto(10L)
                        .cantidad(1)
                        .precioUnit(new BigDecimal("150.00"))
                        .build()))
                .pagos(List.of(PagoRegistroRequestDTO.builder()
                        .monto(new BigDecimal("50.00")) // Solo paga 50 de 150
                        .metodoPago(MetodoPagoEnum.EFECTIVO)
                        .build()))
                .build();

        when(empleadoRepository.findByIdPersonaAndEstado(1L, EstadoGeneralEnum.ACTIVO))
                .thenReturn(Optional.of(empleadoTest));
        when(productoRepository.findById(10L))
                .thenReturn(Optional.of(productoTest));

        assertThatThrownBy(() -> ventaService.registrarVenta(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("RN-VE06");
    }

    @Test
    @DisplayName("CUS06: Venta con receta y adelanto mayor al 25% con fecha de entrega es exitosa (RN-VE07, RN-VE08)")
    void registrarVenta_conRecetaYAdelantoValido_exitoso() {
        Receta receta = Receta.builder()
                .idReceta(5L)
                .paciente(pacienteTest)
                .empleado(empleadoTest)
                .esExterno(0)
                .build();

        VentaRegistroRequestDTO request = VentaRegistroRequestDTO.builder()
                .idEmpleado(1L)
                .idPaciente(2L)
                .idReceta(5L)
                .tipoComprobante(TipoComprobanteEnum.BOLETA)
                .fechaEntregaEstimada(LocalDate.now().plusDays(3))
                .total(new BigDecimal("200.00"))
                .servicios(List.of(ServicioVendidoDTO.builder()
                        .idServicio(20L)
                        .precio(new BigDecimal("30.00"))
                        .build()))
                .pagos(List.of(PagoRegistroRequestDTO.builder()
                        .monto(new BigDecimal("60.00")) // 30% > 25%
                        .metodoPago(MetodoPagoEnum.EFECTIVO)
                        .build()))
                .build();

        when(empleadoRepository.findByIdPersonaAndEstado(1L, EstadoGeneralEnum.ACTIVO))
                .thenReturn(Optional.of(empleadoTest));
        when(pacienteRepository.findById(2L))
                .thenReturn(Optional.of(pacienteTest));
        when(recetaRepository.findByIdForUpdate(5L))
                .thenReturn(Optional.of(receta));
        when(servicioRepository.findById(20L))
                .thenReturn(Optional.of(servicioTest));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));
        when(pagoMapper.toEntity(any(), any())).thenReturn(Pago.builder().build());

        VentaResponseDTO responseMock = VentaResponseDTO.builder()
                .total(new BigDecimal("200.00"))
                .totalPagado(new BigDecimal("60.00"))
                .saldoPendiente(new BigDecimal("140.00"))
                .estado(EstadoVentaEnum.PENDIENTE)
                .build();
        when(ventaMapper.toResponseDTO(any(), any(), any(), any(), any(), any()))
                .thenReturn(responseMock);

        VentaResponseDTO response = ventaService.registrarVenta(request);

        assertThat(response).isNotNull();
        assertThat(response.getEstado()).isEqualTo(EstadoVentaEnum.PENDIENTE);
        assertThat(response.getSaldoPendiente()).isEqualByComparingTo(new BigDecimal("140.00"));
    }

    @Test
    @DisplayName("CUS06: Rechazar venta con receta si el adelanto es inferior al 25% (RN-VE07)")
    void registrarVenta_adelantoMenorAl25Porciento_lanzaReglaNegocioException() {
        Receta receta = Receta.builder()
                .idReceta(5L)
                .paciente(pacienteTest)
                .empleado(empleadoTest)
                .esExterno(0)
                .build();

        VentaRegistroRequestDTO request = VentaRegistroRequestDTO.builder()
                .idEmpleado(1L)
                .idPaciente(2L)
                .idReceta(5L)
                .tipoComprobante(TipoComprobanteEnum.BOLETA)
                .fechaEntregaEstimada(LocalDate.now().plusDays(3))
                .total(new BigDecimal("200.00"))
                .pagos(List.of(PagoRegistroRequestDTO.builder()
                        .monto(new BigDecimal("40.00")) // 40 es 20%, mínimo es 50
                        .metodoPago(MetodoPagoEnum.EFECTIVO)
                        .build()))
                .build();

        when(empleadoRepository.findByIdPersonaAndEstado(1L, EstadoGeneralEnum.ACTIVO))
                .thenReturn(Optional.of(empleadoTest));
        when(pacienteRepository.findById(2L))
                .thenReturn(Optional.of(pacienteTest));
        when(recetaRepository.findByIdForUpdate(5L))
                .thenReturn(Optional.of(receta));

        assertThatThrownBy(() -> ventaService.registrarVenta(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("RN-VE07");
    }

    @Test
    @DisplayName("CUS06: Rechazar venta con adelanto si no se indica fecha de entrega (RN-VE08)")
    void registrarVenta_adelantoSinFechaEntrega_lanzaReglaNegocioException() {
        Receta receta = Receta.builder()
                .idReceta(5L)
                .paciente(pacienteTest)
                .empleado(empleadoTest)
                .esExterno(0)
                .build();

        VentaRegistroRequestDTO request = VentaRegistroRequestDTO.builder()
                .idEmpleado(1L)
                .idPaciente(2L)
                .idReceta(5L)
                .tipoComprobante(TipoComprobanteEnum.BOLETA)
                .fechaEntregaEstimada(null) // Falta fecha de entrega
                .total(new BigDecimal("200.00"))
                .pagos(List.of(PagoRegistroRequestDTO.builder()
                        .monto(new BigDecimal("60.00"))
                        .metodoPago(MetodoPagoEnum.EFECTIVO)
                        .build()))
                .build();

        when(empleadoRepository.findByIdPersonaAndEstado(1L, EstadoGeneralEnum.ACTIVO))
                .thenReturn(Optional.of(empleadoTest));
        when(pacienteRepository.findById(2L))
                .thenReturn(Optional.of(pacienteTest));
        when(recetaRepository.findByIdForUpdate(5L))
                .thenReturn(Optional.of(receta));

        assertThatThrownBy(() -> ventaService.registrarVenta(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("RN-VE08");
    }

    @Test
    @DisplayName("CUS06: Rechazar pago con tarjeta sin número de operación/voucher (RN-VE12)")
    void registrarVenta_tarjetaSinVoucher_lanzaReglaNegocioException() {
        VentaRegistroRequestDTO request = VentaRegistroRequestDTO.builder()
                .idEmpleado(1L)
                .tipoComprobante(TipoComprobanteEnum.BOLETA)
                .total(new BigDecimal("150.00"))
                .productos(List.of(ProductoVendidoDTO.builder()
                        .idProducto(10L)
                        .cantidad(1)
                        .precioUnit(new BigDecimal("150.00"))
                        .build()))
                .pagos(List.of(PagoRegistroRequestDTO.builder()
                        .monto(new BigDecimal("150.00"))
                        .metodoPago(MetodoPagoEnum.TARJETA_DEBITO)
                        .numeroOperacion("") // Vacío
                        .build()))
                .build();

        when(empleadoRepository.findByIdPersonaAndEstado(1L, EstadoGeneralEnum.ACTIVO))
                .thenReturn(Optional.of(empleadoTest));
        when(productoRepository.findById(10L))
                .thenReturn(Optional.of(productoTest));

        assertThatThrownBy(() -> ventaService.registrarVenta(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("RN-VE12");
    }
}

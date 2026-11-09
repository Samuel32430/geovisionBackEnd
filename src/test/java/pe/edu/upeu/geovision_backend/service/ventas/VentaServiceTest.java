package pe.edu.upeu.geovision_backend.service.ventas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoRegistroRequestDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.ProductoVendidoDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.ServicioVendidoDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.VentaRegistroRequestDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.VentaResponseDTO;
import pe.edu.upeu.geovision_backend.entity.security.Empleado;
import pe.edu.upeu.geovision_backend.entity.atencion.Paciente;
import pe.edu.upeu.geovision_backend.entity.ventas.Pago;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.entity.inventario.Producto;
import pe.edu.upeu.geovision_backend.entity.ventas.ProductoVendido;
import pe.edu.upeu.geovision_backend.entity.atencion.Receta;
import pe.edu.upeu.geovision_backend.entity.gestion_mantenimiento.Servicio;
import pe.edu.upeu.geovision_backend.entity.ventas.ServicioVendido;
import pe.edu.upeu.geovision_backend.entity.ventas.Venta;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.MetodoPagoEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.TipoComprobanteEnum;
import pe.edu.upeu.geovision_backend.exception.ReglaNegocioException;
import pe.edu.upeu.geovision_backend.repository.security.EmpleadoRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.PacienteRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.PagoRepository;
import pe.edu.upeu.geovision_backend.repository.inventario.ProductoRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.ProductoVendidoRepository;
import pe.edu.upeu.geovision_backend.repository.atencion.RecetaRepository;
import pe.edu.upeu.geovision_backend.repository.gestion_mantenimiento.ServicioRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.ServicioVendidoRepository;
import pe.edu.upeu.geovision_backend.repository.security.UsuarioRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.VentaRepository;
import pe.edu.upeu.geovision_backend.service.ventas.impl.VentaServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    // >>>> FIXTURES
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

    @InjectMocks
    private VentaServiceImpl ventaService;

    private Empleado empleadoTest;
    private Persona personaEmpleado;
    private Paciente pacienteTest;
    private Persona personaPaciente;
    private Producto productoTest;
    private Servicio servicioTest;

    // >>>> SETUP
    @BeforeEach
    void setUp() {
        personaEmpleado = new Persona(1L, "Carlos", "Vendedor", null, null, null, null, "44556677", null);

        empleadoTest = new Empleado(1L, personaEmpleado, true, null, null);

        personaPaciente = new Persona(2L, "Juan", "Pérez", null, null, null, null, "12345678", null);

        pacienteTest = new Paciente(2L, personaPaciente, true, null, null);

        productoTest = new Producto(10L, "MONT-001", "Montura RayBan", null, null, new BigDecimal("150.00"), 10, true, null, null);

        servicioTest = new Servicio(20L, "Medida de vista", null, new BigDecimal("30.00"), true);
    }

    // >>>> TESTS
    @Test
    @DisplayName("CUS06: Registrar venta directa de productos al 100% exitosamente")
    void registrarVenta_directa100Porciento_exitoso() {
        VentaRegistroRequestDTO request = new VentaRegistroRequestDTO(1L, 2L, null, TipoComprobanteEnum.BOLETA, null, null, List.of(new ProductoVendidoDTO(null, 10L, null, null, 1, new BigDecimal("150.00"), null)), null, List.of(new PagoRegistroRequestDTO(null, new BigDecimal("150.00"), MetodoPagoEnum.EFECTIVO, null)), new BigDecimal("150.00"));

        when(empleadoRepository.findByIdAndActivo(1L, true))
                .thenReturn(Optional.of(empleadoTest));
        when(pacienteRepository.findById(2L))
                .thenReturn(Optional.of(pacienteTest));
        when(productoRepository.findById(10L))
                .thenReturn(Optional.of(productoTest));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> {
            Venta v = inv.getArgument(0);
            v.setId(100L);
            return v;
        }
        );
        when(pagoRepository.save(any(Pago.class))).thenReturn(new Pago(1L, null, null, null, null, null));

        VentaResponseDTO responseMock = new VentaResponseDTO(100L, null, new BigDecimal("150.00"), new BigDecimal("150.00"), BigDecimal.ZERO, EstadoVentaEnum.PAGADA, null, null, null, null, null, null, null, null, null, null, null, null);

        VentaResponseDTO resultado = ventaService.registrarVenta(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.estado()).isEqualTo(EstadoVentaEnum.PAGADA);
        assertThat(resultado.saldoPendiente()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(productoTest.getStock()).isEqualTo(9);
        verify(productoRepository).save(productoTest);
        verify(ventaRepository).save(any(Venta.class));
    }

    @Test
    @DisplayName("CUS06: Rechazar venta cuando el stock disponible es insuficiente (RN-VE05)")
    void registrarVenta_stockInsuficiente_lanzaReglaNegocioException() {
        productoTest.setStock(2);

        VentaRegistroRequestDTO request = new VentaRegistroRequestDTO(1L, null, null, TipoComprobanteEnum.BOLETA, null, null, List.of(new ProductoVendidoDTO(null, 10L, null, null, 3, new BigDecimal("150.00"), null)), null, List.of(new PagoRegistroRequestDTO(null, new BigDecimal("450.00"), MetodoPagoEnum.EFECTIVO, null)), new BigDecimal("450.00"));

        when(empleadoRepository.findByIdAndActivo(1L, true))
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
        VentaRegistroRequestDTO request = new VentaRegistroRequestDTO(1L, null, null, TipoComprobanteEnum.BOLETA, null, null, List.of(new ProductoVendidoDTO(null, 10L, null, null, 1, new BigDecimal("150.00"), null)), null, List.of(new PagoRegistroRequestDTO(null, new BigDecimal("50.00"), MetodoPagoEnum.EFECTIVO, null)), new BigDecimal("150.00"));

        when(empleadoRepository.findByIdAndActivo(1L, true))
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
        Receta receta = new Receta(5L, null, 0, pacienteTest, empleadoTest);

        VentaRegistroRequestDTO request = new VentaRegistroRequestDTO(1L, 2L, 5L, TipoComprobanteEnum.BOLETA, null, LocalDate.now().plusDays(3), null, List.of(new ServicioVendidoDTO(null, 20L, null, new BigDecimal("30.00"))), List.of(new PagoRegistroRequestDTO(null, new BigDecimal("60.00"), MetodoPagoEnum.EFECTIVO, null)), new BigDecimal("200.00"));

        when(empleadoRepository.findByIdAndActivo(1L, true))
                .thenReturn(Optional.of(empleadoTest));
        when(pacienteRepository.findById(2L))
                .thenReturn(Optional.of(pacienteTest));
        when(recetaRepository.findByIdForUpdate(5L))
                .thenReturn(Optional.of(receta));
        when(servicioRepository.findById(20L))
                .thenReturn(Optional.of(servicioTest));
        when(ventaRepository.save(any(Venta.class))).thenAnswer(inv -> inv.getArgument(0));

        VentaResponseDTO responseMock = new VentaResponseDTO(null, null, new BigDecimal("200.00"), new BigDecimal("60.00"), new BigDecimal("140.00"), EstadoVentaEnum.PENDIENTE, null, null, null, null, null, null, null, null, null, null, null, null);

        VentaResponseDTO response = ventaService.registrarVenta(request);

        assertThat(response).isNotNull();
        assertThat(response.estado()).isEqualTo(EstadoVentaEnum.PENDIENTE);
        assertThat(response.saldoPendiente()).isEqualByComparingTo(new BigDecimal("140.00"));
    }

    @Test
    @DisplayName("CUS06: Rechazar venta con receta si el adelanto es inferior al 25% (RN-VE07)")
    void registrarVenta_adelantoMenorAl25Porciento_lanzaReglaNegocioException() {
        Receta receta = new Receta(5L, null, 0, pacienteTest, empleadoTest);

        VentaRegistroRequestDTO request = new VentaRegistroRequestDTO(1L, 2L, 5L, TipoComprobanteEnum.BOLETA, null, LocalDate.now().plusDays(3), null, null, List.of(new PagoRegistroRequestDTO(null, new BigDecimal("40.00"), MetodoPagoEnum.EFECTIVO, null)), new BigDecimal("200.00"));

        when(empleadoRepository.findByIdAndActivo(1L, true))
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
        Receta receta = new Receta(5L, null, 0, pacienteTest, empleadoTest);

        VentaRegistroRequestDTO request = new VentaRegistroRequestDTO(1L, 2L, 5L, TipoComprobanteEnum.BOLETA, null, null, null, null, List.of(new PagoRegistroRequestDTO(null, new BigDecimal("60.00"), MetodoPagoEnum.EFECTIVO, null)), new BigDecimal("200.00"));

        when(empleadoRepository.findByIdAndActivo(1L, true))
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
        VentaRegistroRequestDTO request = new VentaRegistroRequestDTO(1L, null, null, TipoComprobanteEnum.BOLETA, null, null, List.of(new ProductoVendidoDTO(null, 10L, null, null, 1, new BigDecimal("150.00"), null)), null, List.of(new PagoRegistroRequestDTO(null, new BigDecimal("150.00"), MetodoPagoEnum.TARJETA_DEBITO, "")), new BigDecimal("150.00"));

        when(empleadoRepository.findByIdAndActivo(1L, true))
                .thenReturn(Optional.of(empleadoTest));
        when(productoRepository.findById(10L))
                .thenReturn(Optional.of(productoTest));

        assertThatThrownBy(() -> ventaService.registrarVenta(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("RN-VE12");
    }
}

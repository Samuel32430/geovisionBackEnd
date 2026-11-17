package pe.edu.upeu.geovision_backend.service.reports;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.geovision_backend.dto.reports.FiltroReporteVentasDTO;
import pe.edu.upeu.geovision_backend.dto.reports.ReporteRankingComercialDTO;
import pe.edu.upeu.geovision_backend.dto.reports.ReporteVentasPeriodoDTO;
import pe.edu.upeu.geovision_backend.entity.atencion.Paciente;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.entity.inventario.Producto;
import pe.edu.upeu.geovision_backend.entity.security.Empleado;
import pe.edu.upeu.geovision_backend.entity.ventas.Pago;
import pe.edu.upeu.geovision_backend.entity.ventas.ProductoVendido;
import pe.edu.upeu.geovision_backend.entity.ventas.Venta;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.MetodoPagoEnum;
import pe.edu.upeu.geovision_backend.enums.ventas.TipoComprobanteEnum;
import pe.edu.upeu.geovision_backend.repository.gestion_configuracion.ConfiguracionEmpresaRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.PagoRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.ProductoVendidoRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.ServicioVendidoRepository;
import pe.edu.upeu.geovision_backend.repository.ventas.VentaRepository;
import pe.edu.upeu.geovision_backend.service.reports.impl.ReporteVentaServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteVentaServiceTest {

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
    private ConfiguracionEmpresaRepository configuracionEmpresaRepository;

    @InjectMocks
    private ReporteVentaServiceImpl reporteVentaService;

    private Venta venta1;
    private Venta venta2;

    // >>>> SETUP
    @BeforeEach
    void setUp() {
        Persona personaCliente = new Persona();
        personaCliente.setId(10L);
        personaCliente.setNombres("Juan");
        personaCliente.setApellidos("Pérez");
        personaCliente.setNumeroDocumento("70112233");

        Paciente paciente = new Paciente();
        paciente.setId(10L);
        paciente.setPersona(personaCliente);

        Persona personaEmpleado = new Persona();
        personaEmpleado.setId(20L);
        personaEmpleado.setNombres("Carlos");
        personaEmpleado.setApellidos("Vendedor");
        personaEmpleado.setNumeroDocumento("40112233");

        Empleado empleado = new Empleado();
        empleado.setId(20L);
        empleado.setPersona(personaEmpleado);

        venta1 = new Venta();
        venta1.setId(1L);
        venta1.setFechaRegistro(LocalDateTime.now().minusDays(1));
        venta1.setNumeroComprobante("B001-00000001");
        venta1.setTipoComprobante(TipoComprobanteEnum.BOLETA);
        venta1.setEstado(EstadoVentaEnum.PAGADA);
        venta1.setTotal(new BigDecimal("300.00"));
        venta1.setPaciente(paciente);
        venta1.setEmpleado(empleado);

        venta2 = new Venta();
        venta2.setId(2L);
        venta2.setFechaRegistro(LocalDateTime.now());
        venta2.setNumeroComprobante("F001-00000001");
        venta2.setTipoComprobante(TipoComprobanteEnum.FACTURA);
        venta2.setEstado(EstadoVentaEnum.PENDIENTE);
        venta2.setTotal(new BigDecimal("500.00"));
        venta2.setPaciente(paciente);
        venta2.setEmpleado(empleado);
    }

    // >>>> TESTS
    @Test
    @DisplayName("CUN 8 / CU27: obtenerReporteVentasPeriodo calcula correctamente totales, cobros y saldos")
    void obtenerReporteVentasPeriodo_calculaTotalesCorrectos() {
        when(ventaRepository.findAll()).thenReturn(List.of(venta1, venta2));

        when(pagoRepository.sumMontoPagadoByVentaId(1L)).thenReturn(new BigDecimal("300.00"));
        when(pagoRepository.sumMontoPagadoByVentaId(2L)).thenReturn(new BigDecimal("200.00"));

        Pago pago1 = new Pago();
        pago1.setId(1L);
        pago1.setMonto(new BigDecimal("300.00"));
        pago1.setMetodoPago(MetodoPagoEnum.EFECTIVO);

        Pago pago2 = new Pago();
        pago2.setId(2L);
        pago2.setMonto(new BigDecimal("200.00"));
        pago2.setMetodoPago(MetodoPagoEnum.QR_YAPE_PLIN);

        when(pagoRepository.findByVentaId(1L)).thenReturn(List.of(pago1));
        when(pagoRepository.findByVentaId(2L)).thenReturn(List.of(pago2));

        when(productoVendidoRepository.findByVentaId(any())).thenReturn(Collections.emptyList());
        when(servicioVendidoRepository.findByVentaId(any())).thenReturn(Collections.emptyList());

        FiltroReporteVentasDTO filtro = new FiltroReporteVentasDTO(
                LocalDate.now().minusDays(7),
                LocalDate.now(),
                null,
                null,
                null
        );

        ReporteVentasPeriodoDTO resultado = reporteVentaService.obtenerReporteVentasPeriodo(filtro);

        assertThat(resultado).isNotNull();
        assertThat(resultado.totalVentas()).isEqualTo(2);
        assertThat(resultado.totalVendido()).isEqualByComparingTo(new BigDecimal("800.00"));
        assertThat(resultado.totalCobrado()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(resultado.totalSaldoPendiente()).isEqualByComparingTo(new BigDecimal("300.00"));
        assertThat(resultado.ticketPromedio()).isEqualByComparingTo(new BigDecimal("400.00"));

        assertThat(resultado.ventas()).hasSize(2);
        assertThat(resultado.kpis()).hasSize(5);
    }

    @Test
    @DisplayName("obtenerReporteRankingComercial ordena productos y servicios de mayor a menor recaudación")
    void obtenerReporteRankingComercial_ordenaPorRecaudacionDescendente() {
        when(ventaRepository.findAll()).thenReturn(List.of(venta1));

        Producto prodA = new Producto();
        prodA.setId(101L);
        prodA.setCodigo("MONT-01");
        prodA.setNombre("Montura Premium");
        prodA.setPrecio(new BigDecimal("200.00"));

        Producto prodB = new Producto();
        prodB.setId(102L);
        prodB.setCodigo("MONT-02");
        prodB.setNombre("Montura Basica");
        prodB.setPrecio(new BigDecimal("100.00"));

        ProductoVendido pv1 = new ProductoVendido();
        pv1.setProducto(prodA);
        pv1.setCantidad(2);
        pv1.setSubTotal(new BigDecimal("400.00"));

        ProductoVendido pv2 = new ProductoVendido();
        pv2.setProducto(prodB);
        pv2.setCantidad(1);
        pv2.setSubTotal(new BigDecimal("100.00"));

        when(productoVendidoRepository.findByVentaId(1L)).thenReturn(List.of(pv1, pv2));
        when(servicioVendidoRepository.findByVentaId(1L)).thenReturn(Collections.emptyList());

        ReporteRankingComercialDTO ranking = reporteVentaService.obtenerReporteRankingComercial(null);

        assertThat(ranking).isNotNull();
        assertThat(ranking.items()).hasSize(2);
        assertThat(ranking.items().get(0).codigo()).isEqualTo("MONT-01");
        assertThat(ranking.items().get(0).ranking()).isEqualTo(1);
        assertThat(ranking.items().get(1).codigo()).isEqualTo("MONT-02");
        assertThat(ranking.items().get(1).ranking()).isEqualTo(2);
    }

    @Test
    @DisplayName("exportarReporteVentasPeriodoPdf genera archivo PDF válido")
    void exportarReporteVentasPeriodoPdf_generaPdfValido() {
        when(ventaRepository.findAll()).thenReturn(List.of(venta1));
        when(pagoRepository.sumMontoPagadoByVentaId(1L)).thenReturn(new BigDecimal("300.00"));
        when(pagoRepository.findByVentaId(1L)).thenReturn(Collections.emptyList());
        when(productoVendidoRepository.findByVentaId(any())).thenReturn(Collections.emptyList());
        when(servicioVendidoRepository.findByVentaId(any())).thenReturn(Collections.emptyList());
        when(configuracionEmpresaRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.empty());

        byte[] pdf = reporteVentaService.exportarReporteVentasPeriodoPdf(null);

        assertThat(pdf).isNotNull();
        assertThat(pdf.length).isGreaterThan(0);
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }
}

package pe.edu.upeu.geovisionBackEnd.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.geovisionBackEnd.dto.reporte.ventas.FiltroReporteVentasDTO;
import pe.edu.upeu.geovisionBackEnd.dto.reporte.ventas.ReporteRankingComercialDTO;
import pe.edu.upeu.geovisionBackEnd.dto.reporte.ventas.ReporteVentasPeriodoDTO;
import pe.edu.upeu.geovisionBackEnd.entity.*;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoVentaEnum;
import pe.edu.upeu.geovisionBackEnd.enums.MetodoPagoEnum;
import pe.edu.upeu.geovisionBackEnd.enums.TipoComprobanteEnum;
import pe.edu.upeu.geovisionBackEnd.repository.*;
import pe.edu.upeu.geovisionBackEnd.service.impl.ReporteVentaServiceImpl;

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

    private Persona personaCliente;
    private Persona personaEmpleado;
    private Paciente paciente;
    private Empleado empleado;
    private Venta venta1;
    private Venta venta2;

    @BeforeEach
    void setUp() {
        personaCliente = Persona.builder().idPersona(10L).nombres("Juan").apellidos("Pérez").numeroDocumento("70112233").build();
        paciente = Paciente.builder().idPersona(10L).persona(personaCliente).build();

        personaEmpleado = Persona.builder().idPersona(20L).nombres("Carlos").apellidos("Vendedor").numeroDocumento("40112233").build();
        empleado = Empleado.builder().idPersona(20L).persona(personaEmpleado).build();

        venta1 = Venta.builder()
                .idVenta(1L)
                .fechaRegistro(LocalDateTime.now().minusDays(1))
                .numeroComprobante("B001-00000001")
                .tipoComprobante(TipoComprobanteEnum.BOLETA)
                .estado(EstadoVentaEnum.PAGADA)
                .total(new BigDecimal("300.00"))
                .paciente(paciente)
                .empleado(empleado)
                .build();

        venta2 = Venta.builder()
                .idVenta(2L)
                .fechaRegistro(LocalDateTime.now())
                .numeroComprobante("F001-00000001")
                .tipoComprobante(TipoComprobanteEnum.FACTURA)
                .estado(EstadoVentaEnum.PENDIENTE)
                .total(new BigDecimal("500.00"))
                .paciente(paciente)
                .empleado(empleado)
                .build();
    }

    @Test
    @DisplayName("CUN 8 / CU27: obtenerReporteVentasPeriodo calcula correctamente totales, cobros y saldos")
    void obtenerReporteVentasPeriodo_calculaTotalesCorrectos() {
        when(ventaRepository.findAll()).thenReturn(List.of(venta1, venta2));

        when(pagoRepository.sumMontoPagadoByVentaId(1L)).thenReturn(new BigDecimal("300.00"));
        when(pagoRepository.sumMontoPagadoByVentaId(2L)).thenReturn(new BigDecimal("200.00"));

        Pago pago1 = Pago.builder().idPago(1L).monto(new BigDecimal("300.00")).metodoPago(MetodoPagoEnum.EFECTIVO).build();
        Pago pago2 = Pago.builder().idPago(2L).monto(new BigDecimal("200.00")).metodoPago(MetodoPagoEnum.QR_YAPE_PLIN).build();

        when(pagoRepository.findByVentaIdVenta(1L)).thenReturn(List.of(pago1));
        when(pagoRepository.findByVentaIdVenta(2L)).thenReturn(List.of(pago2));

        when(productoVendidoRepository.findByVentaIdVenta(any())).thenReturn(Collections.emptyList());
        when(servicioVendidoRepository.findByVentaIdVenta(any())).thenReturn(Collections.emptyList());

        FiltroReporteVentasDTO filtro = FiltroReporteVentasDTO.builder()
                .fechaInicio(LocalDate.now().minusDays(7))
                .fechaFin(LocalDate.now())
                .build();

        ReporteVentasPeriodoDTO resultado = reporteVentaService.obtenerReporteVentasPeriodo(filtro);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTotalVentas()).isEqualTo(2);
        assertThat(resultado.getTotalVendido()).isEqualByComparingTo(new BigDecimal("800.00"));
        assertThat(resultado.getTotalCobrado()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(resultado.getTotalSaldoPendiente()).isEqualByComparingTo(new BigDecimal("300.00"));
        assertThat(resultado.getTicketPromedio()).isEqualByComparingTo(new BigDecimal("400.00"));

        assertThat(resultado.getVentas()).hasSize(2);
        assertThat(resultado.getKpis()).hasSize(4);
    }

    @Test
    @DisplayName("obtenerReporteRankingComercial ordena productos y servicios de mayor a menor recaudación")
    void obtenerReporteRankingComercial_ordenaPorRecaudacionDescendente() {
        when(ventaRepository.findAll()).thenReturn(List.of(venta1));

        Producto prodA = Producto.builder().idProducto(101L).codigo("MONT-01").nombre("Montura Premium").precio(new BigDecimal("200.00")).build();
        Producto prodB = Producto.builder().idProducto(102L).codigo("MONT-02").nombre("Montura Basica").precio(new BigDecimal("100.00")).build();

        ProductoVendido pv1 = ProductoVendido.builder().producto(prodA).cantidad(2).subTotal(new BigDecimal("400.00")).build();
        ProductoVendido pv2 = ProductoVendido.builder().producto(prodB).cantidad(1).subTotal(new BigDecimal("100.00")).build();

        when(productoVendidoRepository.findByVentaIdVenta(1L)).thenReturn(List.of(pv1, pv2));
        when(servicioVendidoRepository.findByVentaIdVenta(1L)).thenReturn(Collections.emptyList());

        ReporteRankingComercialDTO ranking = reporteVentaService.obtenerReporteRankingComercial(null);

        assertThat(ranking).isNotNull();
        assertThat(ranking.getItems()).hasSize(2);
        assertThat(ranking.getItems().get(0).getCodigo()).isEqualTo("MONT-01");
        assertThat(ranking.getItems().get(0).getRanking()).isEqualTo(1);
        assertThat(ranking.getItems().get(1).getCodigo()).isEqualTo("MONT-02");
        assertThat(ranking.getItems().get(1).getRanking()).isEqualTo(2);
    }

    @Test
    @DisplayName("exportarReporteVentasPeriodoPdf genera archivo PDF válido")
    void exportarReporteVentasPeriodoPdf_generaPdfValido() {
        when(ventaRepository.findAll()).thenReturn(List.of(venta1));
        when(pagoRepository.sumMontoPagadoByVentaId(1L)).thenReturn(new BigDecimal("300.00"));
        when(pagoRepository.findByVentaIdVenta(1L)).thenReturn(Collections.emptyList());
        when(productoVendidoRepository.findByVentaIdVenta(any())).thenReturn(Collections.emptyList());
        when(servicioVendidoRepository.findByVentaIdVenta(any())).thenReturn(Collections.emptyList());
        when(configuracionEmpresaRepository.findFirstByOrderByIdConfiguracionEmpresaAsc()).thenReturn(Optional.empty());

        byte[] pdf = reporteVentaService.exportarReporteVentasPeriodoPdf(null);

        assertThat(pdf).isNotNull();
        assertThat(pdf.length).isGreaterThan(0);
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }
}

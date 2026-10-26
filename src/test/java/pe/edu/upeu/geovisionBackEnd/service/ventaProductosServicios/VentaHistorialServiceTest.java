package pe.edu.upeu.geovisionBackEnd.service.ventaProductosServicios;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pe.edu.upeu.geovisionBackEnd.dto.generic.PaginaResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.FiltroVentaDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.VentaDetalleResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.VentaResumenDTO;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Examen;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Paciente;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Persona;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Receta;
import pe.edu.upeu.geovisionBackEnd.entity.controlInventario.Item;
import pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo.OrdenTrabajo;
import pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento.EstadoVenta;
import pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento.MetodoPago;
import pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento.Servicio;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.Comprobante;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.ItemVendido;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.Lente;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.Pago;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.ServicioVendido;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.Venta;
import pe.edu.upeu.geovisionBackEnd.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovisionBackEnd.mapper.ventaProductosServicios.VentaHistorialMapper;
import pe.edu.upeu.geovisionBackEnd.repository.ventaProductosServicios.PagoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.ventaProductosServicios.VentaRepository;
import pe.edu.upeu.geovisionBackEnd.service.impl.ventaProductosServicios.VentaHistorialServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - VentaHistorialService (CUS08)")
class VentaHistorialServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private PagoRepository pagoRepository;

    @Spy
    private VentaHistorialMapper ventaHistorialMapper = new VentaHistorialMapper();

    @InjectMocks
    private VentaHistorialServiceImpl ventaHistorialService;

    private Venta ventaEjemplo;
    private Comprobante comprobanteEjemplo;
    private Persona personaEjemplo;
    private Paciente pacienteEjemplo;
    private EstadoVenta estadoRegistrada;

    @BeforeEach
    void setUp() {
        personaEjemplo = new Persona();
        personaEjemplo.setIdPersona(10L);
        personaEjemplo.setNombres("María");
        personaEjemplo.setApellidos("Rodríguez");

        pacienteEjemplo = new Paciente();
        pacienteEjemplo.setIdPersona(10L);
        pacienteEjemplo.setPersona(personaEjemplo);

        estadoRegistrada = new EstadoVenta();
        estadoRegistrada.setIdEstadoVenta(1L);
        estadoRegistrada.setNombre("REGISTRADA");

        ventaEjemplo = new Venta();
        ventaEjemplo.setIdVenta(100L);
        ventaEjemplo.setFechaRegistro(LocalDateTime.of(2024, 10, 15, 10, 30));
        ventaEjemplo.setTotal(new BigDecimal("450.00"));
        ventaEjemplo.setPaciente(pacienteEjemplo);
        ventaEjemplo.setEstadoVenta(estadoRegistrada);

        comprobanteEjemplo = new Comprobante();
        comprobanteEjemplo.setIdComprobante(50L);
        comprobanteEjemplo.setSerie("B001");
        comprobanteEjemplo.setNumero("000482");
        comprobanteEjemplo.setFechaEmision(LocalDate.of(2024, 10, 15));
        comprobanteEjemplo.setTotalEmitido(new BigDecimal("450.00"));
        comprobanteEjemplo.setEstadoCobro("CON_ADELANTO");
        comprobanteEjemplo.setVenta(ventaEjemplo);

        ventaEjemplo.setComprobante(comprobanteEjemplo);
    }

    @Test
    @DisplayName("Debe listar historial de ventas paginado con metadatos limpios en PaginaResponseDTO")
    void debeListarHistorialVentasPaginadoConFiltros() {
        Pageable pageable = PageRequest.of(0, 10);
        FiltroVentaDTO filtro = FiltroVentaDTO.builder()
                .termino("Maria")
                .build();

        Page<Venta> page = new PageImpl<>(List.of(ventaEjemplo), pageable, 1);

        when(ventaRepository.buscarHistorialPaginado(eq("Maria"), isNull(), isNull(), isNull(), eq(pageable)))
                .thenReturn(page);
        when(ventaRepository.findDocumentosByPersonaId(10L)).thenReturn(List.of("74829103"));
        when(pagoRepository.sumMontoPagadoByComprobanteId(50L)).thenReturn(new BigDecimal("200.00"));

        PaginaResponseDTO<VentaResumenDTO> respuesta = ventaHistorialService.listarHistorialVentas(filtro, pageable);

        assertNotNull(respuesta);
        assertEquals(1, respuesta.getTotalElementos());
        assertEquals(1, respuesta.getTotalPaginas());
        assertEquals(0, respuesta.getPaginaActual());
        assertEquals(10, respuesta.getTamanoPagina());
        assertTrue(respuesta.isPrimera());
        assertTrue(respuesta.isUltima());
        assertFalse(respuesta.getContenido().isEmpty());

        VentaResumenDTO item = respuesta.getContenido().get(0);
        assertEquals(100L, item.getIdVenta());
        assertEquals("B001-000482", item.getComprobanteSerieNumero());
        assertEquals("María Rodríguez", item.getClienteNombre());
        assertEquals("74829103", item.getClienteDni());
        assertEquals("MR", item.getClienteIniciales());
        assertEquals(new BigDecimal("450.00"), item.getTotal());
        assertEquals(new BigDecimal("200.00"), item.getTotalPagado());
        assertEquals(new BigDecimal("250.00"), item.getSaldoPendiente());
        assertEquals("Pago Pendiente", item.getEstadoVisual());
    }

    @Test
    @DisplayName("Debe manejar lista vacía en historial retornando PaginaResponseDTO consistente")
    void debeRetornarPaginaVaciaCuandoNoHayResultados() {
        Pageable pageable = PageRequest.of(0, 10);
        FiltroVentaDTO filtro = FiltroVentaDTO.builder().termino("Inexistente").build();
        Page<Venta> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(ventaRepository.buscarHistorialPaginado(eq("Inexistente"), isNull(), isNull(), isNull(), eq(pageable)))
                .thenReturn(emptyPage);

        PaginaResponseDTO<VentaResumenDTO> respuesta = ventaHistorialService.listarHistorialVentas(filtro, pageable);

        assertNotNull(respuesta);
        assertEquals(0, respuesta.getTotalElementos());
        assertEquals(0, respuesta.getTotalPaginas());
        assertTrue(respuesta.getContenido().isEmpty());
    }

    @Test
    @DisplayName("Debe obtener detalle de venta con receta y orden de trabajo para el modal (sin DNI)")
    void debeObtenerDetalleVentaConRecetaYOrdenTallerExitosamente() {
        // Asignar receta
        Receta receta = new Receta();
        receta.setIdReceta(84L);
        receta.setEsExterno(0);
        ventaEjemplo.setReceta(receta);

        // Asignar lente e item vendido
        Lente lente = new Lente();
        lente.setIdLente(200L);

        Item item = new Item();
        ItemVendido itemVendido = new ItemVendido();
        itemVendido.setIdItemVendido(1L);
        itemVendido.setDescripcion("Montura Ray-Ban Aviator Oro");
        itemVendido.setCantidad(1);
        itemVendido.setPrecioUnit(new BigDecimal("350.00"));
        itemVendido.setSubTotal(new BigDecimal("350.00"));
        itemVendido.setItem(item);
        itemVendido.setLente(lente);
        ventaEjemplo.agregarItemVendido(itemVendido);

        // Asignar orden de trabajo
        OrdenTrabajo ordenTrabajo = new OrdenTrabajo();
        ordenTrabajo.setIdOrdenTrabajo(300L);
        ordenTrabajo.setNumeroOrden("#ORD-2024-089");
        ordenTrabajo.setFechaEntregaEstimada(LocalDateTime.of(2024, 10, 20, 18, 0));
        ordenTrabajo.setIdLente(200L);

        // Método de pago
        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setNombre("Tarjeta de Crédito");
        Pago pago = new Pago();
        pago.setIdPago(1L);
        pago.setMonto(new BigDecimal("200.00"));
        pago.setNumeroOperacion("4921");
        pago.setMetodoPago(metodoPago);
        comprobanteEjemplo.setPagos(List.of(pago));

        when(ventaRepository.findById(100L)).thenReturn(Optional.of(ventaEjemplo));
        when(pagoRepository.sumMontoPagadoByComprobanteId(50L)).thenReturn(new BigDecimal("200.00"));
        when(ventaRepository.findOrdenTrabajoByIdLente(200L)).thenReturn(Optional.of(ordenTrabajo));

        VentaDetalleResponseDTO detalle = ventaHistorialService.obtenerDetalleVenta(100L);

        assertNotNull(detalle);
        assertEquals(100L, detalle.getIdVenta());
        assertEquals("B001-000482", detalle.getComprobanteSerieNumero());
        assertEquals("María Rodríguez", detalle.getClienteNombre());
        assertEquals("Venta con receta", detalle.getTipoVenta());
        assertEquals("Interna #RC-084", detalle.getRecetaCodigo());
        assertEquals(84L, detalle.getRecetaId());
        assertTrue(detalle.getTieneOrdenTrabajo());
        assertEquals("#ORD-2024-089", detalle.getNumeroOrdenTrabajo());
        assertEquals(LocalDate.of(2024, 10, 20), detalle.getFechaEntregaEstimada());
        assertTrue(detalle.getMetodoCobro().contains("Tarjeta de Crédito"));
        assertEquals(new BigDecimal("450.00"), detalle.getTotal());
        assertEquals(new BigDecimal("200.00"), detalle.getAdelantoAbonado());
        assertEquals(new BigDecimal("250.00"), detalle.getSaldoPendiente());
        assertEquals(1, detalle.getItems().size());
        assertEquals("Montura Ray-Ban Aviator Oro", detalle.getItems().get(0).getDescripcion());
    }

    @Test
    @DisplayName("Debe obtener detalle de venta directa sin receta médica")
    void debeObtenerDetalleVentaDirectaSinReceta() {
        Item item = new Item();
        ItemVendido itemVendido = new ItemVendido();
        itemVendido.setIdItemVendido(2L);
        itemVendido.setDescripcion("Estuche Rígido para Gafas");
        itemVendido.setCantidad(2);
        itemVendido.setPrecioUnit(new BigDecimal("25.00"));
        itemVendido.setSubTotal(new BigDecimal("50.00"));
        itemVendido.setItem(item);
        ventaEjemplo.setTotal(new BigDecimal("50.00"));
        ventaEjemplo.agregarItemVendido(itemVendido);

        when(ventaRepository.findById(100L)).thenReturn(Optional.of(ventaEjemplo));
        when(pagoRepository.sumMontoPagadoByComprobanteId(50L)).thenReturn(new BigDecimal("50.00"));

        VentaDetalleResponseDTO detalle = ventaHistorialService.obtenerDetalleVenta(100L);

        assertNotNull(detalle);
        assertEquals("Venta directa", detalle.getTipoVenta());
        assertNull(detalle.getRecetaCodigo());
        assertNull(detalle.getRecetaId());
        assertFalse(detalle.getTieneOrdenTrabajo());
        assertNull(detalle.getNumeroOrdenTrabajo());
        assertEquals(new BigDecimal("50.00"), detalle.getTotal());
        assertEquals(new BigDecimal("50.00"), detalle.getAdelantoAbonado());
        assertEquals(0, detalle.getSaldoPendiente().compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Debe obtener detalle de venta de servicio clínico")
    void debeObtenerDetalleVentaDeServicioClinico() {
        Servicio servicio = new Servicio();
        servicio.setIdServicio(5L);
        servicio.setNombre("Medida de la Vista Computarizada");

        ServicioVendido sv = new ServicioVendido();
        sv.setIdServicioVendido(1L);
        sv.setServicio(servicio);
        sv.setCantidad(1);
        sv.setPrecio(new BigDecimal("30.00"));
        sv.setSubTotal(new BigDecimal("30.00"));
        ventaEjemplo.setTotal(new BigDecimal("30.00"));
        ventaEjemplo.agregarServicioVendido(sv);

        when(ventaRepository.findById(100L)).thenReturn(Optional.of(ventaEjemplo));
        when(pagoRepository.sumMontoPagadoByComprobanteId(50L)).thenReturn(new BigDecimal("30.00"));

        VentaDetalleResponseDTO detalle = ventaHistorialService.obtenerDetalleVenta(100L);

        assertNotNull(detalle);
        assertEquals("Venta de servicio", detalle.getTipoVenta());
        assertNull(detalle.getRecetaCodigo());
        assertFalse(detalle.getTieneOrdenTrabajo());
        assertEquals(1, detalle.getItems().size());
        assertEquals("Medida de la Vista Computarizada", detalle.getItems().get(0).getDescripcion());
    }

    @Test
    @DisplayName("Debe lanzar RecursosNoEncontradosException cuando el ID de la venta no existe")
    void debeLanzarExcepcionCuandoVentaNoExiste() {
        when(ventaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RecursosNoEncontradosException.class, () ->
                ventaHistorialService.obtenerDetalleVenta(999L)
        );
    }

    @Test
    @DisplayName("Debe resolver correctamente los estados visuales en el Mapper")
    void debeResolverEstadosVisualesEnMapper() {
        // 1. Cancelado por anulación
        EstadoVenta estadoAnulada = new EstadoVenta();
        estadoAnulada.setNombre("ANULADA");
        ventaEjemplo.setEstadoVenta(estadoAnulada);
        assertEquals("Cancelado", ventaHistorialMapper.determinarEstadoVisual(ventaEjemplo, null));

        // 2. Pago pendiente por cobro CON_ADELANTO o PENDIENTE
        ventaEjemplo.setEstadoVenta(estadoRegistrada);
        comprobanteEjemplo.setEstadoCobro("CON_ADELANTO");
        assertEquals("Pago Pendiente", ventaHistorialMapper.determinarEstadoVisual(ventaEjemplo, null));

        // 3. En laboratorio cuando está cancelado el pago pero orden de trabajo no ha sido entregada
        comprobanteEjemplo.setEstadoCobro("CANCELADO");
        OrdenTrabajo otPendiente = new OrdenTrabajo();
        otPendiente.setFechaEntregaPaciente(null);
        assertEquals("En Laboratorio", ventaHistorialMapper.determinarEstadoVisual(ventaEjemplo, otPendiente));

        // 4. Completado cuando está cancelado y entregado
        otPendiente.setFechaEntregaPaciente(LocalDateTime.now());
        assertEquals("Completado", ventaHistorialMapper.determinarEstadoVisual(ventaEjemplo, otPendiente));
    }

    @Test
    @DisplayName("Debe calcular iniciales correctas para el avatar visual del cliente")
    void debeCalcularInicialesCorrectas() {
        assertEquals("MR", ventaHistorialMapper.obtenerIniciales("María Rodríguez"));
        assertEquals("JP", ventaHistorialMapper.obtenerIniciales("Juan Pérez Silva"));
        assertEquals("CA", ventaHistorialMapper.obtenerIniciales("Carlos"));
        assertEquals("CG", ventaHistorialMapper.obtenerIniciales("Cliente General"));
        assertEquals("CG", ventaHistorialMapper.obtenerIniciales(null));
        assertEquals("CG", ventaHistorialMapper.obtenerIniciales("   "));
    }
}

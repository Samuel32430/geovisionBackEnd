package pe.edu.upeu.geovisionBackEnd.service.ventaProductosServicios;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.PagoPendienteRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.PagoResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.SaldoPendienteDTO;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Empleado;
import pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento.EstadoComprobante;
import pe.edu.upeu.geovisionBackEnd.entity.gestionMantenimiento.MetodoPago;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.Comprobante;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.Pago;
import pe.edu.upeu.geovisionBackEnd.enums.gestionMantenimiento.EstadoComprobanteEnum;
import pe.edu.upeu.geovisionBackEnd.enums.gestionMantenimiento.MetodoPagoEnum;
import pe.edu.upeu.geovisionBackEnd.enums.ventaProductosServicios.EstadoCobroEnum;
import pe.edu.upeu.geovisionBackEnd.enums.ventaProductosServicios.EstadoDocumentoEnum;
import pe.edu.upeu.geovisionBackEnd.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovisionBackEnd.exception.ReglaNegocioException;
import pe.edu.upeu.geovisionBackEnd.mapper.ventaProductosServicios.PagoMapper;
import pe.edu.upeu.geovisionBackEnd.repository.controlAccesos.EmpleadoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.gestionMantenimiento.EstadoComprobanteRepository;
import pe.edu.upeu.geovisionBackEnd.repository.gestionMantenimiento.MetodoPagoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.ventaProductosServicios.ComprobanteRepository;
import pe.edu.upeu.geovisionBackEnd.repository.ventaProductosServicios.PagoRepository;
import pe.edu.upeu.geovisionBackEnd.service.impl.ventaProductosServicios.PagoPendienteServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoPendienteServiceTest {

    @Mock
    private ComprobanteRepository comprobanteRepository;

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private MetodoPagoRepository metodoPagoRepository;

    @Mock
    private EstadoComprobanteRepository estadoComprobanteRepository;

    @Mock
    private PagoMapper pagoMapper;

    @InjectMocks
    private PagoPendienteServiceImpl pagoPendienteService;

    @Test
    @DisplayName("1. Debe listar comprobantes con saldo pendiente exitosamente")
    void debeListarComprobantesConSaldoPendienteExitosamente() {
        Comprobante c1 = new Comprobante();
        c1.setIdComprobante(1L);
        c1.setTotalEmitido(new BigDecimal("150.00"));

        Comprobante c2 = new Comprobante();
        c2.setIdComprobante(2L);
        c2.setTotalEmitido(new BigDecimal("200.00"));

        when(comprobanteRepository.findComprobantesConSaldoPendiente()).thenReturn(List.of(c1, c2));
        when(pagoRepository.sumMontoPagadoByComprobanteId(1L)).thenReturn(new BigDecimal("50.00"));
        when(pagoRepository.sumMontoPagadoByComprobanteId(2L)).thenReturn(null);

        SaldoPendienteDTO dto1 = new SaldoPendienteDTO();
        dto1.setIdComprobante(1L);
        dto1.setSerieNumero("B001-0000001");
        dto1.setClienteNombre("Carlos Mendoza");
        dto1.setSaldoPendiente(new BigDecimal("100.00"));

        SaldoPendienteDTO dto2 = new SaldoPendienteDTO();
        dto2.setIdComprobante(2L);
        dto2.setSerieNumero("B001-0000002");
        dto2.setClienteNombre("Ana Silva");
        dto2.setSaldoPendiente(new BigDecimal("200.00"));

        when(pagoMapper.toSaldoPendienteDTO(c1, new BigDecimal("50.00"), new BigDecimal("100.00"))).thenReturn(dto1);
        when(pagoMapper.toSaldoPendienteDTO(c2, BigDecimal.ZERO, new BigDecimal("200.00"))).thenReturn(dto2);

        List<SaldoPendienteDTO> resultado = pagoPendienteService.listarVentasConSaldoPendiente(null);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("B001-0000001", resultado.get(0).getSerieNumero());
        assertEquals("B001-0000002", resultado.get(1).getSerieNumero());
        verify(comprobanteRepository, times(1)).findComprobantesConSaldoPendiente();
        verify(pagoRepository, times(1)).sumMontoPagadoByComprobanteId(1L);
        verify(pagoRepository, times(1)).sumMontoPagadoByComprobanteId(2L);
        verify(pagoMapper, times(1)).toSaldoPendienteDTO(c1, new BigDecimal("50.00"), new BigDecimal("100.00"));
        verify(pagoMapper, times(1)).toSaldoPendienteDTO(c2, BigDecimal.ZERO, new BigDecimal("200.00"));
    }

    @Test
    @DisplayName("2. Debe obtener saldo pendiente por comprobante exitosamente")
    void debeObtenerSaldoPendientePorComprobanteExitosamente() {
        Long idComprobante = 1L;
        Comprobante comprobante = new Comprobante();
        comprobante.setIdComprobante(idComprobante);
        comprobante.setTotalEmitido(new BigDecimal("300.00"));

        when(comprobanteRepository.findById(idComprobante)).thenReturn(Optional.of(comprobante));
        when(pagoRepository.sumMontoPagadoByComprobanteId(idComprobante)).thenReturn(new BigDecimal("100.00"));

        SaldoPendienteDTO dtoEsperado = new SaldoPendienteDTO();
        dtoEsperado.setIdComprobante(idComprobante);
        dtoEsperado.setTotalVenta(new BigDecimal("300.00"));
        dtoEsperado.setTotalPagado(new BigDecimal("100.00"));
        dtoEsperado.setSaldoPendiente(new BigDecimal("200.00"));

        when(pagoMapper.toSaldoPendienteDTO(comprobante, new BigDecimal("100.00"), new BigDecimal("200.00")))
                .thenReturn(dtoEsperado);

        SaldoPendienteDTO resultado = pagoPendienteService.obtenerSaldoPendientePorComprobante(idComprobante);

        assertNotNull(resultado);
        assertEquals(idComprobante, resultado.getIdComprobante());
        assertEquals(new BigDecimal("300.00"), resultado.getTotalVenta());
        assertEquals(new BigDecimal("100.00"), resultado.getTotalPagado());
        assertEquals(new BigDecimal("200.00"), resultado.getSaldoPendiente());
        verify(comprobanteRepository, times(1)).findById(idComprobante);
        verify(pagoRepository, times(1)).sumMontoPagadoByComprobanteId(idComprobante);
        verify(pagoMapper, times(1)).toSaldoPendienteDTO(comprobante, new BigDecimal("100.00"), new BigDecimal("200.00"));
    }

    @Test
    @DisplayName("3. Debe registrar liquidación total y actualizar comprobante a CANCELADO con fechaCancelacion")
    void debeRegistrarLiquidacionTotalYActualizarACancelado() {
        Long comprobanteId = 1L;
        Long empleadoId = 2L;
        Long metodoPagoId = 3L;
        BigDecimal montoPago = new BigDecimal("80.00");

        PagoPendienteRequestDTO request = new PagoPendienteRequestDTO(
                comprobanteId, metodoPagoId, empleadoId, montoPago, null);

        Comprobante comprobante = new Comprobante();
        comprobante.setIdComprobante(comprobanteId);
        comprobante.setTotalEmitido(new BigDecimal("100.00"));
        comprobante.setEstadoDocumento(EstadoDocumentoEnum.EMITIDO.name());
        comprobante.setEstadoCobro(EstadoCobroEnum.CON_ADELANTO.name());
        comprobante.setFechaCancelacion(null);

        Empleado empleado = new Empleado();
        empleado.setIdPersona(empleadoId);
        empleado.setEstado(1);

        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setIdMetodoPago(metodoPagoId);
        metodoPago.setNombre(MetodoPagoEnum.EFECTIVO.name());

        EstadoComprobante estadoCancelado = new EstadoComprobante();
        estadoCancelado.setIdEstadoComprobante(5L);
        estadoCancelado.setNombre(EstadoComprobanteEnum.CANCELADO.name());

        when(comprobanteRepository.findById(comprobanteId)).thenReturn(Optional.of(comprobante));
        when(empleadoRepository.findByIdPersonaAndEstado(empleadoId, 1)).thenReturn(Optional.of(empleado));
        when(metodoPagoRepository.findById(metodoPagoId)).thenReturn(Optional.of(metodoPago));
        when(pagoRepository.sumMontoPagadoByComprobanteId(comprobanteId)).thenReturn(new BigDecimal("20.00"));
        when(estadoComprobanteRepository.findByNombreIgnoreCase(EstadoComprobanteEnum.CANCELADO.name()))
                .thenReturn(Optional.of(estadoCancelado));

        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> {
            Pago p = invocation.getArgument(0);
            p.setIdPago(99L);
            return p;
        });

        PagoResponseDTO responseEsperado = new PagoResponseDTO();
        responseEsperado.setIdPago(99L);
        responseEsperado.setMonto(montoPago);
        responseEsperado.setSaldoRestante(BigDecimal.ZERO);
        responseEsperado.setNuevoEstadoCobro(EstadoCobroEnum.CANCELADO.name());

        when(pagoMapper.toPagoResponseDTO(any(Pago.class), argThat(b -> b != null && b.compareTo(BigDecimal.ZERO) == 0), eq(EstadoCobroEnum.CANCELADO.name())))
                .thenReturn(responseEsperado);

        PagoResponseDTO resultado = pagoPendienteService.registrarPagoPendiente(request);

        assertNotNull(resultado);
        assertEquals(99L, resultado.getIdPago());
        assertEquals(EstadoCobroEnum.CANCELADO.name(), resultado.getNuevoEstadoCobro());
        assertEquals(BigDecimal.ZERO, resultado.getSaldoRestante());

        // Verificaciones del cambio de estado y fecha de cancelación en el comprobante
        assertEquals(EstadoCobroEnum.CANCELADO.name(), comprobante.getEstadoCobro());
        assertNotNull(comprobante.getFechaCancelacion());
        assertEquals(LocalDate.now(), comprobante.getFechaCancelacion());
        assertEquals(estadoCancelado, comprobante.getEstadoComprobante());

        // Verificación de Pago persistido con ArgumentCaptor
        ArgumentCaptor<Pago> pagoCaptor = ArgumentCaptor.forClass(Pago.class);
        verify(pagoRepository, times(1)).save(pagoCaptor.capture());
        Pago pagoGuardado = pagoCaptor.getValue();
        assertEquals(montoPago, pagoGuardado.getMonto());
        assertEquals(comprobante, pagoGuardado.getComprobante());
        assertEquals(empleado, pagoGuardado.getEmpleado());
        assertEquals(metodoPago, pagoGuardado.getMetodoPago());
        assertEquals(0, pagoGuardado.getAnulado());
        assertNotNull(pagoGuardado.getFechaRegistro());

        verify(comprobanteRepository, times(1)).save(comprobante);
        verify(pagoMapper, times(1)).toPagoResponseDTO(any(Pago.class), argThat(b -> b != null && b.compareTo(BigDecimal.ZERO) == 0), eq(EstadoCobroEnum.CANCELADO.name()));
    }

    @Test
    @DisplayName("4. Debe registrar abono parcial y actualizar comprobante a CON_ADELANTO con fechaCancelacion null")
    void debeRegistrarAbonoParcialYActualizarAConAdelanto() {
        Long comprobanteId = 1L;
        Long empleadoId = 2L;
        Long metodoPagoId = 3L;
        BigDecimal montoPago = new BigDecimal("40.00");

        PagoPendienteRequestDTO request = new PagoPendienteRequestDTO(
                comprobanteId, metodoPagoId, empleadoId, montoPago, null);

        Comprobante comprobante = new Comprobante();
        comprobante.setIdComprobante(comprobanteId);
        comprobante.setTotalEmitido(new BigDecimal("100.00"));
        comprobante.setEstadoDocumento(EstadoDocumentoEnum.EMITIDO.name());
        comprobante.setEstadoCobro(EstadoCobroEnum.PENDIENTE.name());
        comprobante.setFechaCancelacion(LocalDate.now());

        Empleado empleado = new Empleado();
        empleado.setIdPersona(empleadoId);
        empleado.setEstado(1);

        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setIdMetodoPago(metodoPagoId);
        metodoPago.setNombre(MetodoPagoEnum.EFECTIVO.name());

        EstadoComprobante estadoConAdelanto = new EstadoComprobante();
        estadoConAdelanto.setIdEstadoComprobante(4L);
        estadoConAdelanto.setNombre(EstadoComprobanteEnum.CON_ADELANTO.name());

        when(comprobanteRepository.findById(comprobanteId)).thenReturn(Optional.of(comprobante));
        when(empleadoRepository.findByIdPersonaAndEstado(empleadoId, 1)).thenReturn(Optional.of(empleado));
        when(metodoPagoRepository.findById(metodoPagoId)).thenReturn(Optional.of(metodoPago));
        when(pagoRepository.sumMontoPagadoByComprobanteId(comprobanteId)).thenReturn(BigDecimal.ZERO);
        when(estadoComprobanteRepository.findByNombreIgnoreCase(EstadoComprobanteEnum.CON_ADELANTO.name()))
                .thenReturn(Optional.of(estadoConAdelanto));

        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> {
            Pago p = invocation.getArgument(0);
            p.setIdPago(100L);
            return p;
        });

        BigDecimal saldoRestanteEsperado = new BigDecimal("60.00");
        PagoResponseDTO responseEsperado = new PagoResponseDTO();
        responseEsperado.setIdPago(100L);
        responseEsperado.setMonto(montoPago);
        responseEsperado.setSaldoRestante(saldoRestanteEsperado);
        responseEsperado.setNuevoEstadoCobro(EstadoCobroEnum.CON_ADELANTO.name());

        when(pagoMapper.toPagoResponseDTO(any(Pago.class), eq(saldoRestanteEsperado), eq(EstadoCobroEnum.CON_ADELANTO.name())))
                .thenReturn(responseEsperado);

        PagoResponseDTO resultado = pagoPendienteService.registrarPagoPendiente(request);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getIdPago());
        assertEquals(EstadoCobroEnum.CON_ADELANTO.name(), resultado.getNuevoEstadoCobro());
        assertEquals(saldoRestanteEsperado, resultado.getSaldoRestante());

        // Comprobante debe estar en CON_ADELANTO y fechaCancelacion en null
        assertEquals(EstadoCobroEnum.CON_ADELANTO.name(), comprobante.getEstadoCobro());
        assertNull(comprobante.getFechaCancelacion());
        assertEquals(estadoConAdelanto, comprobante.getEstadoComprobante());

        verify(pagoRepository, times(1)).save(any(Pago.class));
        verify(comprobanteRepository, times(1)).save(comprobante);
        verify(pagoMapper, times(1)).toPagoResponseDTO(any(Pago.class), eq(saldoRestanteEsperado), eq(EstadoCobroEnum.CON_ADELANTO.name()));
    }

    @Test
    @DisplayName("5. Debe lanzar ReglaNegocioException cuando el monto supera el saldo pendiente (RN-VE13)")
    void debeLanzarExcepcionCuandoMontoSuperaSaldoPendiente() {
        Long comprobanteId = 1L;
        Long empleadoId = 2L;
        Long metodoPagoId = 3L;
        BigDecimal montoPago = new BigDecimal("80.00");

        PagoPendienteRequestDTO request = new PagoPendienteRequestDTO(
                comprobanteId, metodoPagoId, empleadoId, montoPago, null);

        Comprobante comprobante = new Comprobante();
        comprobante.setIdComprobante(comprobanteId);
        comprobante.setTotalEmitido(new BigDecimal("100.00"));
        comprobante.setEstadoDocumento(EstadoDocumentoEnum.EMITIDO.name());
        comprobante.setEstadoCobro(EstadoCobroEnum.CON_ADELANTO.name());

        Empleado empleado = new Empleado();
        empleado.setIdPersona(empleadoId);
        empleado.setEstado(1);

        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setIdMetodoPago(metodoPagoId);
        metodoPago.setNombre(MetodoPagoEnum.EFECTIVO.name());

        when(comprobanteRepository.findById(comprobanteId)).thenReturn(Optional.of(comprobante));
        when(empleadoRepository.findByIdPersonaAndEstado(empleadoId, 1)).thenReturn(Optional.of(empleado));
        when(metodoPagoRepository.findById(metodoPagoId)).thenReturn(Optional.of(metodoPago));
        when(pagoRepository.sumMontoPagadoByComprobanteId(comprobanteId)).thenReturn(new BigDecimal("50.00")); // Saldo pendiente = 50.00

        ReglaNegocioException excepcion = assertThrows(
                ReglaNegocioException.class,
                () -> pagoPendienteService.registrarPagoPendiente(request)
        );

        assertTrue(excepcion.getMessage().contains("excede el saldo pendiente"));
        verify(pagoRepository, never()).save(any(Pago.class));
        verify(comprobanteRepository, never()).save(any(Comprobante.class));
    }

    @Test
    @DisplayName("6. Debe lanzar ReglaNegocioException cuando el monto es cero o negativo (RN-VE13)")
    void debeLanzarExcepcionCuandoMontoEsCeroONegativo() {
        Long comprobanteId = 1L;
        Long empleadoId = 2L;
        Long metodoPagoId = 3L;

        Comprobante comprobante = new Comprobante();
        comprobante.setIdComprobante(comprobanteId);
        comprobante.setTotalEmitido(new BigDecimal("100.00"));
        comprobante.setEstadoDocumento(EstadoDocumentoEnum.EMITIDO.name());
        comprobante.setEstadoCobro(EstadoCobroEnum.PENDIENTE.name());

        Empleado empleado = new Empleado();
        empleado.setIdPersona(empleadoId);
        empleado.setEstado(1);

        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setIdMetodoPago(metodoPagoId);
        metodoPago.setNombre(MetodoPagoEnum.EFECTIVO.name());

        when(comprobanteRepository.findById(comprobanteId)).thenReturn(Optional.of(comprobante));
        when(empleadoRepository.findByIdPersonaAndEstado(empleadoId, 1)).thenReturn(Optional.of(empleado));
        when(metodoPagoRepository.findById(metodoPagoId)).thenReturn(Optional.of(metodoPago));
        when(pagoRepository.sumMontoPagadoByComprobanteId(comprobanteId)).thenReturn(BigDecimal.ZERO);

        // Caso 1: Monto igual a cero
        PagoPendienteRequestDTO requestCero = new PagoPendienteRequestDTO(
                comprobanteId, metodoPagoId, empleadoId, BigDecimal.ZERO, null);
        ReglaNegocioException exCero = assertThrows(
                ReglaNegocioException.class,
                () -> pagoPendienteService.registrarPagoPendiente(requestCero)
        );
        assertEquals("El monto a pagar debe ser mayor a cero", exCero.getMessage());

        // Caso 2: Monto negativo
        PagoPendienteRequestDTO requestNegativo = new PagoPendienteRequestDTO(
                comprobanteId, metodoPagoId, empleadoId, new BigDecimal("-15.00"), null);
        ReglaNegocioException exNegativo = assertThrows(
                ReglaNegocioException.class,
                () -> pagoPendienteService.registrarPagoPendiente(requestNegativo)
        );
        assertEquals("El monto a pagar debe ser mayor a cero", exNegativo.getMessage());

        // Caso 3: Monto nulo
        PagoPendienteRequestDTO requestNull = new PagoPendienteRequestDTO(
                comprobanteId, metodoPagoId, empleadoId, null, null);
        ReglaNegocioException exNull = assertThrows(
                ReglaNegocioException.class,
                () -> pagoPendienteService.registrarPagoPendiente(requestNull)
        );
        assertEquals("El monto a pagar debe ser mayor a cero", exNull.getMessage());

        verify(pagoRepository, never()).save(any(Pago.class));
        verify(comprobanteRepository, never()).save(any(Comprobante.class));
    }

    @Test
    @DisplayName("7. Debe lanzar ReglaNegocioException cuando el comprobante ya está cancelado (RN-VE10)")
    void debeLanzarExcepcionCuandoComprobanteYaEstaCancelado() {
        Long comprobanteId = 1L;
        PagoPendienteRequestDTO request = new PagoPendienteRequestDTO(
                comprobanteId, 3L, 2L, new BigDecimal("50.00"), null);

        Comprobante comprobante = new Comprobante();
        comprobante.setIdComprobante(comprobanteId);
        comprobante.setEstadoDocumento(EstadoDocumentoEnum.EMITIDO.name());
        comprobante.setEstadoCobro(EstadoCobroEnum.CANCELADO.name());

        when(comprobanteRepository.findById(comprobanteId)).thenReturn(Optional.of(comprobante));

        ReglaNegocioException excepcion = assertThrows(
                ReglaNegocioException.class,
                () -> pagoPendienteService.registrarPagoPendiente(request)
        );

        assertEquals("La orden ya se encuentra 100% cancelada y no admite nuevos pagos", excepcion.getMessage());
        verify(empleadoRepository, never()).findByIdPersonaAndEstado(any(), any());
        verify(metodoPagoRepository, never()).findById(any());
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    @DisplayName("8. Debe lanzar ReglaNegocioException cuando el comprobante está anulado (RN-VE10)")
    void debeLanzarExcepcionCuandoComprobanteEstaAnulado() {
        Long comprobanteId = 1L;
        PagoPendienteRequestDTO request = new PagoPendienteRequestDTO(
                comprobanteId, 3L, 2L, new BigDecimal("50.00"), null);

        Comprobante comprobante = new Comprobante();
        comprobante.setIdComprobante(comprobanteId);
        comprobante.setEstadoDocumento(EstadoDocumentoEnum.ANULADO.name());

        when(comprobanteRepository.findById(comprobanteId)).thenReturn(Optional.of(comprobante));

        ReglaNegocioException excepcion = assertThrows(
                ReglaNegocioException.class,
                () -> pagoPendienteService.registrarPagoPendiente(request)
        );

        assertEquals("No se pueden registrar pagos en un comprobante anulado", excepcion.getMessage());
        verify(empleadoRepository, never()).findByIdPersonaAndEstado(any(), any());
        verify(metodoPagoRepository, never()).findById(any());
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    @DisplayName("9. Debe lanzar ReglaNegocioException cuando falta el número de voucher en pago con tarjeta (RN-VE12)")
    void debeLanzarExcepcionCuandoFaltaVoucherEnPagoConTarjeta() {
        Long comprobanteId = 1L;
        Long empleadoId = 2L;
        Long metodoPagoId = 3L;

        Comprobante comprobante = new Comprobante();
        comprobante.setIdComprobante(comprobanteId);
        comprobante.setTotalEmitido(new BigDecimal("100.00"));
        comprobante.setEstadoDocumento(EstadoDocumentoEnum.EMITIDO.name());
        comprobante.setEstadoCobro(EstadoCobroEnum.PENDIENTE.name());

        Empleado empleado = new Empleado();
        empleado.setIdPersona(empleadoId);
        empleado.setEstado(1);

        MetodoPago metodoPagoTarjeta = new MetodoPago();
        metodoPagoTarjeta.setIdMetodoPago(metodoPagoId);
        metodoPagoTarjeta.setNombre(MetodoPagoEnum.TARJETA.name());

        when(comprobanteRepository.findById(comprobanteId)).thenReturn(Optional.of(comprobante));
        when(empleadoRepository.findByIdPersonaAndEstado(empleadoId, 1)).thenReturn(Optional.of(empleado));
        when(metodoPagoRepository.findById(metodoPagoId)).thenReturn(Optional.of(metodoPagoTarjeta));

        // Subcaso 1: Voucher nulo
        PagoPendienteRequestDTO requestVoucherNull = new PagoPendienteRequestDTO(
                comprobanteId, metodoPagoId, empleadoId, new BigDecimal("50.00"), null);
        ReglaNegocioException exNull = assertThrows(
                ReglaNegocioException.class,
                () -> pagoPendienteService.registrarPagoPendiente(requestVoucherNull)
        );
        assertEquals("Es obligatorio ingresar el número de voucher/operación para pagos con tarjeta/POS", exNull.getMessage());

        // Subcaso 2: Voucher vacío o en blanco
        PagoPendienteRequestDTO requestVoucherVacio = new PagoPendienteRequestDTO(
                comprobanteId, metodoPagoId, empleadoId, new BigDecimal("50.00"), "   ");
        ReglaNegocioException exVacio = assertThrows(
                ReglaNegocioException.class,
                () -> pagoPendienteService.registrarPagoPendiente(requestVoucherVacio)
        );
        assertEquals("Es obligatorio ingresar el número de voucher/operación para pagos con tarjeta/POS", exVacio.getMessage());

        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    @DisplayName("10. Debe lanzar RecursosNoEncontradosException cuando el comprobante no existe")
    void debeLanzarExcepcionCuandoComprobanteNoExiste() {
        Long comprobanteId = 999L;
        PagoPendienteRequestDTO request = new PagoPendienteRequestDTO(
                comprobanteId, 1L, 1L, new BigDecimal("50.00"), null);

        when(comprobanteRepository.findById(comprobanteId)).thenReturn(Optional.empty());

        RecursosNoEncontradosException excepcion = assertThrows(
                RecursosNoEncontradosException.class,
                () -> pagoPendienteService.registrarPagoPendiente(request)
        );

        assertTrue(excepcion.getMessage().contains("Comprobante no encontrado con ID: 999"));
        verify(empleadoRepository, never()).findByIdPersonaAndEstado(any(), any());
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    @DisplayName("11. Debe lanzar RecursosNoEncontradosException cuando el empleado no existe o está inactivo")
    void debeLanzarExcepcionCuandoEmpleadoNoExisteOInactivo() {
        Long comprobanteId = 1L;
        Long empleadoId = 999L;
        PagoPendienteRequestDTO request = new PagoPendienteRequestDTO(
                comprobanteId, 1L, empleadoId, new BigDecimal("50.00"), null);

        Comprobante comprobante = new Comprobante();
        comprobante.setIdComprobante(comprobanteId);
        comprobante.setEstadoDocumento(EstadoDocumentoEnum.EMITIDO.name());
        comprobante.setEstadoCobro(EstadoCobroEnum.PENDIENTE.name());

        when(comprobanteRepository.findById(comprobanteId)).thenReturn(Optional.of(comprobante));
        when(empleadoRepository.findByIdPersonaAndEstado(empleadoId, 1)).thenReturn(Optional.empty());

        RecursosNoEncontradosException excepcion = assertThrows(
                RecursosNoEncontradosException.class,
                () -> pagoPendienteService.registrarPagoPendiente(request)
        );

        assertEquals("Empleado responsable no encontrado o inactivo", excepcion.getMessage());
        verify(metodoPagoRepository, never()).findById(any());
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    @DisplayName("12. Debe lanzar RecursosNoEncontradosException cuando el método de pago no existe")
    void debeLanzarExcepcionCuandoMetodoPagoNoExiste() {
        Long comprobanteId = 1L;
        Long empleadoId = 2L;
        Long metodoPagoId = 999L;
        PagoPendienteRequestDTO request = new PagoPendienteRequestDTO(
                comprobanteId, metodoPagoId, empleadoId, new BigDecimal("50.00"), null);

        Comprobante comprobante = new Comprobante();
        comprobante.setIdComprobante(comprobanteId);
        comprobante.setEstadoDocumento(EstadoDocumentoEnum.EMITIDO.name());
        comprobante.setEstadoCobro(EstadoCobroEnum.PENDIENTE.name());

        Empleado empleado = new Empleado();
        empleado.setIdPersona(empleadoId);
        empleado.setEstado(1);

        when(comprobanteRepository.findById(comprobanteId)).thenReturn(Optional.of(comprobante));
        when(empleadoRepository.findByIdPersonaAndEstado(empleadoId, 1)).thenReturn(Optional.of(empleado));
        when(metodoPagoRepository.findById(metodoPagoId)).thenReturn(Optional.empty());

        RecursosNoEncontradosException excepcion = assertThrows(
                RecursosNoEncontradosException.class,
                () -> pagoPendienteService.registrarPagoPendiente(request)
        );

        assertEquals("Método de pago no encontrado", excepcion.getMessage());
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    @DisplayName("13. Debe lanzar RecursosNoEncontradosException cuando el comprobante no existe al consultar saldo")
    void debeLanzarExcepcionCuandoComprobanteNoExisteAlObtenerSaldoPendiente() {
        Long idComprobante = 999L;
        when(comprobanteRepository.findById(idComprobante)).thenReturn(Optional.empty());

        RecursosNoEncontradosException ex = assertThrows(
                RecursosNoEncontradosException.class,
                () -> pagoPendienteService.obtenerSaldoPendientePorComprobante(idComprobante)
        );

        assertTrue(ex.getMessage().contains("Comprobante no encontrado con ID: 999"));
        verify(pagoRepository, never()).sumMontoPagadoByComprobanteId(any());
    }

    @Test
    @DisplayName("14. Debe filtrar comprobantes correctamente por término de búsqueda (serie o cliente)")
    void debeFiltrarComprobantesPorTerminoSerieOCliente() {
        Comprobante c1 = new Comprobante();
        c1.setIdComprobante(1L);
        c1.setTotalEmitido(new BigDecimal("100.00"));

        Comprobante c2 = new Comprobante();
        c2.setIdComprobante(2L);
        c2.setTotalEmitido(new BigDecimal("200.00"));

        when(comprobanteRepository.findComprobantesConSaldoPendiente()).thenReturn(List.of(c1, c2));
        when(pagoRepository.sumMontoPagadoByComprobanteId(1L)).thenReturn(BigDecimal.ZERO);
        when(pagoRepository.sumMontoPagadoByComprobanteId(2L)).thenReturn(BigDecimal.ZERO);

        SaldoPendienteDTO dto1 = new SaldoPendienteDTO();
        dto1.setIdComprobante(1L);
        dto1.setSerieNumero("B001-0000001");
        dto1.setClienteNombre("Carlos Mendoza");

        SaldoPendienteDTO dto2 = new SaldoPendienteDTO();
        dto2.setIdComprobante(2L);
        dto2.setSerieNumero("F001-0000002");
        dto2.setClienteNombre("Ana Silva");

        when(pagoMapper.toSaldoPendienteDTO(c1, BigDecimal.ZERO, new BigDecimal("100.00"))).thenReturn(dto1);
        when(pagoMapper.toSaldoPendienteDTO(c2, BigDecimal.ZERO, new BigDecimal("200.00"))).thenReturn(dto2);

        // Filtro por nombre de cliente
        List<SaldoPendienteDTO> filtroCliente = pagoPendienteService.listarVentasConSaldoPendiente("carlos");
        assertEquals(1, filtroCliente.size());
        assertEquals("Carlos Mendoza", filtroCliente.get(0).getClienteNombre());

        // Filtro por serie
        List<SaldoPendienteDTO> filtroSerie = pagoPendienteService.listarVentasConSaldoPendiente("F001");
        assertEquals(1, filtroSerie.size());
        assertEquals("F001-0000002", filtroSerie.get(0).getSerieNumero());

        // Filtro sin coincidencia
        List<SaldoPendienteDTO> filtroSinCoincidencia = pagoPendienteService.listarVentasConSaldoPendiente("NoExiste");
        assertTrue(filtroSinCoincidencia.isEmpty());
    }

    @Test
    @DisplayName("15. Debe registrar pago con tarjeta exitosamente cuando tiene voucher válido")
    void debeRegistrarPagoConTarjetaCuandoTieneVoucherValido() {
        Long comprobanteId = 1L;
        Long empleadoId = 2L;
        Long metodoPagoId = 3L;
        BigDecimal montoPago = new BigDecimal("50.00");

        PagoPendienteRequestDTO request = new PagoPendienteRequestDTO(
                comprobanteId, metodoPagoId, empleadoId, montoPago, "  OP-987654  ");

        Comprobante comprobante = new Comprobante();
        comprobante.setIdComprobante(comprobanteId);
        comprobante.setTotalEmitido(new BigDecimal("100.00"));
        comprobante.setEstadoDocumento(EstadoDocumentoEnum.EMITIDO.name());
        comprobante.setEstadoCobro(EstadoCobroEnum.PENDIENTE.name());

        Empleado empleado = new Empleado();
        empleado.setIdPersona(empleadoId);
        empleado.setEstado(1);

        MetodoPago metodoPagoTarjeta = new MetodoPago();
        metodoPagoTarjeta.setIdMetodoPago(metodoPagoId);
        metodoPagoTarjeta.setNombre("TARJETA VISA");

        EstadoComprobante estadoConAdelanto = new EstadoComprobante();
        estadoConAdelanto.setIdEstadoComprobante(2L);
        estadoConAdelanto.setNombre(EstadoComprobanteEnum.CON_ADELANTO.name());

        when(comprobanteRepository.findById(comprobanteId)).thenReturn(Optional.of(comprobante));
        when(empleadoRepository.findByIdPersonaAndEstado(empleadoId, 1)).thenReturn(Optional.of(empleado));
        when(metodoPagoRepository.findById(metodoPagoId)).thenReturn(Optional.of(metodoPagoTarjeta));
        when(pagoRepository.sumMontoPagadoByComprobanteId(comprobanteId)).thenReturn(BigDecimal.ZERO);
        when(estadoComprobanteRepository.findByNombreIgnoreCase(EstadoComprobanteEnum.CON_ADELANTO.name()))
                .thenReturn(Optional.of(estadoConAdelanto));

        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> {
            Pago p = inv.getArgument(0);
            p.setIdPago(105L);
            return p;
        });

        PagoResponseDTO responseDto = new PagoResponseDTO();
        responseDto.setIdPago(105L);
        responseDto.setNumeroOperacion("OP-987654");
        when(pagoMapper.toPagoResponseDTO(any(Pago.class), any(BigDecimal.class), anyString()))
                .thenReturn(responseDto);

        PagoResponseDTO res = pagoPendienteService.registrarPagoPendiente(request);

        assertNotNull(res);
        assertEquals(105L, res.getIdPago());

        ArgumentCaptor<Pago> captor = ArgumentCaptor.forClass(Pago.class);
        verify(pagoRepository).save(captor.capture());
        assertEquals("OP-987654", captor.getValue().getNumeroOperacion());
    }
}

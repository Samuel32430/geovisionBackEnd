package pe.edu.upeu.geovisionBackEnd.service.impl.ventaProductosServicios;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.geovisionBackEnd.dto.generic.PaginaResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.FiltroVentaDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.VentaDetalleResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.VentaResumenDTO;
import pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo.OrdenTrabajo;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.ItemVendido;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.Venta;
import pe.edu.upeu.geovisionBackEnd.exception.RecursosNoEncontradosException;
import pe.edu.upeu.geovisionBackEnd.mapper.ventaProductosServicios.VentaHistorialMapper;
import pe.edu.upeu.geovisionBackEnd.repository.ventaProductosServicios.PagoRepository;
import pe.edu.upeu.geovisionBackEnd.repository.ventaProductosServicios.VentaRepository;
import pe.edu.upeu.geovisionBackEnd.service.service.ventaProductosServicios.VentaHistorialService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class VentaHistorialServiceImpl implements VentaHistorialService {

    private final VentaRepository ventaRepository;
    private final PagoRepository pagoRepository;
    private final VentaHistorialMapper ventaHistorialMapper;

    @Override
    @Transactional(readOnly = true)
    public PaginaResponseDTO<VentaResumenDTO> listarHistorialVentas(FiltroVentaDTO filtro, Pageable pageable) {
        String termino = (filtro != null && filtro.getTermino() != null && !filtro.getTermino().isBlank())
                ? filtro.getTermino().trim()
                : null;
        Long idEstado = filtro != null ? filtro.getIdEstado() : null;
        LocalDateTime fechaInicio = filtro != null ? filtro.getFechaInicio() : null;
        LocalDateTime fechaFin = filtro != null ? filtro.getFechaFin() : null;

        log.info("Buscando historial de ventas. Término: '{}', idEstado: {}, rango: [{} - {}]",
                termino, idEstado, fechaInicio, fechaFin);

        Page<Venta> ventasPage = ventaRepository.buscarHistorialPaginado(termino, idEstado, fechaInicio, fechaFin, pageable);

        Page<VentaResumenDTO> resumenPage = ventasPage.map(venta -> {
            // Resolver DNI
            String dni = resolverDniCliente(venta);

            // Resolver total pagado y saldo
            BigDecimal totalPagado = BigDecimal.ZERO;
            if (venta.getComprobante() != null && venta.getComprobante().getIdComprobante() != null) {
                totalPagado = pagoRepository.sumMontoPagadoByComprobanteId(venta.getComprobante().getIdComprobante());
                if (totalPagado == null) {
                    totalPagado = BigDecimal.ZERO;
                }
            }
            BigDecimal totalVenta = venta.getTotal() != null ? venta.getTotal() : BigDecimal.ZERO;
            BigDecimal saldoPendiente = totalVenta.subtract(totalPagado);
            if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
                saldoPendiente = BigDecimal.ZERO;
            }

            // Resolver taller / estado visual
            OrdenTrabajo ot = buscarOrdenTrabajoDeVenta(venta);
            String estadoVisual = ventaHistorialMapper.determinarEstadoVisual(venta, ot);

            return ventaHistorialMapper.toVentaResumenDTO(venta, dni, totalPagado, saldoPendiente, estadoVisual);
        });

        return PaginaResponseDTO.de(resumenPage);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaDetalleResponseDTO obtenerDetalleVenta(Long idVenta) {
        log.info("Obteniendo detalle de venta para modal. ID: {}", idVenta);
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RecursosNoEncontradosException("Venta no encontrada con ID: " + idVenta));

        BigDecimal totalPagado = BigDecimal.ZERO;
        if (venta.getComprobante() != null && venta.getComprobante().getIdComprobante() != null) {
            totalPagado = pagoRepository.sumMontoPagadoByComprobanteId(venta.getComprobante().getIdComprobante());
            if (totalPagado == null) {
                totalPagado = BigDecimal.ZERO;
            }
        }

        BigDecimal totalVenta = venta.getTotal() != null ? venta.getTotal() : BigDecimal.ZERO;
        BigDecimal saldoPendiente = totalVenta.subtract(totalPagado);
        if (saldoPendiente.compareTo(BigDecimal.ZERO) < 0) {
            saldoPendiente = BigDecimal.ZERO;
        }

        OrdenTrabajo ot = buscarOrdenTrabajoDeVenta(venta);
        String metodoCobro = ventaHistorialMapper.resolverMetodoCobro(venta.getComprobante());
        String estadoVisual = ventaHistorialMapper.determinarEstadoVisual(venta, ot);

        return ventaHistorialMapper.toVentaDetalleResponseDTO(venta, totalPagado, saldoPendiente, ot, metodoCobro, estadoVisual);
    }

    private String resolverDniCliente(Venta venta) {
        Long idPersona = null;
        if (venta.getComprador() != null && venta.getComprador().getIdPersona() != null) {
            idPersona = venta.getComprador().getIdPersona();
        } else if (venta.getPaciente() != null && venta.getPaciente().getPersona() != null
                && venta.getPaciente().getPersona().getIdPersona() != null) {
            idPersona = venta.getPaciente().getPersona().getIdPersona();
        }

        if (idPersona != null) {
            List<String> documentos = ventaRepository.findDocumentosByPersonaId(idPersona);
            if (documentos != null && !documentos.isEmpty()) {
                return documentos.get(0);
            }
        }
        return "-";
    }

    private OrdenTrabajo buscarOrdenTrabajoDeVenta(Venta venta) {
        if (venta.getItemsVendidos() == null || venta.getItemsVendidos().isEmpty()) {
            return null;
        }

        return venta.getItemsVendidos().stream()
                .map(ItemVendido::getLente)
                .filter(Objects::nonNull)
                .map(lente -> lente.getIdLente() != null ? ventaRepository.findOrdenTrabajoByIdLente(lente.getIdLente()).orElse(null) : null)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}

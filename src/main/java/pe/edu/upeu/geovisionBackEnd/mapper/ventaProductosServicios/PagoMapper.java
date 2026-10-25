package pe.edu.upeu.geovisionBackEnd.mapper.ventaProductosServicios;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.PagoResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.SaldoPendienteDTO;
import pe.edu.upeu.geovisionBackEnd.entity.controlAccesos.Empleado;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.Comprobante;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.Pago;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.Venta;

import java.math.BigDecimal;

@Component
public class PagoMapper {

    public SaldoPendienteDTO toSaldoPendienteDTO(Comprobante comprobante, BigDecimal totalPagado, BigDecimal saldoPendiente) {
        if (comprobante == null) {
            return null;
        }

        SaldoPendienteDTO dto = new SaldoPendienteDTO();
        dto.setIdVenta(comprobante.getVenta() != null ? comprobante.getVenta().getIdVenta() : null);
        dto.setIdComprobante(comprobante.getIdComprobante());

        String serie = comprobante.getSerie() != null ? comprobante.getSerie() : "";
        String numero = comprobante.getNumero() != null ? comprobante.getNumero() : "";
        String serieNumero = serie.isEmpty() && numero.isEmpty() ? "" : (serie + "-" + numero);
        dto.setSerieNumero(serieNumero);

        dto.setClienteNombre(obtenerNombreCliente(comprobante));
        dto.setFechaEmision(comprobante.getFechaEmision());
        dto.setTotalVenta(comprobante.getTotalEmitido() != null ? comprobante.getTotalEmitido() : BigDecimal.ZERO);
        dto.setTotalPagado(totalPagado != null ? totalPagado : BigDecimal.ZERO);
        dto.setSaldoPendiente(saldoPendiente != null ? saldoPendiente : BigDecimal.ZERO);
        dto.setEstadoCobro(comprobante.getEstadoCobro());

        return dto;
    }

    public PagoResponseDTO toPagoResponseDTO(Pago pago, BigDecimal saldoRestante, String nuevoEstadoCobro) {
        if (pago == null) {
            return null;
        }

        PagoResponseDTO dto = new PagoResponseDTO();
        dto.setIdPago(pago.getIdPago());

        if (pago.getComprobante() != null) {
            Comprobante comprobante = pago.getComprobante();
            dto.setIdComprobante(comprobante.getIdComprobante());
            String serie = comprobante.getSerie() != null ? comprobante.getSerie() : "";
            String numero = comprobante.getNumero() != null ? comprobante.getNumero() : "";
            String serieNumero = serie.isEmpty() && numero.isEmpty() ? "" : (serie + "-" + numero);
            dto.setSerieNumeroComprobante(serieNumero);
        }

        if (pago.getMetodoPago() != null) {
            dto.setIdMetodoPago(pago.getMetodoPago().getIdMetodoPago());
            dto.setNombreMetodoPago(pago.getMetodoPago().getNombre());
        }

        if (pago.getEmpleado() != null) {
            dto.setIdEmpleado(pago.getEmpleado().getIdPersona());
            dto.setNombreEmpleado(obtenerNombreEmpleado(pago.getEmpleado()));
        }

        dto.setFechaRegistro(pago.getFechaRegistro());
        dto.setMonto(pago.getMonto());
        dto.setNumeroOperacion(pago.getNumeroOperacion());
        dto.setAnulado(pago.getAnulado());
        dto.setMotivoAnulacion(pago.getMotivoAnulacion());
        dto.setSaldoRestante(saldoRestante);
        dto.setNuevoEstadoCobro(nuevoEstadoCobro);

        return dto;
    }

    private String obtenerNombreCliente(Comprobante comprobante) {
        if (comprobante == null || comprobante.getVenta() == null) {
            return "";
        }
        Venta venta = comprobante.getVenta();
        if (venta.getComprador() != null) {
            String nombres = venta.getComprador().getNombres() != null ? venta.getComprador().getNombres().trim() : "";
            String apellidos = venta.getComprador().getApellidos() != null ? venta.getComprador().getApellidos().trim() : "";
            return (nombres + " " + apellidos).trim();
        }
        if (venta.getPaciente() != null && venta.getPaciente().getPersona() != null) {
            String nombres = venta.getPaciente().getPersona().getNombres() != null ? venta.getPaciente().getPersona().getNombres().trim() : "";
            String apellidos = venta.getPaciente().getPersona().getApellidos() != null ? venta.getPaciente().getPersona().getApellidos().trim() : "";
            return (nombres + " " + apellidos).trim();
        }
        return "";
    }

    private String obtenerNombreEmpleado(Empleado empleado) {
        if (empleado == null || empleado.getPersona() == null) {
            return null;
        }
        String nombres = empleado.getPersona().getNombres() != null ? empleado.getPersona().getNombres().trim() : "";
        String apellidos = empleado.getPersona().getApellidos() != null ? empleado.getPersona().getApellidos().trim() : "";
        String completo = (nombres + " " + apellidos).trim();
        return completo.isEmpty() ? null : completo;
    }
}

package pe.edu.upeu.geovisionBackEnd.mapper.ventaProductosServicios;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.ItemDetalleResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.VentaDetalleResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.ventaProductosServicios.VentaResumenDTO;
import pe.edu.upeu.geovisionBackEnd.entity.atencionPacientesConsulta.Receta;
import pe.edu.upeu.geovisionBackEnd.entity.elaboracionOrdenTrabajo.OrdenTrabajo;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.Comprobante;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.ItemVendido;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.Pago;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.ServicioVendido;
import pe.edu.upeu.geovisionBackEnd.entity.ventaProductosServicios.Venta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class VentaHistorialMapper {

    public VentaResumenDTO toVentaResumenDTO(Venta venta, String dni, BigDecimal totalPagado, BigDecimal saldoPendiente, String estadoVisual) {
        if (venta == null) {
            return null;
        }

        String clienteNombre = obtenerNombreCliente(venta);
        String serieNumero = obtenerSerieNumero(venta.getComprobante());
        String tipoVenta = determinarTipoVenta(venta);

        return VentaResumenDTO.builder()
                .idVenta(venta.getIdVenta())
                .comprobanteSerieNumero(serieNumero)
                .fechaRegistro(venta.getFechaRegistro())
                .clienteNombre(clienteNombre)
                .clienteDni(dni != null && !dni.isBlank() ? dni : "-")
                .clienteIniciales(obtenerIniciales(clienteNombre))
                .tipoVenta(tipoVenta)
                .total(venta.getTotal() != null ? venta.getTotal() : BigDecimal.ZERO)
                .estadoVisual(estadoVisual != null ? estadoVisual : "Completado")
                .estadoVenta(venta.getEstadoVenta() != null ? venta.getEstadoVenta().getNombre() : "")
                .estadoCobro(venta.getComprobante() != null ? venta.getComprobante().getEstadoCobro() : "")
                .totalPagado(totalPagado != null ? totalPagado : BigDecimal.ZERO)
                .saldoPendiente(saldoPendiente != null ? saldoPendiente : BigDecimal.ZERO)
                .build();
    }

    public VentaDetalleResponseDTO toVentaDetalleResponseDTO(Venta venta, BigDecimal totalPagado, BigDecimal saldoPendiente,
                                                            OrdenTrabajo ordenTrabajo, String metodoCobro, String estadoVisual) {
        if (venta == null) {
            return null;
        }

        String serieNumero = obtenerSerieNumero(venta.getComprobante());
        String clienteNombre = obtenerNombreCliente(venta);
        String tipoVenta = determinarTipoVenta(venta);
        LocalDateTime fechaEmision = venta.getComprobante() != null && venta.getComprobante().getFechaEmision() != null
                ? venta.getComprobante().getFechaEmision().atStartOfDay()
                : venta.getFechaRegistro();

        // Mapear items vendidos (productos, lentes) y servicios vendidos
        List<ItemDetalleResponseDTO> items = new ArrayList<>();
        if (venta.getItemsVendidos() != null) {
            for (ItemVendido iv : venta.getItemsVendidos()) {
                String desc = iv.getDescripcion() != null && !iv.getDescripcion().isBlank()
                        ? iv.getDescripcion()
                        : "Producto Óptico";
                String subtexto = iv.getLente() != null ? "Lente oftálmico a medida" : "Artículo óptico";

                items.add(ItemDetalleResponseDTO.builder()
                        .descripcion(desc)
                        .subtexto(subtexto)
                        .cantidad(iv.getCantidad() != null ? iv.getCantidad() : 1)
                        .precioUnit(iv.getPrecioUnit() != null ? iv.getPrecioUnit() : BigDecimal.ZERO)
                        .total(iv.getSubTotal() != null ? iv.getSubTotal() : BigDecimal.ZERO)
                        .build());
            }
        }

        if (venta.getServiciosVendidos() != null) {
            for (ServicioVendido sv : venta.getServiciosVendidos()) {
                String desc = sv.getServicio() != null && sv.getServicio().getNombre() != null
                        ? sv.getServicio().getNombre()
                        : (sv.getDescripcion() != null ? sv.getDescripcion() : "Servicio Óptico");

                BigDecimal subtotal = sv.getSubTotal();
                if (subtotal == null && sv.getPrecio() != null && sv.getCantidad() != null) {
                    subtotal = sv.getPrecio().multiply(BigDecimal.valueOf(sv.getCantidad()));
                }

                items.add(ItemDetalleResponseDTO.builder()
                        .descripcion(desc)
                        .subtexto("Servicio clínico especializado")
                        .cantidad(sv.getCantidad() != null ? sv.getCantidad() : 1)
                        .precioUnit(sv.getPrecio() != null ? sv.getPrecio() : BigDecimal.ZERO)
                        .total(subtotal != null ? subtotal : BigDecimal.ZERO)
                        .build());
            }
        }

        // Información de receta médica
        String recetaCodigo = null;
        Long recetaId = null;
        if (venta.getReceta() != null) {
            Receta r = venta.getReceta();
            recetaId = r.getIdReceta();
            String prefix = (r.getEsExterno() != null && r.getEsExterno() == 1) ? "Externa #RC-" : "Interna #RC-";
            recetaCodigo = prefix + String.format("%03d", r.getIdReceta());
        }

        // Taller y orden de trabajo
        LocalDate fechaEntrega = null;
        String numeroOrden = null;
        boolean tieneOT = ordenTrabajo != null;
        if (ordenTrabajo != null) {
            numeroOrden = ordenTrabajo.getNumeroOrden();
            if (ordenTrabajo.getFechaEntregaEstimada() != null) {
                fechaEntrega = ordenTrabajo.getFechaEntregaEstimada().toLocalDate();
            }
        }

        BigDecimal total = venta.getTotal() != null ? venta.getTotal() : BigDecimal.ZERO;
        BigDecimal adelanto = totalPagado != null ? totalPagado : BigDecimal.ZERO;
        BigDecimal saldo = saldoPendiente != null ? saldoPendiente : BigDecimal.ZERO;

        return VentaDetalleResponseDTO.builder()
                .idVenta(venta.getIdVenta())
                .comprobanteSerieNumero(serieNumero)
                .clienteNombre(clienteNombre)
                .fechaEmision(fechaEmision)
                .tipoVenta(tipoVenta)
                .recetaCodigo(recetaCodigo)
                .recetaId(recetaId)
                .estadoActual(estadoVisual != null ? estadoVisual : "Completado")
                .items(items)
                .metodoCobro(metodoCobro != null ? metodoCobro : "Efectivo")
                .fechaEntregaEstimada(fechaEntrega)
                .tieneOrdenTrabajo(tieneOT)
                .numeroOrdenTrabajo(numeroOrden)
                .subtotal(total)
                .total(total)
                .adelantoAbonado(adelanto)
                .saldoPendiente(saldo)
                .build();
    }

    public String determinarTipoVenta(Venta venta) {
        if (venta == null) {
            return "Venta directa";
        }
        if (venta.getReceta() != null) {
            return "Venta con receta";
        }
        boolean hasItems = venta.getItemsVendidos() != null && !venta.getItemsVendidos().isEmpty();
        boolean hasServicios = venta.getServiciosVendidos() != null && !venta.getServiciosVendidos().isEmpty();

        if (hasServicios && !hasItems) {
            return "Venta de servicio";
        }
        return "Venta directa";
    }

    public String determinarEstadoVisual(Venta venta, OrdenTrabajo ordenTrabajo) {
        if (venta == null) {
            return "Completado";
        }
        if (venta.getEstadoVenta() != null && "ANULADA".equalsIgnoreCase(venta.getEstadoVenta().getNombre())) {
            return "Cancelado";
        }
        if (venta.getComprobante() != null) {
            String estadoCobro = venta.getComprobante().getEstadoCobro();
            if ("PENDIENTE".equalsIgnoreCase(estadoCobro) || "CON_ADELANTO".equalsIgnoreCase(estadoCobro)) {
                return "Pago Pendiente";
            }
        }
        if (ordenTrabajo != null && ordenTrabajo.getFechaEntregaPaciente() == null) {
            return "En Laboratorio";
        }
        return "Completado";
    }

    public String resolverMetodoCobro(Comprobante comprobante) {
        if (comprobante == null || comprobante.getPagos() == null || comprobante.getPagos().isEmpty()) {
            return "Efectivo";
        }
        // Obtener el primer pago válido
        Pago primerPago = comprobante.getPagos().stream()
                .filter(p -> p.getAnulado() == null || p.getAnulado() == 0)
                .findFirst()
                .orElse(null);

        if (primerPago != null && primerPago.getMetodoPago() != null) {
            String metodo = primerPago.getMetodoPago().getNombre();
            if (primerPago.getNumeroOperacion() != null && !primerPago.getNumeroOperacion().isBlank()) {
                metodo += " (Op. " + primerPago.getNumeroOperacion() + ")";
            }
            return metodo;
        }
        return "Efectivo";
    }

    public String obtenerNombreCliente(Venta venta) {
        if (venta == null) {
            return "Cliente General";
        }
        if (venta.getComprador() != null) {
            String nombres = venta.getComprador().getNombres() != null ? venta.getComprador().getNombres().trim() : "";
            String apellidos = venta.getComprador().getApellidos() != null ? venta.getComprador().getApellidos().trim() : "";
            String completo = (nombres + " " + apellidos).trim();
            if (!completo.isEmpty()) {
                return completo;
            }
        }
        if (venta.getPaciente() != null && venta.getPaciente().getPersona() != null) {
            String nombres = venta.getPaciente().getPersona().getNombres() != null ? venta.getPaciente().getPersona().getNombres().trim() : "";
            String apellidos = venta.getPaciente().getPersona().getApellidos() != null ? venta.getPaciente().getPersona().getApellidos().trim() : "";
            String completo = (nombres + " " + apellidos).trim();
            if (!completo.isEmpty()) {
                return completo;
            }
        }
        return "Cliente General";
    }

    public String obtenerSerieNumero(Comprobante comprobante) {
        if (comprobante == null) {
            return "-";
        }
        String serie = comprobante.getSerie() != null ? comprobante.getSerie().trim() : "";
        String numero = comprobante.getNumero() != null ? comprobante.getNumero().trim() : "";
        if (serie.isEmpty() && numero.isEmpty()) {
            return "-";
        }
        if (serie.isEmpty()) {
            return numero;
        }
        if (numero.isEmpty()) {
            return serie;
        }
        return serie + "-" + numero;
    }

    public String obtenerIniciales(String nombre) {
        if (nombre == null || nombre.isBlank() || "Cliente General".equalsIgnoreCase(nombre)) {
            return "CG";
        }
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length == 1) {
            return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
        }
        return (partes[0].substring(0, 1) + partes[1].substring(0, 1)).toUpperCase();
    }
}

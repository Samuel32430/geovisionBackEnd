package pe.edu.upeu.geovision_backend.mapper.ventas;

import pe.edu.upeu.geovision_backend.dto.ventas.ProductoVendidoDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.ServicioVendidoDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.VentaHistorialResponseDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.VentaRegistroRequestDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.VentaResponseDTO;
import pe.edu.upeu.geovision_backend.entity.security.Empleado;
import pe.edu.upeu.geovision_backend.entity.atencion.Paciente;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.entity.ventas.ProductoVendido;
import pe.edu.upeu.geovision_backend.entity.atencion.Receta;
import pe.edu.upeu.geovision_backend.entity.ventas.ServicioVendido;
import pe.edu.upeu.geovision_backend.entity.ventas.Venta;
import pe.edu.upeu.geovision_backend.enums.ventas.EstadoVentaEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class VentaMapper {
    private VentaMapper() {}

    public static Venta toEntity(
            VentaRegistroRequestDTO dto,
            Empleado empleado,
            Paciente paciente,
            Receta receta,
            EstadoVentaEnum estado) {

        if (dto == null) {
            return null;
        }

        return new Venta(null, LocalDateTime.now(), dto.total(), estado, dto.numeroComprobante() != null ? dto.numeroComprobante().trim() : null, dto.tipoComprobante(), empleado, paciente, receta);
    }

    public static VentaResponseDTO toResponseDTO(
            Venta venta,
            BigDecimal totalPagado,
            BigDecimal saldoPendiente,
            List<ProductoVendido> productos,
            List<ServicioVendido> servicios,
            List<pe.edu.upeu.geovision_backend.entity.ventas.Pago> pagos) {

        if (venta == null) {
            return null;
        }

        String nombreCompletoEmpleado = null;
        if (venta.getEmpleado() != null && venta.getEmpleado().getPersona() != null) {
            Persona p = venta.getEmpleado().getPersona();
            nombreCompletoEmpleado = (p.getNombres() + " " + p.getApellidos()).trim();
        }

        String nombreCompletoPaciente = null;
        Long idPaciente = null;
        if (venta.getPaciente() != null) {
            idPaciente = venta.getPaciente().getId();
            if (venta.getPaciente().getPersona() != null) {
                Persona p = venta.getPaciente().getPersona();
                nombreCompletoPaciente = (p.getNombres() + " " + p.getApellidos()).trim();
            }
        }

        List<ProductoVendidoDTO> productoDTOs = (productos == null || productos.isEmpty())
                ? Collections.emptyList()
                : productos.stream().map(VentaMapper::toProductoVendidoDTO).collect(Collectors.toList());

        List<ServicioVendidoDTO> servicioDTOs = (servicios == null || servicios.isEmpty())
                ? Collections.emptyList()
                : servicios.stream().map(VentaMapper::toServicioVendidoDTO).collect(Collectors.toList());

        return new VentaResponseDTO(venta.getId(), venta.getFechaRegistro(), venta.getTotal(), totalPagado != null ? totalPagado : BigDecimal.ZERO, saldoPendiente != null ? saldoPendiente : BigDecimal.ZERO, venta.getEstado(), venta.getEstado() != null ? venta.getEstado().getDescripcion() : null, venta.getTipoComprobante(), venta.getTipoComprobante() != null ? venta.getTipoComprobante().getDescripcion() : null, venta.getNumeroComprobante(), venta.getEmpleado() != null ? venta.getEmpleado().getId() : null, nombreCompletoEmpleado, idPaciente, nombreCompletoPaciente, venta.getReceta() != null ? venta.getReceta().getId() : null, productoDTOs, servicioDTOs, PagoMapper.toDTOList(pagos));
    }

    public static VentaHistorialResponseDTO toHistorialResponseDTO(
            Venta venta,
            BigDecimal totalPagado,
            BigDecimal saldoPendiente,
            Integer cantidadItems) {

        if (venta == null) {
            return null;
        }

        String clienteNombre = null;
        String clienteDocumento = null;
        if (venta.getPaciente() != null && venta.getPaciente().getPersona() != null) {
            Persona p = venta.getPaciente().getPersona();
            clienteNombre = (p.getNombres() + " " + p.getApellidos()).trim();
            clienteDocumento = p.getNumeroDocumento();
        }

        String vendedorNombre = null;
        if (venta.getEmpleado() != null && venta.getEmpleado().getPersona() != null) {
            Persona p = venta.getEmpleado().getPersona();
            vendedorNombre = (p.getNombres() + " " + p.getApellidos()).trim();
        }

        return new VentaHistorialResponseDTO(venta.getId(), venta.getFechaRegistro(), venta.getNumeroComprobante(), venta.getTipoComprobante(), venta.getTipoComprobante() != null ? venta.getTipoComprobante().getDescripcion() : null, clienteNombre, clienteDocumento, vendedorNombre, venta.getTotal(), totalPagado != null ? totalPagado : BigDecimal.ZERO, saldoPendiente != null ? saldoPendiente : BigDecimal.ZERO, venta.getEstado(), venta.getEstado() != null ? venta.getEstado().getDescripcion() : null, venta.getReceta() != null, cantidadItems != null ? cantidadItems : 0);
    }

    public static ProductoVendidoDTO toProductoVendidoDTO(ProductoVendido entity) {
        if (entity == null) {
            return null;
        }
        return new ProductoVendidoDTO(entity.getId(), entity.getProducto() != null ? entity.getProducto().getId() : null, entity.getProducto() != null ? entity.getProducto().getCodigo() : null, entity.getProducto() != null ? entity.getProducto().getNombre() : null, entity.getCantidad(), entity.getPrecioUnit(), entity.getSubTotal());
    }

    public static ServicioVendidoDTO toServicioVendidoDTO(ServicioVendido entity) {
        if (entity == null) {
            return null;
        }
        return new ServicioVendidoDTO(entity.getId(), entity.getServicio() != null ? entity.getServicio().getId() : null, entity.getServicio() != null ? entity.getServicio().getNombre() : null, entity.getPrecio());
    }
}

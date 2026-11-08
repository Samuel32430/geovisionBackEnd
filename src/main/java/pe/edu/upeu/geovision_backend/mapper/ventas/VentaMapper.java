package pe.edu.upeu.geovision_backend.mapper.ventas;

import org.springframework.stereotype.Component;
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

@Component
public class VentaMapper {

    private final PagoMapper pagoMapper;

    public VentaMapper(PagoMapper pagoMapper) {
        this.pagoMapper = pagoMapper;
    }

    public Venta toEntity(
            VentaRegistroRequestDTO dto,
            Empleado empleado,
            Paciente paciente,
            Receta receta,
            EstadoVentaEnum estado) {

        if (dto == null) {
            return null;
        }

        return Venta.builder()
                .total(dto.total())
                .estado(estado)
                .tipoComprobante(dto.tipoComprobante())
                .numeroComprobante(dto.numeroComprobante() != null ? dto.numeroComprobante().trim() : null)
                .empleado(empleado)
                .paciente(paciente)
                .receta(receta)
                .fechaRegistro(LocalDateTime.now())
                .build();
    }

    public VentaResponseDTO toResponseDTO(
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
            idPaciente = venta.getPaciente().getIdPersona();
            if (venta.getPaciente().getPersona() != null) {
                Persona p = venta.getPaciente().getPersona();
                nombreCompletoPaciente = (p.getNombres() + " " + p.getApellidos()).trim();
            }
        }

        List<ProductoVendidoDTO> productoDTOs = (productos == null || productos.isEmpty())
                ? Collections.emptyList()
                : productos.stream().map(this::toProductoVendidoDTO).collect(Collectors.toList());

        List<ServicioVendidoDTO> servicioDTOs = (servicios == null || servicios.isEmpty())
                ? Collections.emptyList()
                : servicios.stream().map(this::toServicioVendidoDTO).collect(Collectors.toList());

        return VentaResponseDTO.builder()
                .idVenta(venta.getIdVenta())
                .fechaRegistro(venta.getFechaRegistro())
                .total(venta.getTotal())
                .totalPagado(totalPagado != null ? totalPagado : BigDecimal.ZERO)
                .saldoPendiente(saldoPendiente != null ? saldoPendiente : BigDecimal.ZERO)
                .estado(venta.getEstado())
                .estadoDescripcion(venta.getEstado() != null ? venta.getEstado().getDescripcion() : null)
                .tipoComprobante(venta.getTipoComprobante())
                .tipoComprobanteDescripcion(venta.getTipoComprobante() != null ? venta.getTipoComprobante().getDescripcion() : null)
                .numeroComprobante(venta.getNumeroComprobante())
                .idEmpleado(venta.getEmpleado() != null ? venta.getEmpleado().getIdPersona() : null)
                .nombreCompletoEmpleado(nombreCompletoEmpleado)
                .idPaciente(idPaciente)
                .nombreCompletoPaciente(nombreCompletoPaciente)
                .idReceta(venta.getReceta() != null ? venta.getReceta().getIdReceta() : null)
                .productos(productoDTOs)
                .servicios(servicioDTOs)
                .pagos(pagoMapper.toDTOList(pagos))
                .build();
    }

    public VentaHistorialResponseDTO toHistorialResponseDTO(
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

        return VentaHistorialResponseDTO.builder()
                .idVenta(venta.getIdVenta())
                .fechaRegistro(venta.getFechaRegistro())
                .numeroComprobante(venta.getNumeroComprobante())
                .tipoComprobante(venta.getTipoComprobante())
                .tipoComprobanteDescripcion(venta.getTipoComprobante() != null ? venta.getTipoComprobante().getDescripcion() : null)
                .clienteNombre(clienteNombre)
                .clienteDocumento(clienteDocumento)
                .vendedorNombre(vendedorNombre)
                .total(venta.getTotal())
                .totalPagado(totalPagado != null ? totalPagado : BigDecimal.ZERO)
                .saldoPendiente(saldoPendiente != null ? saldoPendiente : BigDecimal.ZERO)
                .estado(venta.getEstado())
                .estadoDescripcion(venta.getEstado() != null ? venta.getEstado().getDescripcion() : null)
                .tieneReceta(venta.getReceta() != null)
                .cantidadItems(cantidadItems != null ? cantidadItems : 0)
                .build();
    }

    public ProductoVendidoDTO toProductoVendidoDTO(ProductoVendido entity) {
        if (entity == null) {
            return null;
        }
        return ProductoVendidoDTO.builder()
                .idProductoVendido(entity.getIdProductoVendido())
                .idProducto(entity.getProducto() != null ? entity.getProducto().getIdProducto() : null)
                .codigoProducto(entity.getProducto() != null ? entity.getProducto().getCodigo() : null)
                .nombreProducto(entity.getProducto() != null ? entity.getProducto().getNombre() : null)
                .cantidad(entity.getCantidad())
                .precioUnit(entity.getPrecioUnit())
                .subTotal(entity.getSubTotal())
                .build();
    }

    public ServicioVendidoDTO toServicioVendidoDTO(ServicioVendido entity) {
        if (entity == null) {
            return null;
        }
        return ServicioVendidoDTO.builder()
                .idServicioVendido(entity.getIdServicioVendido())
                .idServicio(entity.getServicio() != null ? entity.getServicio().getIdServicio() : null)
                .nombreServicio(entity.getServicio() != null ? entity.getServicio().getNombre() : null)
                .precio(entity.getPrecio())
                .build();
    }
}

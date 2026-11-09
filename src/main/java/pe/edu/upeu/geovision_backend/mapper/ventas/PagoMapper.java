package pe.edu.upeu.geovision_backend.mapper.ventas;

import pe.edu.upeu.geovision_backend.dto.ventas.PagoPendienteResponseDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoRegistroRequestDTO;
import pe.edu.upeu.geovision_backend.dto.ventas.PagoResponseDTO;
import pe.edu.upeu.geovision_backend.entity.ventas.Pago;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;
import pe.edu.upeu.geovision_backend.entity.ventas.Venta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class PagoMapper {
    private PagoMapper() {}

    public static Pago toEntity(PagoRegistroRequestDTO dto, Venta venta) {
        if (dto == null) {
            return null;
        }

        return new Pago(null, LocalDateTime.now(), dto.numeroOperacion() != null ? dto.numeroOperacion().trim() : null, dto.monto(), dto.metodoPago(), venta);
    }

    public static PagoResponseDTO toDTO(Pago entity) {
        if (entity == null) {
            return null;
        }

        return new PagoResponseDTO(entity.getId(), entity.getVenta() != null ? entity.getVenta().getId() : null, entity.getMonto(), entity.getMetodoPago(), entity.getMetodoPago() != null ? entity.getMetodoPago().getDescripcion() : null, entity.getNumeroOperacion(), entity.getFechaRegistro());
    }

    public static List<PagoResponseDTO> toDTOList(List<Pago> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(PagoMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static PagoPendienteResponseDTO toPagoPendienteResponseDTO(
        Venta venta,
        BigDecimal totalPagado,
        BigDecimal saldoPendiente,
        List<Pago> pagos
    ) {

        if (venta == null) {
            return null;
        }

        String pacienteNombre = null;
        String pacienteTelefono = null;
        String pacienteDocumento = null;
        Long idPaciente = null;

        if (venta.getPaciente() != null) {
            idPaciente = venta.getPaciente().getId();
            Persona persona = venta.getPaciente().getPersona();
            if (persona != null) {
                pacienteNombre = (persona.getNombres() + " " + persona.getApellidos()).trim();
                pacienteTelefono = persona.getTelefono();
                pacienteDocumento = persona.getNumeroDocumento();
            }
        }

        return new PagoPendienteResponseDTO(venta.getId(), venta.getNumeroComprobante(), venta.getFechaRegistro(), idPaciente, pacienteNombre, pacienteTelefono, pacienteDocumento, venta.getTotal(), totalPagado != null ? totalPagado : BigDecimal.ZERO, saldoPendiente != null ? saldoPendiente : BigDecimal.ZERO, venta.getEstado(), toDTOList(pagos));
    }
}

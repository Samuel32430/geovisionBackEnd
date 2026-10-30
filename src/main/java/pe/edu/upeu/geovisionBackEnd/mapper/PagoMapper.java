package pe.edu.upeu.geovisionBackEnd.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovisionBackEnd.dto.PagoPendienteResponseDTO;
import pe.edu.upeu.geovisionBackEnd.dto.PagoRegistroRequestDTO;
import pe.edu.upeu.geovisionBackEnd.dto.PagoResponseDTO;
import pe.edu.upeu.geovisionBackEnd.entity.Pago;
import pe.edu.upeu.geovisionBackEnd.entity.Persona;
import pe.edu.upeu.geovisionBackEnd.entity.Venta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PagoMapper {

    public Pago toEntity(PagoRegistroRequestDTO dto, Venta venta) {
        if (dto == null) {
            return null;
        }

        return Pago.builder()
                .venta(venta)
                .monto(dto.getMonto())
                .metodoPago(dto.getMetodoPago())
                .numeroOperacion(dto.getNumeroOperacion() != null ? dto.getNumeroOperacion().trim() : null)
                .fechaRegistro(LocalDateTime.now())
                .build();
    }

    public PagoResponseDTO toDTO(Pago entity) {
        if (entity == null) {
            return null;
        }

        return PagoResponseDTO.builder()
                .idPago(entity.getIdPago())
                .idVenta(entity.getVenta() != null ? entity.getVenta().getIdVenta() : null)
                .monto(entity.getMonto())
                .metodoPago(entity.getMetodoPago())
                .metodoPagoDescripcion(entity.getMetodoPago() != null ? entity.getMetodoPago().getDescripcion() : null)
                .numeroOperacion(entity.getNumeroOperacion())
                .fechaRegistro(entity.getFechaRegistro())
                .build();
    }

    public List<PagoResponseDTO> toDTOList(List<Pago> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PagoPendienteResponseDTO toPagoPendienteResponseDTO(
            Venta venta,
            BigDecimal totalPagado,
            BigDecimal saldoPendiente,
            List<Pago> pagos) {

        if (venta == null) {
            return null;
        }

        String pacienteNombre = null;
        String pacienteTelefono = null;
        String pacienteDocumento = null;
        Long idPaciente = null;

        if (venta.getPaciente() != null) {
            idPaciente = venta.getPaciente().getIdPersona();
            Persona persona = venta.getPaciente().getPersona();
            if (persona != null) {
                pacienteNombre = (persona.getNombres() + " " + persona.getApellidos()).trim();
                pacienteTelefono = persona.getTelefono();
                pacienteDocumento = persona.getNumeroDocumento();
            }
        }

        return PagoPendienteResponseDTO.builder()
                .idVenta(venta.getIdVenta())
                .numeroComprobante(venta.getNumeroComprobante())
                .fechaVenta(venta.getFechaRegistro())
                .idPaciente(idPaciente)
                .pacienteNombre(pacienteNombre)
                .pacienteTelefono(pacienteTelefono)
                .pacienteDocumento(pacienteDocumento)
                .totalVenta(venta.getTotal())
                .totalPagado(totalPagado != null ? totalPagado : BigDecimal.ZERO)
                .saldoPendiente(saldoPendiente != null ? saldoPendiente : BigDecimal.ZERO)
                .estado(venta.getEstado())
                .historialPagos(toDTOList(pagos))
                .build();
    }
}

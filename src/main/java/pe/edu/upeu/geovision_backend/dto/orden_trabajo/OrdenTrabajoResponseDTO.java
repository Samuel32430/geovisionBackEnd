package pe.edu.upeu.geovision_backend.dto.orden_trabajo;

import pe.edu.upeu.geovision_backend.dto.atencion.LenteResponseDTO;

import pe.edu.upeu.geovision_backend.enums.orden_trabajo.EstadoOrdenTrabajoEnum;

import java.time.LocalDateTime;
import java.util.List;

public record OrdenTrabajoResponseDTO(
    Long id,
    LocalDateTime fechaRegistro,
    LocalDateTime fechaEntrega,
    Long idVenta,
    java.math.BigDecimal saldoPendiente,
    LocalDateTime fechaNotificacion,
    EstadoOrdenTrabajoEnum estado,
    String estadoDescripcion,
    String descripcion,
    Long idLente,
    LenteResponseDTO lente,
    Long idPaciente,
    String nombrePaciente,
    String telefonoPaciente,
    List<EnvioLaboratorioResponseDTO> envios
) {
}

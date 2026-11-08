package pe.edu.upeu.geovision_backend.dto.orden_trabajo;

import pe.edu.upeu.geovision_backend.dto.atencion.MedidaResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record EnvioLaboratorioResponseDTO(
    Long id,
    Long idOrdenTrabajo,
    Long idLaboratorio,
    String nombreLaboratorio,
    LocalDateTime fechaEnvio,
    LocalDate fechaRecepcionEstimada,
    LocalDateTime fechaRecepcion,
    LocalDateTime fechaRevision,
    BigDecimal tolerancia,
    Boolean aprobado,
    String observaciones,
    List<MedidaResponseDTO> medidasControl
) {
}

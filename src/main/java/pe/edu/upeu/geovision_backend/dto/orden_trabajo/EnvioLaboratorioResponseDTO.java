package pe.edu.upeu.geovision_backend.dto.orden_trabajo;

import lombok.Builder;
import pe.edu.upeu.geovision_backend.dto.atencion.MedidaResponseDTO;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
public record EnvioLaboratorioResponseDTO(
    Long idEnvioLaboratorio,
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

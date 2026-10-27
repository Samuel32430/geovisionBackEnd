package pe.edu.upeu.geovisionBackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvioLaboratorioResponseDTO {

    private Long idEnvioLaboratorio;
    private Long idOrdenTrabajo;
    private Long idLaboratorio;
    private String nombreLaboratorio;
    private LocalDateTime fechaEnvio;
    private LocalDate fechaRecepcionEstimada;
    private LocalDateTime fechaRecepcion;
    private LocalDateTime fechaRevision;
    private BigDecimal tolerancia;
    private Boolean aprobado;
    private String observaciones;
    private List<MedidaResponseDTO> medidasControl;
}

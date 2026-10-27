package pe.edu.upeu.geovisionBackEnd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.geovisionBackEnd.enums.EstadoOrdenTrabajoEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenTrabajoResponseDTO {

    private Long idOrdenTrabajo;
    private LocalDateTime fechaRegistro;
    private LocalDate fechaEntrega;
    private LocalDateTime fechaNotificacion;
    private EstadoOrdenTrabajoEnum estado;
    private String estadoDescripcion;
    private String descripcion;
    private Long idLente;
    private LenteResponseDTO lente;
    private Long idPaciente;
    private String nombrePaciente;
    private String telefonoPaciente;
    private List<EnvioLaboratorioResponseDTO> envios;
}

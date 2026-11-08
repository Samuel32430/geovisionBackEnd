package pe.edu.upeu.geovision_backend.dto.orden_trabajo;

import pe.edu.upeu.geovision_backend.dto.atencion.LenteResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.geovision_backend.enums.orden_trabajo.EstadoOrdenTrabajoEnum;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenTrabajoResponseDTO {

    private Long idOrdenTrabajo;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaEntrega;
    private Long idVenta;
    private java.math.BigDecimal saldoPendiente;
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

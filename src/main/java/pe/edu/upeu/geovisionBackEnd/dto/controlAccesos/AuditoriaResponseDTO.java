package pe.edu.upeu.geovisionBackEnd.dto.controlAccesos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaResponseDTO {

    private Long idAuditoria;
    private LocalDateTime fechaLog;
    private String entidad;
    private Long idEntidad;
    private String accion;
    private String datosAnteriores;
    private String datosNuevos;
    private Long idUsuario;
    private String nombreUsuario;
}

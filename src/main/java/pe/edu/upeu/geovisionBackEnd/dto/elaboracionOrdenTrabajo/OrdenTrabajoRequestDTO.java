package pe.edu.upeu.geovisionBackEnd.dto.elaboracionOrdenTrabajo;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenTrabajoRequestDTO {

    @NotBlank(message = "numeroOrden es obligatorio")
    @Size(max = 50, message = "numeroOrden no puede superar 50 caracteres")
    private String numeroOrden;

    private LocalDateTime fechaEntregaEstimada;

    private LocalDateTime fechaEntregaPaciente;

    private LocalDateTime fechaNotificacion;

    @NotNull(message = "esUrgente es obligatorio")
    private Boolean esUrgente = false;

    @Size(max = 2000, message = "descripcion no puede superar 2000 caracteres")
    private String descripcion;

    private LocalDateTime fechaEntregaReal;

    @NotNull(message = "idLente es obligatorio")
    @Positive(message = "idLente debe ser positivo")
    private Long idLente;

    @NotNull(message = "idEmpleado es obligatorio")
    @Positive(message = "idEmpleado debe ser positivo")
    private Long idEmpleado;

    @NotNull(message = "idEstadoOrdenTrabajo es obligatorio")
    @Positive(message = "idEstadoOrdenTrabajo debe ser positivo")
    private Long idEstadoOrdenTrabajo;
}

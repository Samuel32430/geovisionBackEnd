package pe.edu.upeu.geovisionBackEnd.dto.elaboracionOrdenTrabajo;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/** Creacion: el backend fija el registro, el empleado autenticado y el estado inicial. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenTrabajoRequestDTO {

    @NotBlank(message = "numeroOrden es obligatorio")
    @Size(max = 50)
    private String numeroOrden;

    @FutureOrPresent(message = "La fecha estimada no puede ser anterior al momento de creacion")
    private LocalDateTime fechaEntregaEstimada;

    @NotNull(message = "esUrgente es obligatorio")
    private Boolean esUrgente = false;

    @Size(max = 2000)
    private String descripcion;

    @NotNull(message = "idLente es obligatorio")
    @Positive(message = "idLente debe ser positivo")
    private Long idLente;
}

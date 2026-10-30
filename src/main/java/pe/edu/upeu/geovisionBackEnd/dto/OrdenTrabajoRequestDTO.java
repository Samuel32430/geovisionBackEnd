package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenTrabajoRequestDTO {

    @NotNull(message = "El ID del lente es obligatorio")
    private Long idLente;

    @Size(max = 500, message = "La descripción no debe superar los 500 caracteres")
    private String descripcion;

    private LocalDate fechaEntregaEstimada;
}

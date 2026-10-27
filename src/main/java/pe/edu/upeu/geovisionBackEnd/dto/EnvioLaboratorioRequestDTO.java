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
public class EnvioLaboratorioRequestDTO {

    @NotNull(message = "El ID de la orden de trabajo es obligatorio")
    private Long idOrdenTrabajo;

    @NotNull(message = "El ID del laboratorio es obligatorio")
    private Long idLaboratorio;

    private LocalDate fechaRecepcionEstimada;

    @Size(max = 500, message = "Las observaciones no deben superar los 500 caracteres")
    private String observaciones;
}

package pe.edu.upeu.geovisionBackEnd.dto.controlAccesos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoRequestDTO {

    @NotNull(message = "El identificador de persona es obligatorio")
    @Positive(message = "El identificador de persona debe ser positivo")
    private Long idPersona;

    @NotNull(message = "El identificador de cargo es obligatorio")
    @Positive(message = "El identificador de cargo debe ser positivo")
    private Long idCargo;

    private LocalDate fechaContrato;

    @Min(value = 0, message = "El estado debe ser 0 o 1")
    @Max(value = 1, message = "El estado debe ser 0 o 1")
    private Integer estado;

    @NotNull(message = "El identificador de usuario es obligatorio")
    @Positive(message = "El identificador de usuario debe ser positivo")
    private Long idUsuario;
}

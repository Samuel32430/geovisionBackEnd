package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PacienteRequestDTO {

    @Positive(message = "El ID de la persona debe ser positivo")
    private Long idPersona;

    @Valid
    private PersonaRequestDTO persona;

    @Builder.Default
    @Min(value = 0, message = "El estado debe ser 0 o 1")
    @Max(value = 1, message = "El estado debe ser 0 o 1")
    private Integer estado = 1;

    @Positive(message = "El ID del usuario debe ser positivo")
    private Long idUsuario;
}

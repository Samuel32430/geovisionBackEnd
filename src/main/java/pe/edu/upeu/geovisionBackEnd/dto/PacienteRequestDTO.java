package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PacienteRequestDTO {

    private Long idPersona;

    @Valid
    private PersonaRequestDTO persona;

    @Builder.Default
    private Integer estado = 1;

    private Long idUsuario;
}

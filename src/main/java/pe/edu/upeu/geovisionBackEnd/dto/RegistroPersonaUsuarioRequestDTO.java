package pe.edu.upeu.geovisionBackEnd.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroPersonaUsuarioRequestDTO {
    @Valid
    @NotNull(message = "Los datos de la persona son obligatorios")
    private PersonaRequestDTO persona;

    @Valid
    @NotNull(message = "Los datos de la cuenta son obligatorios")
    private CuentaUsuarioRegistroDTO usuario;

    @Valid
    @NotNull(message = "El alta de empleado y su fecha de contrato son obligatorias")
    private EmpleadoRegistroDTO empleado;
}

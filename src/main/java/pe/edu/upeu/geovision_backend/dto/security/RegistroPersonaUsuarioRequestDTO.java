package pe.edu.upeu.geovision_backend.dto.security;

import pe.edu.upeu.geovision_backend.dto.atencion.PersonaRequestDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record RegistroPersonaUsuarioRequestDTO(
    @Valid
    @NotNull(message = "Los datos de la persona son obligatorios")
    PersonaRequestDTO persona,
    @Valid
    @NotNull(message = "Los datos de la cuenta son obligatorios")
    CuentaUsuarioRegistroDTO usuario,
    @Valid
    @NotNull(message = "El alta de empleado y su fecha de contrato son obligatorias")
    EmpleadoRegistroDTO empleado
) {
}

package pe.edu.upeu.geovision_backend.mapper.atencion;

import pe.edu.upeu.geovision_backend.dto.atencion.PacienteResponseDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.PersonaRequestDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.PersonaResponseDTO;
import pe.edu.upeu.geovision_backend.entity.atencion.Paciente;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;

public final class PacienteMapper {
    private PacienteMapper() {}

    // >>>> MAPPING
    public static Persona toPersona(PersonaRequestDTO request) {
        return new Persona(null, request.nombres().trim(), request.apellidos().trim(), limpiar(request.telefono()),
                request.fechaNacimiento(), request.sexo(), limpiar(request.direccion()),
                request.numeroDocumento().trim(), request.tipoDocumento()
        );
    }

    public static void actualizarPersona(Persona persona, PersonaRequestDTO request) {
        persona.setNombres(request.nombres().trim());
        persona.setApellidos(request.apellidos().trim());
        persona.setTelefono(limpiar(request.telefono()));
        persona.setDireccion(limpiar(request.direccion()));
        persona.setFechaNacimiento(request.fechaNacimiento());
        persona.setSexo(request.sexo());
        persona.setTipoDocumento(request.tipoDocumento());
        persona.setNumeroDocumento(request.numeroDocumento().trim());
    }

    public static PacienteResponseDTO toPacienteResponse(Paciente paciente) {
        Persona persona = paciente.getPersona();
        PersonaResponseDTO datos = new PersonaResponseDTO(persona.getId(), persona.getNombres(),
                persona.getApellidos(), null, persona.getTelefono(), persona.getFechaNacimiento(), persona.getSexo(),
                persona.getSexo() == null ? null : persona.getSexo().getDescripcion(), persona.getDireccion(),
                persona.getTipoDocumento(), persona.getTipoDocumento().getDescripcion(), persona.getNumeroDocumento()
        );
        return new PacienteResponseDTO(paciente.getId(), datos, (paciente.getActivo() == null ? null : Boolean.TRUE.equals(paciente.getActivo()) ? 1 : 0), Boolean.TRUE.equals(paciente.getActivo()) ? "Activo" : "Inactivo", paciente.getFechaRegistro(), paciente.getUsuario() == null ? null : paciente.getUsuario().getId());
    }

    // >>>> HELPERS
    private static String limpiar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}

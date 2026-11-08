package pe.edu.upeu.geovision_backend.mapper.atencion;

import org.springframework.stereotype.Component;
import pe.edu.upeu.geovision_backend.dto.atencion.PacienteResponseDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.PersonaRequestDTO;
import pe.edu.upeu.geovision_backend.dto.atencion.PersonaResponseDTO;
import pe.edu.upeu.geovision_backend.entity.atencion.Paciente;
import pe.edu.upeu.geovision_backend.entity.atencion.Persona;

@Component
public class PacienteMapper {

    public Persona toPersona(PersonaRequestDTO request) {
        Persona persona = new Persona();
        actualizarPersona(persona, request);
        return persona;
    }

    public void actualizarPersona(Persona persona, PersonaRequestDTO request) {
        persona.setNombres(request.nombres().trim());
        persona.setApellidos(request.apellidos().trim());
        persona.setTelefono(limpiar(request.telefono()));
        persona.setDireccion(limpiar(request.direccion()));
        persona.setFechaNacimiento(request.fechaNacimiento());
        persona.setSexo(request.sexo());
        persona.setTipoDocumento(request.tipoDocumento());
        persona.setNumeroDocumento(request.numeroDocumento().trim());
    }

    public PacienteResponseDTO toPacienteResponse(Paciente paciente) {
        Persona persona = paciente.getPersona();
        PersonaResponseDTO datos = PersonaResponseDTO.builder()
                .idPersona(persona.getIdPersona())
                .nombres(persona.getNombres())
                .apellidos(persona.getApellidos())
                .telefono(persona.getTelefono())
                .direccion(persona.getDireccion())
                .fechaNacimiento(persona.getFechaNacimiento())
                .sexo(persona.getSexo())
                .sexoDescripcion(persona.getSexo() == null ? null : persona.getSexo().getDescripcion())
                .tipoDocumento(persona.getTipoDocumento())
                .tipoDocumentoDescripcion(persona.getTipoDocumento().getDescripcion())
                .numeroDocumento(persona.getNumeroDocumento())
                .build();
        return PacienteResponseDTO.builder()
                .idPersona(paciente.getIdPersona())
                .persona(datos)
                .estado((paciente.getActivo() == null ? null : Boolean.TRUE.equals(paciente.getActivo()) ? 1 : 0))
                .estadoDescripcion(Boolean.TRUE.equals(paciente.getActivo()) ? "Activo" : "Inactivo")
                .fechaRegistro(paciente.getFechaRegistro())
                .idUsuario(paciente.getUsuario() == null ? null : paciente.getUsuario().getIdUsuario())
                .build();
    }

    private String limpiar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}

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
        persona.setNombres(request.getNombres().trim());
        persona.setApellidos(request.getApellidos().trim());
        persona.setTelefono(limpiar(request.getTelefono()));
        persona.setDireccion(limpiar(request.getDireccion()));
        persona.setFechaNacimiento(request.getFechaNacimiento());
        persona.setSexo(request.getSexo());
        persona.setTipoDocumento(request.getTipoDocumento());
        persona.setNumeroDocumento(request.getNumeroDocumento().trim());
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
                .estado(paciente.getEstadoCodigo())
                .estadoDescripcion(paciente.getEstado().getDescripcion())
                .fechaRegistro(paciente.getFechaRegistro())
                .idUsuario(paciente.getUsuario() == null ? null : paciente.getUsuario().getIdUsuario())
                .build();
    }

    private String limpiar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
